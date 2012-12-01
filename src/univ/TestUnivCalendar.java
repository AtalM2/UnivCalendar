package univ;

import ggl.GGLParser;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.google.gdata.client.calendar.CalendarService;

import univ.calendar.Calendar;
import univ.calendar.Week;
import univ.ics.ICSFinder;
import univ.ics.ICSParser;
import univ.util.DateTime;
import univ.view.MainFrame;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class TestUnivCalendar {

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

		CalendarService myService = new CalendarService("exampleCo-exampleApp-1");
		Calendar calGoogle = GGLParser.parse(myService);
		System.out.println(calGoogle.toString());
		
		
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		DateTime now = new DateTime();
		Week week = calendar.findWeek(now);
		mainFrame.getJWeek().addWeek(week, Color.lightGray);
		//mainFrame.getJWeek().addWeek(calendar.getWeeksList().get(12), Color.blue);
		mainFrame.getWeekNumber().setText("Semaine " + week.getWeekOfYear());
		mainFrame.getWeekDetail().setText("Du " + week.getStartDate().toString() + " au " + week.getEndDate().toString());
		mainFrame.getJWeek().build();


	}
}
