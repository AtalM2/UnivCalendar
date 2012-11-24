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
 * 
 */

package it.babel.funambol.CalDAV.engine;

import it.babel.funambol.CalDAV.helper.CalendarHelper;
import it.babel.funambol.CalDAV.util.TimeUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.LastModified;

import net.fortuna.ical4j.model.property.Uid;

import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.ResourceNotFoundException;
import org.osaf.caldav4j.model.request.PropFilter;
import org.osaf.caldav4j.util.ICalendarUtils;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * connect to caldav, throwing SyncSource exception if can't communicate with
 * server
 * 
 * @author <a href='mailto:rpolli _@ babel.it'>Roberto Polli</a>
 * @author pventura_at_babel.it
 * @version $Id$
 * 
 *          TODO instead of checking the connectivity with the server, manage
 *          the relative exception
 * */
public class CaldavInterface {

	public static final String LOG_NAME = "caldav.engine";

	private Integer caldavPort;
	private String caldavHost = null;
	private String username = null;
	private String password = null;
	private String baseUrl = null; // the base url of the caldav server (eg.
	// bedework "/ucaldav/user")

	protected CalDavCollectionManager cdm = null;
	protected VEventDAO dao = null; // needed to pass the logger to the dao

	protected FunambolLogger logger = null;
	protected TimeZone tz = null;

	
	/**
	 * Connecto to caldav server
	 * 
	 * @param host
	 *            the server hostname/ip
	 * @param port
	 *            the server port
	 * @param user
	 *            the username
	 * @param pass
	 *            the password
	 * @param base
	 *            the the base URL which stands before the username
	 * @param secure
	 *            Should the connection be secured
	 * @throws SyncSourceException
	 *             TODO could throw a can't connect exception
	 */
	public CaldavInterface(String host, String port, String user, String pass, String base, boolean secure) throws SyncSourceException {

		logger = FunambolLoggerFactory.getLogger(LOG_NAME);

		if (!(Integer.parseInt(port) > 0)) {
			caldavPort = secure ? 433 : 80;
		} else {
			caldavPort = Integer.parseInt(port);
		}

		logger.info("Connecting to " + host + ":" + caldavPort + " ssl:" + secure + " loginDN:" + user + " Password: ***");

		this.username = user;
		this.password = pass;
		this.baseUrl = base;
		this.caldavHost = host;

		try {
			// connect to caldavServer
			BaseCaldavClient caldavClient = new BaseCaldavClient(host, caldavPort.toString(), "http", baseUrl, user, pass);

			// create the connection manager, pointing to baseUrl/username
			cdm = new CalDavCollectionManager(caldavClient);
			logger.info("After caldavcollectionmanager");
			cdm.setRelativePath("calendar");

			logger.info("Created CalDavCollectionManager with path" + cdm.getCalendarCollectionRoot());
			dao = new VEventDAO(this);

		} catch (Exception e) {
			logger.error(e);
			logger.fatal("Error while connecting and binding to CalDAV: " + e.toString());

			throw new SyncSourceException("Error while connecting and binding to CalDAV: " + e.toString());
		}
	}

	/**
	 * Get all VEVENT from last year to the future. TODO set this parameter as
	 * configurabe?
	 * 
	 * @return ArrayList of uid of Caldav Calendars
	 * @throws SyncSourceException
	 *             if can't connecto ro server
	 */
	public List<String> getAllUids() throws SyncSourceException {
		try {
			DateTime oneYearAgo = new DateTime(new DateTime().getTime() - (3600 * 24 * 365 * 1000L));
			DateTime now = new DateTime();
			oneYearAgo.setUtc(true);
			now.setUtc(true);

			return cdm.getComponentProperty(Component.VEVENT, Property.UID, oneYearAgo, now);
		} catch (CalDAV4JException e) {
			throw new SyncSourceException("Error while connecting to CalDAV: ", e);
		}
	}

	
	
	
	/**
	 * Get ics stream by uid
	 * 
	 * @param uid
	 *            entryUID
	 * @return one String containing the entry. empty string if null
	 * @throws SyncSourceException
	 */
	public Calendar getCalendarContentByUid(String uid, TimeZone serverTimeZone) throws SyncSourceException {
		try {
			
			net.fortuna.ical4j.model.Calendar cal = cdm.getCalendarForEventUID(uid);
			
			CalendarHelper.convertDatePropIntoUFCDateProp(cal, Property.EXDATE, serverTimeZone);
			CalendarHelper.convertDatePropIntoUFCDateProp(cal, Property.RDATE, serverTimeZone);
			CalendarHelper.convertDatePropIntoUFCDateProp(cal, Property.LAST_MODIFIED, serverTimeZone);
			
			return cal;
			
		} catch (ResourceNotFoundException e) {

			logger.info("can't find event with uid" + uid, e);
			return null;
		} catch (CalDAV4JException e) {
			logger.error(e);
			throw new SyncSourceException("getCalendarByUid():" + e.getMessage());
		}
	}
	
