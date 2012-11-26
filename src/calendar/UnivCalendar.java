package calendar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class UnivCalendar {

	public static void main(String[] args) {

		// Récupération en local
		try {
			System.out.println(ICSFinder.getLocal("exemple.ics"));
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException : " + e.getMessage());
		}

		// Récupération sur le serveur
		try {
			System.out.println(ICSFinder.getURL("http://www.edt-sciences.univ-nantes.fr/g78030.ics"));
		} catch (MalformedURLException e) {
			System.out.println("MalformedURLException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
		}

		// Récupération google
		// adresse : atal.univ.nantes@gmail.com
		// passwd : jnatal44
		
		//https://developers.google.com/google-apps/calendar/v2/developers_guide_java


	}
}
