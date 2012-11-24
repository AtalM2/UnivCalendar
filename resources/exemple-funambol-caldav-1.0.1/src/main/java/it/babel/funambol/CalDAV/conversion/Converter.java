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
 * @author pventura@babel.it
 */
package it.babel.funambol.CalDAV.conversion;

import it.babel.funambol.CalDAV.exceptions.ConversionException;
import it.babel.funambol.CalDAV.helper.CalendarHelper;
import it.babel.funambol.CalDAV.util.CalDavConstants;
import it.babel.funambol.CalDAV.util.ModuleConstants;
import it.babel.funambol.CalDAV.util.StringDecoderUtils;
import it.babel.funambol.CalDAV.util.TimeUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.TimeZone;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.converter.BaseConverter;
import com.funambol.common.pim.converter.CalendarToSIFE;
import com.funambol.common.pim.converter.TaskToSIFT;
import com.funambol.common.pim.converter.VComponentWriter;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.sif.SIFCalendarParser;
import com.funambol.common.pim.xvcalendar.XVCalendarParser;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * Make conversion between ICAL, VCAL and SIFE formats. Make conversions as
 * suggested by funambol team. Code is taken from PIMCalendarSyncSource.
 * 
 * @author pventura@babel.it
 * 
 */
public class Converter {

	protected static FunambolLogger log = FunambolLoggerFactory.getLogger(ModuleConstants.LOG_NAME);
	private String deviceCharset;
	private TimeZone deviceTimeZone;

	public Converter() {

	}

	public Converter(TimeZone deviceTimeZone) {
		this.deviceTimeZone = deviceTimeZone;
	}

	public String getDeviceCharset() {
		return deviceCharset;
	}

	public void setDeviceCharset(String deviceCharset) {
		this.deviceCharset = deviceCharset;
	}

	public TimeZone getDeviceTimeZone() {
		return deviceTimeZone;
	}

	public void setDeviceTimeZone(TimeZone deviceTimeZone) {
		this.deviceTimeZone = deviceTimeZone;
	}

	public Calendar webCalendar2Calendar(InputStream buffer, String vCalType) throws ConversionException {

		try {
			VCalendar vcalendar;
			String version;
			String charset;

			StringBuilder sb = new StringBuilder(60);
			sb.append("Converting: ").append(vCalType).append(" => Calendar ");
			log.debug(sb.toString());

			if (vCalType.equals(CalDavConstants.TYPE_VCAL)) {
				// vCalendar (1.0):
				XVCalendarParser parser = new XVCalendarParser(buffer);
				vcalendar = (VCalendar) parser.XVCalendar();
				version = "1.0";
				charset = BaseConverter.CHARSET_UTF7; // (versit spec)
			} else {
				// convert text to iCalendar (2.0)
				ICalendarParser parser = new ICalendarParser(buffer);
				vcalendar = (VCalendar) parser.ICalendar();
				version = "2.0";
				charset = BaseConverter.CHARSET_UTF8; // (RFC 2445)
			}

			if (deviceCharset != null) {
				charset = deviceCharset; // overrides the default character set
			}

			String retrievedVersion = null;
			if (vcalendar.getProperty("VERSION") != null) {
				retrievedVersion = vcalendar.getProperty("VERSION").getValue();
				vcalendar.delProperty(vcalendar.getProperty("VERSION"));
			}
			vcalendar.addProperty("VERSION", version);
			if (retrievedVersion == null) {
				log.trace("No version property was found in the vCal/iCal " + "data: version set to " + version);
			} else if (!retrievedVersion.equals(version)) {
				log.trace("The version in the data was " + retrievedVersion + " but it's been changed to " + version);
			}

			log.trace("VCalendar object ={" + vcalendar + "}\n");
			VCalendarConverter vcf = new VCalendarConverter(deviceTimeZone, charset);
			Calendar c = vcf.vcalendar2calendar(vcalendar);
			log.trace("Conversion done.");

			return c;

		} catch (Exception e) {
			throw new ConversionException("Error converting " + vCalType + " to Calendar. ", e);
		}
	}