	public String getCalendarContentAsStringByUid(String uid, TimeZone serverTimeZone) throws SyncSourceException {
		net.fortuna.ical4j.model.Calendar cal = this.getCalendarContentByUid(uid, serverTimeZone);
		return (cal != null) ? cal.toString() : null;
	}

	/**
	 * Get uids modified after Timestamp
	 * 
	 * @param ts
	 *            Timestamp of last sync
	 * @return List of Component UID
	 * @throws SyncSourceException
	 */
	public List<String> getModifiedItemKeys(String component, Timestamp ts) throws SyncSourceException {

		DateTime beginDate = new DateTime(ts.getTime());
		beginDate.setUtc(true);

		try {
			return cdm.getComponentPropertyByTimestamp(component, Property.UID, Property.LAST_MODIFIED, beginDate, null);
		} catch (CalDAV4JException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new SyncSourceException("can't connect to Caldav server");
		}
	}

	/**
	 * Creates a CalDAV filter based on a timestamp to get all new entries
	 * 
	 * @param ts
	 *            Timestamp of last sync
	 * @return Array of ids of Uids
	 * @throws SyncSourceException
	 * @throws CalDAV4JException
	 */
	public List<String> getNewEntries(Timestamp ts) throws SyncSourceException {

		DateTime beginDate = new DateTime(ts.getTime());
		beginDate.setUtc(true);

		logger.info("search filter is" + beginDate.toString());

		PropFilter filterCreated = new PropFilter("C");
		filterCreated.setName(Property.CREATED);
		filterCreated.setTimeRange(null, beginDate);

		PropFilter filterLastModified = new PropFilter("C");
		filterLastModified.setName(Property.LAST_MODIFIED);
		filterLastModified.setTimeRange(beginDate, null);

		List<PropFilter> pFilters = new ArrayList<PropFilter>();
		pFilters.add(filterCreated);
		pFilters.add(filterLastModified);

		try {
			return cdm.getComponentPropertyByTimestamp(Component.VEVENT, Property.UID, Property.CREATED, beginDate, null);
		} catch (CalDAV4JException e) {
			logger.error(e);
			throw new SyncSourceException("getEventPropertyByTimestamp(): can't connect to caldav server");
		}
	}

	/**
	 * Adds a new entry to the server modifying the sincItemKey with the Caldav
	 * Server UUID (?) TODO next on error? TODO set the timezone
	 * 
	 * @param si
	 *            <i>SyncItem</i> to get the attributes to be inserted.
	 * @return the GUID set by the server, null if errors
	 * @throws SyncSourceException
	 * @throws Exception
	 */
	public String addNewEntry(SyncItem si, Timestamp created) throws SyncSourceException {
		// create caldav event and add it to the server
		Calendar cal = dao.syncItem2Ical4j(si);
		VEvent ve = (VEvent) cal.getComponent(Component.VEVENT);
		
		if (ve.getProperty(net.fortuna.ical4j.model.Property.UID) == null) {
			Uid uid = new Uid(si.getKey().getKeyAsString());
			ve.getProperties().add(uid);
		}
		VTimeZone vtz = (VTimeZone) cal.getComponent(Component.VTIMEZONE);

		logger.debug("Writing a new entry");
		logger.trace("entry content:\n" + new String(si.getContent()));
		
		//set CREATED property
		CalendarHelper.addOrReplaceCreated(ve, created);
		
		try {
			ve = cdm.addEvent(ve, vtz);

			logger.info("Wrote the entry with UID: " + ve.getUid().getValue());
			si.getKey().setKeyValue(ve.getUid().getValue());
			return si.getKey().getKeyAsString();

		} catch (CalDAV4JException e) {
			logger.error("Can't write the entry" + new String(si.getContent()), e);
			throw new SyncSourceException("Can't store item:" + new String(si.getContent()));
		}
	}

