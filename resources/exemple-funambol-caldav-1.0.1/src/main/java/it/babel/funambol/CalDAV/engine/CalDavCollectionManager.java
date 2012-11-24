/**
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * @author rpolli _at_ babel.it
 * @author pventura _at_ babel.it
 */

package it.babel.funambol.CalDAV.engine;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.PartStat;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.ResourceNotFoundException;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.GetMethod;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.util.CalendarComparator;
import org.osaf.caldav4j.util.ICalendarUtils;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;


/**
 * this class binds a CalendarCollection (a folder of events) to a Caldav Client
 * this should be the only entity used in the s4j module
 * @author rpolli@babel.it
 * @author pventura_at_babel.it
 */
public class CalDavCollectionManager extends CalDAVCalendarCollection {

	private BaseCaldavClient client = null;
	private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

	public static final String LOG_NAME = "caldav.engine";
	protected static FunambolLogger logger   =  FunambolLoggerFactory.getLogger(LOG_NAME);

	/**
	 * associates a CalendarCollection to a BaseCalDavClient, 
	 * pointing to a default folder
	 */
	public CalDavCollectionManager(BaseCaldavClient c){

		client=c;

		setHostConfiguration(c.hostConfig);
		setMethodFactory(methodFactory);

		setCalendarCollectionRoot();
	}


	public String getUsername() {
		return client.getCalDavSeverUsername();
	}

