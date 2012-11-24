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

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.codec.CodecException;
import com.funambol.framework.tools.codec.QuotedPrintableCodec;

public class StringDecoderUtils {

	protected static FunambolLogger log = FunambolLoggerFactory.getLogger(ModuleConstants.LOG_NAME);

	/**
	 * decode a calendar using quoted/printable encoding in specified props
	 * names
	 * 
	 * @param calendar
	 *            the calendar to decode
	 * @param propsName
	 *            the array of properties to decode
	 * @return calendar decoded
	 */
	public static String decodeCalendar(String calendar, String[] propsName) {
		if (propsName != null && propsName.length > 0) {

			for (int i = 0; i < propsName.length; i++) {
				String propName = propsName[i];
				String regex = "\n" + propName + ".*\n";

				// Pattern pat =
				// Pattern.compile("\n(DT(START|END|STAMP):\\d{1,8})\n");
				Pattern pat = Pattern.compile(regex);
				Matcher mat = pat.matcher(calendar);
				// calendar.replaceAll(";ENCODING=QUOTED-PRINTABLE=.*?([;:])",
				// "$1");
				String toDecode = null;
				if (mat.find()) {
					toDecode = mat.group();
				}

				if (toDecode != null) {
					if (toDecode.contains("ENCODING=QUOTED-PRINTABLE")) {
						toDecode = toDecode.substring(toDecode.indexOf(":") + 1, toDecode.length());
						toDecode = decodeQuotedPrintableString(toDecode);
						calendar = calendar.replaceAll(regex, "\n" + propName + ":" + toDecode);
					}
				}
			}

		}
		
		return calendar;
	}

	/**
	 * decode string value using quoted/printable encoding
	 * 
	 * @param string
	 *            the string to decode
	 * @return string decoded
	 */
	public static String decodeQuotedPrintableString(String string) {
		String decoded = string;
		QuotedPrintableCodec qu = new QuotedPrintableCodec();
		try {
			decoded = qu.decode(decoded);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (CodecException e) {
			e.printStackTrace();
		}
		return decoded;
	}

}