	/**
	 * Modify an entry on the server
	 * 
	 * @param si
	 *            <i>SyncItem</i> to get modifications from
	 */
	public void updateCalendar(SyncItem si, Timestamp lastModified) throws SyncSourceException {

		Calendar cal = dao.syncItem2Ical4j(si);
		VEvent ve = (VEvent) cal.getComponent(Component.VEVENT);
		
		if (ve.getProperty(net.fortuna.ical4j.model.Property.UID) == null) {
			Uid uid = new Uid(si.getKey().getKeyAsString());
			ve.getProperties().add(uid);
		}
		VTimeZone vtz = (VTimeZone) cal.getComponent(Component.VTIMEZONE);
		
		//set LAST_MODIFIED property
		CalendarHelper.addOrReplaceLastModified(ve, lastModified);
		
		try {
			cdm.editEvent(ve, vtz);
		} catch (ValidationException e) {
			logger.error(e);
			logger.warn("malformed calendar: " + e.getMessage() + "for event \n" + ve.toString());
			throw new SyncSourceException(
					"Failed merging event: see http://m2.modularity.net.au/projects/ical4j/apidocs/net/fortuna/ical4j/util/Calendars.html#merge(net.fortuna.ical4j.model.Calendar, net.fortuna.ical4j.model.Calendar)");
		}
	}

	/**
	 * Delete a syncItem from server
	 * 
	 * @param si
	 *            syncItem to remove
	 * @throws SyncSourceException
	 */
	public void deleteEntry(SyncItem si) throws SyncSourceException {
		String uid = si.getKey().getKeyAsString();
		deleteEntryByUid(uid);
	}

	public void deleteEntryByUid(String uid) throws SyncSourceException {
		logger.info("Removing item with Uid : " + uid);
		try {
			String path = cdm.getPathToResourceForEventId(uid);
			cdm.deletePath(path);
		} catch (NullPointerException e) {
			logger.warn(e);
			logger.info("Can't find item with null uid");
		} catch (ResourceNotFoundException e) {
			// if event is not found, continue
			logger.warn(e);
			logger.info("Can't find item with uid: " + uid);
		} catch (CalDAV4JException e) {
			logger.error(e);
			throw new SyncSourceException("Can't delete item from server");
		}
	}

	/**
	 * Sets the timezone for timestamp convertion
	 * 
	 * @param tz
	 *            LDAP server timezone (should be the same than synch server)
	 */
	public void setTimeZone(TimeZone tz) {
		this.tz = tz;
	}

	/**
	 * disconnect from caldav
	 * */
	public void close() {
		// TODO do I have to disconnect?
	}

	/**
	 * Converts an generalized times to a java.util.Timestamp object XXX imho
	 * this is a kludge
	 * 
	 * @param s
	 *            timestamp string from OpenLDAP
	 * @return t
	 */
	public static Timestamp generalized2timestamp(String s, TimeZone tz) {
		s = s.substring(0, 14); // remove the ending char 'Z';

		Timestamp t = null;
		SimpleDateFormat mySimpleFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			t = new Timestamp(mySimpleFormat.parse(s).getTime());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return t;
	}

	/**
	 * Converts a java.util.Timestamp to a generalized time
	 * 
	 * @param mytime
	 *            input timestamp
	 * @return localized timestamp ?
	 */
	public static String Timestamp2generalized(Timestamp mytime, TimeZone tz) {
		Timestamp b = (mytime != null) ?  new Timestamp(mytime.getTime()) : new Timestamp(System.currentTimeMillis());
		SimpleDateFormat mySimpleFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
		mySimpleFormat.setTimeZone(tz);

		return mySimpleFormat.format(b);
	}

	/**
	 * Converts a java.util.Timestamp to UTC needed if the server can't manage
	 * Generalized Timestamp
	 * 
	 * @param mytime
	 *            input timestamp
	 * @return UTC SQL timestamp
	 */
	public static String Timestamp2UTC(Timestamp mytime) {
		Timestamp b = (mytime != null) ?  new Timestamp(mytime.getTime()) : new Timestamp(System.currentTimeMillis());		
		SimpleDateFormat mySimpleFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
		mySimpleFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		return mySimpleFormat.format(b);
	}

	// XXX remove-me: this and other functions should be replaced with the
	// generic ones
	public List<String> getModifiedItemKeys(Timestamp ts) throws SyncSourceException {
		return getModifiedItemKeys(Component.VEVENT, ts);
	}

} // end class
