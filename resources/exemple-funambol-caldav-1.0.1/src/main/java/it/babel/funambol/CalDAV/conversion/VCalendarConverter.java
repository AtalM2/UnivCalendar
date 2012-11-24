package it.babel.funambol.CalDAV.conversion;

import java.util.TimeZone;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.converter.ConverterException;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.model.VCalendarContent;



public class VCalendarConverter extends com.funambol.common.pim.converter.VCalendarConverter{

	public VCalendarConverter(TimeZone timezone, String charset) {
		super(timezone, charset);
		
	}

	@Override
	public VCalendar calendar2vcalendar(Calendar cal, boolean xv)
			throws ConverterException {
		CalendarContent content = cal.getCalendarContent();
		Property acc = content.getAccessClass();
		if (acc != null){
			Object value = acc.getPropertyValue();
			if (value != null){
				if (value instanceof String){
					acc.setPropertyValue(Short.parseShort((String) value) );
					content.setAccessClass(acc);
					cal.setCalendarContent(content);
				}
			}
		}
		
		VCalendar vcal = super.calendar2vcalendar(cal, xv);
		
		if(!xv){
			decodeProperty(vcal, "DESCRIPTION");
			decodeProperty(vcal, "SUMMARY");
			decodeProperty(vcal, "LOCATION");
		}
		
		return vcal;
	}

	/**
	 * decode quoted-printable the prop specified
	 * 
	 * @param vcal the input calendar
	 * @param propName the properties to decode
	 */	private void decodeProperty(VCalendar vcal, String propName) {
		
		VCalendarContent vcc = vcal.getVCalendarContent();
		com.funambol.common.pim.model.Property prop = vcc.getProperty(propName);
		if (prop != null) {
			Property decoded = decodeField(prop);
			vcc.delProperty(prop);
			String decodedValue = decoded.getPropertyValueAsString(); 
			//used for long lines and  line feed
			decodedValue = decodedValue.replaceAll("\r\n", "\\\\n").replaceAll("\\\\n ", "\r\n ");
			vcc.addProperty(propName, decodedValue); 
		}
	}

	
} 