	public void deletePath(String path)
	{
		DeleteMethod delete = new DeleteMethod(path);
		try {
			client.executeMethod(getHostConfiguration(), delete);
		} catch (HttpException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}


	/**
	 * XXX assumes that URL is function of uid
	 * @param uid
	 * @throws CalDAV4JException
	 */
	public void deleteComponentByUid(String uid) 
	throws CalDAV4JException {
		if ((uid!=null)  && (! "".equals(uid))){
			deletePath(getCalendarCollectionRoot()+"/" +uid+".ics");
			return;
		}
		throw new CalDAV4JException("Item not found"+getCalendarCollectionRoot()+"/" +uid+".ics");
	}

	/**
	 * create new directory in path
	 * @param path
	 * @return 0 if ok statusCode on error
	 * @throws Exception
	 */
	public int mkDirectory(String path){
		MkCalendarMethod mk = new MkCalendarMethod();
		mk.setPath(path);

		try {
			client.executeMethod(getHostConfiguration(), mk);
		} catch (HttpException e) {
			logger.error(e);
		} catch (IOException e) {		
			logger.error(e);
		}

		int statusCode = mk.getStatusCode();
		return  ( statusCode == WebdavStatus.SC_CREATED)  ? 0 : statusCode;

	}

	/**
	 * list file and folder in path
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public  int listCalendar(String path) throws IOException {
		//now let's try and get it, make sure it's there
		GetMethod get = new GetMethod();
		get.setPath(path);

		try {
			client.executeMethod(getHostConfiguration(), get);
		} catch (HttpException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}

		int statusCode = get.getStatusCode();
		return  ( statusCode == WebdavStatus.SC_OK)  ? 0 : statusCode;

	}    



	/** 
	 * returns an event collection between two dates     
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws CalDAV4JException 
	 */
	public List<Calendar> getEventResources(Date beginDate, Date endDate)
	throws CalDAV4JException
	{
		CalDAV4JMethodFactory mf = new CalDAV4JMethodFactory();
		setMethodFactory(mf);

		return getEventResources(client, beginDate, endDate);
	}

	/**
	 * return event list in month/year
	 * @param year
	 * @param month
	 * @return
	 * @throws CalDAV4JException 
	 */
	public List<Calendar> getEventResourcesByMonth(int year, int month) throws CalDAV4JException {
		Date beginDate = ICalendarUtils.createDateTime(year, month, 0, null, true); // uses no timezone, and UTC

		GregorianCalendar c = new GregorianCalendar();
		c.setTime(beginDate);
		c.add(GregorianCalendar.MONTH, 1);

		Date endDate = ICalendarUtils.createDateTime(c.get(GregorianCalendar.YEAR),c.get(GregorianCalendar.MONTH), c.get(GregorianCalendar.DATE),null, true);

		return getEventResources(beginDate, endDate);
	}

	/** sort a Calendar list by date
	 * 
	 * @param calendars
	 * @return
	 */
	public List<Calendar> sortByStartDate(List<Calendar> calendars) {
		Collections.sort(calendars, new CalendarComparator());
		return calendars;

	}
	/**
	 * add event
	 * @param ve event
	 * @param VTimezone
	 * @return VEvent marked with an UID if success, null on error
	 * @throws CalDAV4JException 
	 */
	public VEvent addEvent(VEvent ve, VTimeZone vtz)
	throws CalDAV4JException  
	{

		if (ve.getProperty(Property.UID) == null) {
			Uid uid = new Uid( new DateTime().toString()
					+"-" + UUID.randomUUID().toString()
					+"-" + getUsername() );

			ve.getProperties().add(uid);
		}
		// logger.info("VEVENT>>>>>>>>>>>>>>>"+ve+"<<<<<<<<<<<<<<<");
		
		addEvent(client, ve, vtz);
		return ve;
	}
	/**
	 * edit event  
	 * @param ve
	 * @param vtz
	 * @return ve on success, null on failure
	 * @throws ValidationException 
	 */
	public VEvent editEvent(VEvent ve, VTimeZone vtz)
	throws ValidationException
	{
		try {
			updateMasterEvent(client, ve, vtz);
			return ve;
		} catch (CalDAV4JException e) {
			logger.error(e);
		}
		return null;
	}

	
	public void setCalendarCollectionRoot(String path) {
		if (path == null) {
			path = client.getCalDavSeverWebDAVRoot()+"/"+ getUsername()+"/";
		} 
		super.setCalendarCollectionRoot(path);
	}
	public void setCalendarCollectionRoot() {
		setCalendarCollectionRoot(null);
	}
	public String getCalendarCollectionRoot() {
		return super.getCalendarCollectionRoot();
	}

	public void setRelativePath(String path) {
		String home = client.getCalDavSeverWebDAVRoot()+"/"+getUsername()+"/";
		if (path == null) {
			path = home;
		} else {
			home += path;
		}

		super.setCalendarCollectionRoot(home);
	}

	/**
	 * @see super
	 * @param uid
	 * @return Calendar if object exists, null if not exists
	 * 
	 * @throws CalDAV4JException
	 */
	public Calendar getCalendarForEventUID(String uid)
	throws CalDAV4JException, ResourceNotFoundException {
		return super.getCalendarForEventUID(client, uid);
	}

	public List <String> getComponentProperty(String componentName, String propertyName, Date beginDate, Date endDate)
	throws CalDAV4JException 
	{
		return super.getComponentPropertyByTimestamp(client, componentName, propertyName,Property.DTSTAMP,  beginDate, endDate);
	}

	/**
	 * @see super
	 * @param propertyName
	 * @param beginDate
	 * @param endDate
	 * @throws CalDAV4JException
	 * @Deprecated  {@link getComponentPropertyByTimestamp} 
	 */
	public List <String> getEventPropertyByTimestamp(String propertyName, Date beginDate, Date endDate)
	throws CalDAV4JException 
	{
		return super.getEventPropertyByTimestamp(client, propertyName, beginDate, endDate);
	}

	/**
	 * see super
	 * @param componentName
	 * @param propertyName
	 * @param propertyFilter
	 * @param beginDate
	 * @param endDate
	 * @throws CalDAV4JException if can't connect
	 */
	public List <String> getComponentPropertyByTimestamp(String componentName, String propertyName, String propertyFilter, Date beginDate, Date endDate)
	throws CalDAV4JException 
	{
		return super.getComponentPropertyByTimestamp(client, componentName, propertyName, propertyFilter, beginDate, endDate);
	}

	/**
	 * 
	 * @param uid
	 * @return
	 * @throws CalDAV4JException if can't connect
	 * @throws ResourceNotFoundException if can't find object
	 * @see super
	 */
	public String getPathToResourceForEventId(String uid) 
	throws CalDAV4JException, ResourceNotFoundException {
		return  super.getPathToResourceForEventId(client, uid);
	}




	/********************* test test test ******************
	 * XXX Test method, this should go in another place
	 * @param args
	 * @throws CalDAV4JException
	 * @throws SocketException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) 
	throws CalDAV4JException, SocketException, URISyntaxException {

		BaseCaldavClient cli = new BaseCaldavClient();

		CalDavCollectionManager cdm = new CalDavCollectionManager(cli);
		// go to home
		cdm.setRelativePath("/calendar");

		Random r = new Random();
		int newDay = r.nextInt(30);

		// create date for event
		Date beginDate = ICalendarUtils.createDateTime(2007, 8, 9, newDay,0, null, true);
		Date endDate = ICalendarUtils.createDateTime(2007, 8, 7, null, true);
		Dur duration = new Dur("3H"); 

		// test for getByTimestamp
		Date startDTSTART = ICalendarUtils.createDateTime(2007, 5, 7, null, true);
		Date endDTSTART = ICalendarUtils.createDateTime(2007, 11, 10, null, true);

		// create new event
		VEvent nve = new VEvent(beginDate, duration, "Il mio nuovo evento");
		cdm.addEvent(nve, null);

		// modify event
		Description d = new Description("Spada");

		ParameterList pl = new ParameterList();
		pl.add(PartStat.ACCEPTED);
		Attendee invitato = new Attendee(pl, "mailto:rpolli@babel.it");


		ICalendarUtils.addOrReplaceProperty(nve, nve.getUid());
		ICalendarUtils.addOrReplaceProperty(nve, new Summary("new summary!"));
		ICalendarUtils.addOrReplaceProperty(nve, invitato);
		ICalendarUtils.addOrReplaceProperty(nve, invitato);
		ICalendarUtils.addOrReplaceProperty(nve, d);

		// re-get event
		Calendar pippo =  cdm.getCalendarForEventUID( ((HttpClient) cdm.client), nve.getUid().getValue());
		try {
			cdm.editEvent(nve, null);
		} catch (ValidationException e) {
			logger.error(e);
		}


		List <String> lc = cdm.getEventPropertyByTimestamp( cdm.client, Property.UID, beginDate, endDate);
		for (String cal : lc) {
			System.out.println( "UID="+cal);
		} 
	}




}