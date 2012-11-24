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
package it.babel.funambol.CalDAV.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

public class TimeUtils {
	
	protected static FunambolLogger log = FunambolLoggerFactory.getLogger(ModuleConstants.LOG_NAME);
	
	//static fields
	public final static String PATTERN_UTC = "yyyyMMdd'T'HHmmss'Z'";
	public final static int PATTERN_UTC_LENGTH = 16;

	public final static String PATTERN_UTC_WOZ = "yyyyMMdd'T'HHmmss";
	public final static int PATTERN_UTC_WOZ_LENGTH = 15;
	
	public final static String PATTERN_LOCAL_DATE = "yyyyMMdd'T'HHmmss";
	public final static int PATTERN_LOCAL_DATE_LENGTH = 15;
	
	public final static String PATTERN_LOCAL_DATE_SHORT = "yyyyMMdd";
	public final static int PATTERN_SHORT_DATE_LENGTH = 8;

	public final static TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
	
	/**
	 * convert DTSTART, DTEND, DTSTAMP in date format yyyyMMddT000000 
	 * 
	 * @param calendar the in calendar to normalize date
	 * @return calendar with date formatted
	 */
	public static String normalizeDate(String calendar){
		
		if(calendar == null || calendar.equals("")){
			return "";
		}
		calendar = calendar.replaceAll("\n(DT(START|END|STAMP):\\d{1,8})\n", "\n$1T000000\n");
		return calendar;		
	}
	
	/**
	 * convert a date in UTC format with specified param timeZone  
	 * 
	 * @param localDateTime rappresent a date rappresented in local time format
	 * @param timZone rappresent a timeZone used to convert the param localDateTime in UTC format
	 * @return String date converted in UTC format
	 */
	public static String convertLocalTimeToUTC(String localDateTime, TimeZone timeZone) throws Exception {
		
		if(dateIsUTCformat(localDateTime)){
			return localDateTime;
		}
		
		SimpleDateFormat parser = null;
		
		if(localDateTime.length() == PATTERN_SHORT_DATE_LENGTH){
			parser = new SimpleDateFormat(PATTERN_LOCAL_DATE_SHORT);
		} else {
			parser = new SimpleDateFormat(PATTERN_LOCAL_DATE);
		}
		
		parser.setTimeZone(timeZone);
		Date localDate = parser.parse(localDateTime);

		SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_UTC);
		formatter.setTimeZone(timeZone);
		formatter.setTimeZone(TIMEZONE_UTC);

		return formatter.format(localDate);

	}
	
	/**
	 * check if the param dateValue is in UTCformat  
	 * 
	 * @param dateValue represent a date to check
	 * @return boolean 
	 */
	public static boolean dateIsUTCformat(String dateValue) {
		return (dateValue != null && !dateValue.equals("") && dateValue.endsWith("Z"));
	}
	
	

}