	public Calendar webCalendar2Calendar(String text, String vCalType) throws ConversionException {

		try {

			VCalendar vcalendar;
			String version;
			String charset;

			StringBuilder sb = new StringBuilder(text.length() + 60);
			sb.append("Converting: ").append(vCalType).append(" => Calendar ").append("\nINPUT = {").append(text).append('}');
			log.debug(sb.toString());

			if (vCalType.equals(CalDavConstants.TYPE_VCAL)) {
				text = TimeUtils.normalizeDate(text);
			}

			ByteArrayInputStream buffer = new ByteArrayInputStream(text.getBytes());

			/*
			 * if (vCalType.equals(PIMSyncSource.TYPE[PIMSyncSource.VCAL])) { //
			 * vCalendar (1.0): XVCalendarParser parser = new
			 * XVCalendarParser(buffer); vcalendar = (VCalendar)
			 * parser.XVCalendar(); version = "1.0"; charset =
			 * BaseConverter.CHARSET_UTF7; // (versit spec) } else
			 */
			{
				// iCalendar (2.0):
				ICalendarParser parser = new ICalendarParser(buffer);
				vcalendar = (VCalendar) parser.ICalendar();
				version = "2.0";
				charset = BaseConverter.CHARSET_UTF8; // (RFC 2445)
			}
			if (deviceCharset != null) {
				charset = deviceCharset; // overrides the default character set
			}

			String retrievedVersion = null;
			if (vcalendar.getProperty("VERSION") != null) {
				retrievedVersion = vcalendar.getProperty("VERSION").getValue();
			}
			vcalendar.addProperty("VERSION", version);
			if (retrievedVersion == null) {
				log.trace("No version property was found in the vCal/iCal " + "data: version set to " + version);
			} else if (!retrievedVersion.equals(version)) {

				log.trace("The version in the data was " + retrievedVersion + " but it's been changed to " + version);

			}

			VCalendarConverter vcf = new VCalendarConverter(deviceTimeZone, charset);
			Calendar c = vcf.vcalendar2calendar(vcalendar);
			log.trace("Conversion done.");
			return c;

		} catch (Exception e) {
			throw new ConversionException("Error converting " + vCalType + " to Calendar. ", e);
		}
	}

	public String calendar2webCalendar(Calendar calendar, String vCalType) throws ConversionException {

		try {

			String charset;
			/*
			 * if (vCalType.equals(PIMSyncSource.TYPE[PIMSyncSource.VCAL])) { //
			 * vCalendar (1.0): charset = BaseConverter.CHARSET_UTF7; // (versit
			 * spec) } else
			 */{
				// iCalendar (2.0):
				charset = BaseConverter.CHARSET_UTF8; // (RFC 2445)
			}
			if (deviceCharset != null) {
				charset = deviceCharset; // overrides the default character set
			}

			VCalendarConverter vcf = new VCalendarConverter(deviceTimeZone, charset);

			VCalendar vcalendar;
			String vcal;

			log.trace("Converting: Calendar => " + vCalType);

			if (vCalType.equals(CalDavConstants.TYPE_VCAL)) { // VCAL
				vcalendar = vcf.calendar2vcalendar(calendar, true); // text/x-
																	// vcalendar
			} else { // ICAL
				vcalendar = vcf.calendar2vcalendar(calendar, false); // text/
																		// calendar
			}

			VComponentWriter writer = new VComponentWriter(VComponentWriter.NO_FOLDING);
			vcal = writer.toString(vcalendar);

			if (vCalType.equals(CalDavConstants.TYPE_VCAL)) {
				vcal = CalendarHelper.prepareCalendarForOutlook(vcal);
			} 
			
			log.debug("OUTPUT = {" + vcal + "}. Conversion done.");
			return vcal;

		} catch (Exception e) {
			throw new ConversionException("Error converting Calendar to " + vCalType, e);
		}
	}

	public Calendar sif2Calendar(String xml, String sifType) throws ConversionException {

		StringBuilder sb = new StringBuilder(xml.length() + 60);
		sb.append("Converting: ").append(sifType).append(" => Calendar ").append("\nINPUT = {").append(xml).append('}');
		log.debug(sb.toString());

		ByteArrayInputStream buffer = null;
		Calendar calendar = null;
		try {
			calendar = new Calendar();
			buffer = new ByteArrayInputStream(xml.getBytes());
			if ((xml.getBytes()).length > 0) {
				SIFCalendarParser parser = new SIFCalendarParser(buffer);
				calendar = parser.parse();
			} else {
				throw new Exception("No data");
			}
		} catch (Exception e) {
			throw new ConversionException("Error converting " + sifType + " to Calendar. ", e);
		}
		log.trace("Conversion done.");
		return calendar;
	}

	public String calendar2sif(Calendar calendar, String sifType) throws ConversionException {

		log.debug("Converting: Calendar => " + sifType);
		String xml = null;
		BaseConverter c2xml;
		Object thing;

		try {
			if (sifType.equals(CalDavConstants.TYPE_SIFE)) { // SIF-E
				c2xml = new CalendarToSIFE(deviceTimeZone, deviceCharset);
				thing = calendar;
				// NB: A CalendarToSIFE converts a Calendar into a SIF-E
			} else { // SIF-T
				c2xml = new TaskToSIFT(deviceTimeZone, deviceCharset);
				thing = calendar.getTask();
				// NB: A TaskToSIFT converts just a Task into a SIF-T
			}

			xml = c2xml.convert(thing);
			log.debug("OUTPUT = {" + xml + "}. Conversion done.");
		} catch (Exception e) {
			throw new ConversionException("Error converting Calendar to " + sifType, e);
		}
		return xml;
	}
}
