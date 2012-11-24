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
 * @author acaretto@babel.it
 */
package it.babel.funambol.CalDAV.helper;

import it.babel.funambol.CalDAV.conversion.Converter;
import it.babel.funambol.CalDAV.engine.CaldavInterface;
import it.babel.funambol.CalDAV.exceptions.ConversionException;
import it.babel.funambol.CalDAV.util.ModuleConstants;
import it.babel.funambol.CalDAV.util.TimeUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.TimeZone;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Version;

import org.osaf.caldav4j.CalDAV4JException;
import org.osaf.caldav4j.util.ICalendarUtils;

import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

public class CalendarHelper {

	protected static FunambolLogger log = FunambolLoggerFactory.getLogger(ModuleConstants.LOG_NAME);
	
	/**
	 * update ical4j item with data contained in a syncItem
	 * 
	 * @param oldItem
	 * @param newItem
	 * @return calendar merged
	 * @throws SyncSourceException
	 */
	public static Calendar mergeCalendarWithSyncItem(Calendar oldCalendar, byte[] newItemContent, String siType, TimeZone deviceTimeZone) throws SyncSourceException {
		/**
		 * case newItem.getType: - vcs -> convert to funambol pim and get
		 * meaningful data - sif -> as vcs
		 */
		// select the fields we need to update
		// DESCRIPTION, SUMMARY, CLASS, LOCATION, DTSTART, DTEND, CLASS
		// RRULE, AALARM, ATTENDEEs
		// convert sincItem to Funambol Calendar PIM
		Converter converter = new Converter(deviceTimeZone);
		try {
			// update oldCalendar with new data
			com.funambol.common.pim.calendar.Calendar pimCalendar = converter.webCalendar2Calendar(new ByteArrayInputStream(newItemContent), siType);

			CalendarContent pimCC = pimCalendar.getCalendarContent();
			if (pimCC != null) {

				CalendarComponent cc = (CalendarComponent) oldCalendar.getComponent(Component.VEVENT);

				if (pimCC.getDescription() != null) {
					ICalendarUtils.addOrReplaceProperty(cc, new Description(pimCalendar.getCalendarContent().getDescription().getPropertyValueAsString()));
				}
				if (pimCC.getSummary() != null) {
					ICalendarUtils.addOrReplaceProperty(cc, new Summary(pimCalendar.getCalendarContent().getSummary().getPropertyValueAsString()));
				}
				if (pimCC.getLocation() != null) {
					ICalendarUtils.addOrReplaceProperty(cc, new Location(pimCalendar.getCalendarContent().getLocation().getPropertyValueAsString()));
				}
				if (pimCC.getDtStart() != null) {
					ICalendarUtils.addOrReplaceProperty(cc, new DtStart(pimCalendar.getCalendarContent().getDtStart().getPropertyValueAsString()));
				}
				if (pimCC.getDtEnd() != null) {
					ICalendarUtils.addOrReplaceProperty(cc, new DtEnd(pimCalendar.getCalendarContent().getDtEnd().getPropertyValueAsString()));
				}
			}
			return oldCalendar;

		} catch (ConversionException e) {
			throw new SyncSourceException("Error parsing calendar", e.getCause());
		} catch (ParseException e) {
			throw new SyncSourceException("Error parsing calendar Date/Time", e.getCause());
		}
	}

	/**
	 * convert a value of specific calendar properties date into UTC format
	 * 
	 * @param calendar
	 *            the calender containing a properties date
	 * @param propName
	 *            the name of the properties to convert
	 * @param timeZone
	 *            the TimeZone to reffer for conversion into UTC format
	 */
	public static void convertDatePropIntoUFCDateProp(Calendar calendar, String propName, TimeZone serverTimeZone) {
		try {
			if (calendar != null && calendar.getComponent(Component.VEVENT) != null) {

				Property prop = calendar.getComponent(Component.VEVENT).getProperty(propName);
				if (prop != null) {
					String dateValue = prop.getValue();

					if (!TimeUtils.dateIsUTCformat(dateValue)) {
						if (serverTimeZone == null)
							serverTimeZone = TimeZone.getDefault();

						String utcDate = TimeUtils.convertLocalTimeToUTC(dateValue, serverTimeZone);

						// update the properties
						calendar.getComponent(Component.VEVENT).getProperties().remove(prop);
						prop.setValue(utcDate);
						calendar.getComponent(Component.VEVENT).getProperties().add(prop);
					}
				}
			}

		} catch (Exception e) {
			log.error("convertDatePropIntoUFCDateProp has encurred an error with propName: " + propName, e);
		}

	}

	/**
	 * strip a no-outlook properties for sync SERVER to CLIENT
	 * 
	 * @param calendar
	 *            the calender string
	 */
	public static String prepareCalendarForOutlook(String calendar) {
		calendar = calendar.replaceAll(";X-BEDEWORK-UID=.*?([;:])", "$1");
		calendar = calendar.replaceAll("\r\nSTATUS:.*", "");
		calendar = calendar.replaceAll("\r\nUID:.*", "");
		return calendar;
	}

	/**
	 * add or replace the property LAST_MODIFIED with the timestamp value converted in UTC format
	 * 
	 * @param ve event to addOrReplace LAST-MODIFIED
	 * @param value time stamp value
	 */
	public static void addOrReplaceLastModified(VEvent ve, Timestamp value) {	
		
		try {
			Property lastModified = new LastModified();
			
			lastModified.setValue(CaldavInterface.Timestamp2UTC(value));
			
			ICalendarUtils.addOrReplaceProperty(ve, lastModified);
			
		} catch (IOException e) {
			log.error("addOrReplaceLastModified has encurred an I/O error", e);
		} catch (URISyntaxException e) {
			log.error("addOrReplaceLastModified has encurred an URI error", e);
		} catch (ParseException e) {
			log.error("addOrReplaceLastModified has encurred an parse error", e);
		} catch (Exception e) {
			log.error("addOrReplaceLastModified has encurred an error", e);
		} 	
	}
	
	/**
	 * add or replace the property CREATED with the timestamp value converted in UTC format
	 * 
	 * @param ve event to addOrReplace CREATED
	 * @param value time stamp value
	 */
	public static void addOrReplaceCreated(VEvent ve, Timestamp value) {	
		
		try {
			Property lastModified = new LastModified();
			Property created = new Created();
			
			lastModified.setValue(CaldavInterface.Timestamp2UTC(value));
			created.setValue(CaldavInterface.Timestamp2UTC(value));
			
			ICalendarUtils.addOrReplaceProperty(ve, lastModified);
			ICalendarUtils.addOrReplaceProperty(ve, created);
			
		} catch (IOException e) {
			log.error("addOrReplaceCreated has encurred an I/O error", e);
		} catch (URISyntaxException e) {
			log.error("addOrReplaceCreated has encurred an URI error", e);
		} catch (ParseException e) {
			log.error("addOrReplaceCreated has encurred an parse error", e);
		} catch (Exception e) {
			log.error("addOrReplaceCreated has encurred an error", e);
		} 	
	}


}
