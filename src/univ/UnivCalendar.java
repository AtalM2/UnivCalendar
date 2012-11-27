package univ;

import univ.calendar.Calendar;
import univ.ics.ICSParser;
import univ.ics.ICSFinder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class UnivCalendar {

	public static void main(String[] args) {
		ArrayList<String> ics = null;
		
		// Récupération sur le serveur
		try {
			ics = ICSFinder.getURL("http://www.edt-sciences.univ-nantes.fr/g78030.ics");
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
		}

		// Récupération en local
//		try {
//			System.out.println(ICSFinder.getLocal("exemple.ics"));
//		} catch (FileNotFoundException e) {
//			System.out.println("FileNotFoundException : " + e.getMessage());
//		}
		
		// Parsing de l'ICS récupéré
		Calendar calendar = ICSParser.parse(ics);
		System.out.println(calendar.toString());

		// Récupération google
		// adresse : atal.univ.nantes@gmail.com
		// passwd : jnatal44
		
		//https://developers.google.com/google-apps/calendar/v2/developers_guide_java


	}
}
