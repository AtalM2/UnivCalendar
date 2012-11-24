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
package it.babel.funambol.CalDAV.exceptions;

/**
 * Class for convertion errors
 * @author pventura@babel.it
 *
 */
public class ConversionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7805352456102167959L;
	private static final String MESSAGE = "Conversion error";

	public ConversionException() {
		super(MESSAGE);
	}

	public ConversionException(String message, Throwable cause) {
		super(MESSAGE + "; "+ message, cause);
		
	}

	public ConversionException(String message) {
		super("MESSAGE" +  "; " + message);
		
	}

	public ConversionException(Throwable cause) {
		super(MESSAGE, cause);
	}
	
	

}
