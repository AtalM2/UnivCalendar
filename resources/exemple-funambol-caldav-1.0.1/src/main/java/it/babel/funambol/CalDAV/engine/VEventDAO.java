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
 */

package it.babel.funambol.CalDAV.engine;

import it.babel.funambol.CalDAV.conversion.Converter;
import it.babel.funambol.CalDAV.engine.source.CalDAVSyncSourceVEvent;
import it.babel.funambol.CalDAV.exceptions.ConversionException;
import it.babel.funambol.CalDAV.util.CalDavConstants;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Uid;

import com.funambol.common.pim.common.ReaderException;
import com.funambol.common.pim.contact.Contact;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;


/**
 * This class implements the Data Access Object for <i>VEvent</i>.
 * @author  rpolli@babel.it
 * @version $Id$
 */
public class VEventDAO
{
	// ----------------------------------------------------------- Private data

	public static final int SIFC = 0;  // To be used as index for SIF-Contact
	public static final int SIFE = 1;  // To be used as index for SIF-Event
	public static final int SIFN = 2;  // To be used as index for SIF-Note
	public static final int SIFT = 3;  // To be used as index for SIF-Task
	public static final int VCARD = 4; // To be used as index for VCard
	public static final int VCAL = 5;  // To be used as index for VCal
	public static final int ICAL = 6;  // To be used as index for ICal
	public static final int VNOTE = 7; // To be used as index for VNote


	private CaldavInterface  cdInterface 	= null;
	private FunambolLogger logger			= null;

	// ------------------------------------------------------------ Constructor

	/**
	 * The constructor is called the the CalDAVInterface object and it stores
	 * this object as a private object variable
	 *  
	 * @param caldavInterface Reference to the CalDAV Interface to read single CalDAV entries
	 */	
	public VEventDAO(CaldavInterface caldavInterface)
	{
		cdInterface = caldavInterface;
		logger = cdInterface.logger;
	} 


	/** convert a vCal content to an iCal content
	 * 
	 */
	private static String StripEmptyFields(String siContent) throws NullPointerException
	{
		// TODO maybe in this way I can lose the tz
		// TODO convert siContent from vCalendar to iCalendar
		// XXX strips AALARM from vCalendar v1.0 events 
		try {
			siContent = siContent
			.replaceAll("\nRRULE:\\s+\n", "\n")
			.replaceAll("AALARM;TYPE=X-EPOCSOUND:", "AALARM:")
			//			.replaceAll("\nSTATUS:[0-9].*","")
			//			.replaceAll("\nAALARM:.*", "") // this will be converted to X-VCAL10-AALARM
			//			.replaceAll("\n(DT(START|END|STAMP):\\d{1,8})\n", "\n$1T000000Z\n")
			;
			//PV
			siContent=siContent.replaceAll("\nMETHOD:.*\n", "\n");
		} catch (Exception e) {
			throw new NullPointerException("StripEmptyFields(): null content");
		}
		return siContent;
	}

	/**
	 * convert a syncItem to a ical4j VEvent setting the sincItemKey as UID if not defined
	 * @return {@link VEvent}
	 */
	public VEvent syncItem2Ical4jVEvent(SyncItem si)
	throws SyncSourceException
	{
		// TODO maybe in this way I can lose the tz
		net.fortuna.ical4j.model.Calendar cal = syncItem2Ical4j(si);
		VEvent ve = (VEvent) cal.getComponent(Component.VEVENT);

		if (ve.getProperty(net.fortuna.ical4j.model.Property.UID) == null) {
			Uid uid = new Uid(si.getKey().getKeyAsString() );
			ve.getProperties().add(uid);
		}
		return ve;
	}

	/**
	 * convert a syncItem SIFE or VCAL1 to a ical4j calendar (iCal only)
	 * @return {@link Calendar} an ical4j Calendar item
	 */
	public Calendar syncItem2Ical4j(SyncItem si)
	throws SyncSourceException
	{
		// TODO maybe in this way I can lose the tz?
		String siContent = new String(si.getContent());
		StringReader sr = null;

		logger.info("SOURCE_TYPE: "+si.getType());
		try {
			CalDAVSyncSourceVEvent source = (CalDAVSyncSourceVEvent) si.getSyncSource();
			if (CalDavConstants.TYPE_SIFE.equals(si.getType())) { //SIFE
				Converter c= new Converter(source.getDeviceTimeZone());
				com.funambol.common.pim.calendar.Calendar calendar = c.sif2Calendar(siContent, CalDavConstants.TYPE_SIFE);
				siContent = c.calendar2webCalendar(calendar,CalDavConstants.TYPE_ICAL);
				siContent=normalizeCalendar(siContent);

			} else if (CalDavConstants.TYPE_VCAL.equals(si.getType()) ){
				//VCAL1 
				// strip non-rfc fields from field
				siContent = StripEmptyFields(siContent);

				Converter c = new Converter(source.getDeviceTimeZone());
				com.funambol.common.pim.calendar.Calendar calendar = c.webCalendar2Calendar(siContent, CalDavConstants.TYPE_VCAL);
				siContent = c.calendar2webCalendar(calendar,CalDavConstants.TYPE_ICAL);
				siContent = normalizeCalendar(siContent); 

			}
		} 
		catch (ConversionException e) {
			logger.error(e.getCause());
			throw new SyncSourceException("Can't parse content "+siContent);
		}

		//EPV
		// ... then create a net.fortuna.ical4j

		try {
			CalendarBuilder cb = new CalendarBuilder();
			sr = new StringReader(siContent);

			return cb.build(sr);
		} catch (IOException e) {
			e.printStackTrace();
			logger.warn("Error while building"+siContent,e);
			throw new SyncSourceException("IOException:can't convert syncItem to iCalendar. bad syncItem format?");
		} catch (ParserException e) {
			e.printStackTrace();
			logger.warn("Error while building"+siContent,e);
			throw new SyncSourceException("can't convert syncItem to iCalendar. bad syncItem format?");
		}
	}

	/**
	 * Creates a unique identifier
	 * @param contact contact needing an id
	 * @return id of the contact
	 */
	private String createUniqueId(Contact contact) {
		Date ora = new Date();

		if (contact.getUid()==null)
			return "fnbl-id-" + Math.abs(((contact.toString() + ora.getTime()).hashCode()));
		else
			return contact.getUid() + "-" + ora.getTime();
	}

	private String normalizeCalendar(String calendar){
		//calendar=calendar.replaceAll("(ATTENDEE;\\s*)*PARTSTAT","ATTENDEE;PARTSTAT").replaceAll("PARTSTAT\\s*\\W","PARTSTAT=");
		//clean ical4j output
		//calendar=calendar.replaceAll("PARTSTAT:TENTATIVE\\s+\n", "");
		//remove partstat completely
		calendar=calendar.replaceAll("PARTSTAT:[^\n]*\n", "");
		calendar=calendar.replaceAll("(;)?CHARSET=[^:]*:", ":");
		calendar=calendar.replaceAll("DTSTART:\\s*([0-9]{4})-([0-9]{2})-([0-9]{2})[^T]*.*", "DTSTART:$1$2$3T000000");
		calendar=calendar.replaceAll("DTEND:\\s*([0-9]{4})-([0-9]{2})-([0-9]{2})[^T]*.*", "DTEND:$1$2$3T000000");


		// remove AALARM
		calendar=calendar.replaceAll("AALARM:", "X-VCAL10-AALARM:");
		
		return calendar;
	}
}
