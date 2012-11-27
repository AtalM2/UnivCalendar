/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar.util;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class Tools {

	/**
	 * Conversion d'une date de l'ICS (20121119T070000Z) vers une date au format
	 * XS:DateTime (2012-11-19T07:00:00)
	 */
	public static String getXSDateTime(String dateTime) {
		String ret = "";
		ret = dateTime.substring(0, 4);		// Année
		ret += "-" + dateTime.substring(4, 6);		// Mois
		ret += "-" + dateTime.substring(6, 8);		// Année
		ret += "T" + dateTime.substring(9, 11);		// Heures
		ret += ":" + dateTime.substring(11, 13);		// Minutes
		ret += ":" + dateTime.substring(13, 15);		// Secondes

		return ret;
	}

	/**
	 * Conversion d'une date de l'ICS (20121119T070000Z) vers une date au format
	 * XS:Date (2012-11-19)
	 */
	public static String getXSDate(String date) {
		String ret = "";
		ret = date.substring(0, 4);		// Année
		ret += "-" + date.substring(4, 6);		// Mois
		ret += "-" + date.substring(6, 8);		// Année
		return ret;
	}
}
