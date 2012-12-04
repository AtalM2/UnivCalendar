package univ;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import univ.calendar.Calendar;
import univ.calendar.Event;
import univ.calendar.Week;
import univ.google.GGLCreator;
import univ.google.GGLParser;
import univ.ics.ICSFinder;
import univ.ics.ICSParser;
import univ.util.DateTime;
import univ.view.MainFrame;

/**
 *
 * @author Noémi Salaün, Joseph Lark
 */
public class TestUnivCalendar {

	public static void main(String[] args) {
		ArrayList<String> ics = null;

		// Récupération sur le serveur
		try {
			ics = ICSFinder.getURL("http://www.edt-sciences.univ-nantes.fr/g78030.ics");
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException");
			e.printStackTrace();
		}

		// Récupération en local
//		try {
//			System.out.println(ICSFinder.getLocal("exemple.ics"));
//		} catch (FileNotFoundException e) {
//			System.out.println("FileNotFoundException : " + e.getMessage());
//		}

		// Parsing de l'ICS récupéré
		Calendar calendar = ICSParser.parse(ics);
		//System.out.println(calendar.toString());

		// Récupération google
		String adresse = "atal.univ.nantes@gmail.com";
		String passwd = "jnatal44";

		//https://developers.google.com/google-apps/calendar/v2/developers_guide_java

		CalendarService myService = new CalendarService("");
		try {
			myService.setUserCredentials(adresse, passwd);
		} catch (AuthenticationException e) {
			System.err.println("AuthenticationException");
			e.printStackTrace();
		}
		System.out.println("calendrier cours");
		Calendar calGoogleCours = GGLParser.parse(myService, true);
		System.out.println(calGoogleCours.toString());
		
		System.out.println("calendrier autres");
		Calendar calGoogleNotCours = GGLParser.parse(myService, false);
		System.out.println(calGoogleNotCours.toString());
		
		Event event = new Event();
		event.setStartTime(new DateTime("20121203_200000"));
		event.setEndTime(new DateTime("20121203_210000"));
		event.setSummary("Le summary de l event");
		event.setUid("L_UID_DE_L_EVENT");
		//
		System.out.println(event.toString());
		CalendarEventEntry cee = new CalendarEventEntry();
//		try {
//			cee = GGLCreator.createSingleEvent(myService, e);
//		} catch (ServiceException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}


//		try {
//			GGLCreator.deleteEvent(myService, cee);
//		} catch (ServiceException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}


		try {
			GGLCreator.updateEvent(myService, event);
		} catch (ServiceException e) {
			System.err.println("ServiceException");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException");
			e.printStackTrace();
		}

		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		DateTime now = new DateTime();
		Week week = calendar.findWeek(now);
		Week weekGoogle = calGoogleNotCours.findWeek(now);
		mainFrame.getJWeek().addIcsUnivWeek(week);
		mainFrame.getJWeek().addGoogleEventWeek(weekGoogle);
		mainFrame.getWeekNumber().setText("Semaine " + week.getWeekOfYear());
		mainFrame.getWeekDetail().setText("Du " + week.getStartDate().toString() + " au " + week.getEndDate().toString());
		mainFrame.getJWeek().build();

	}
}
