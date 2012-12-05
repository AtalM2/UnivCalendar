package univ;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import univ.calendar.Calendar;
import univ.calendar.Day;
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
//		String adresse = "univcalendar@gmail.com";
//		String passwd = "lolcalendar";

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
		Calendar calGoogleCours = GGLParser.parse(myService, true);
//		System.out.println(calGoogleCours.toString());
		
		Calendar calGoogleNotCours = GGLParser.parse(myService, false);
//		System.out.println(calGoogleNotCours.toString());
		
		Event event = new Event();
		event.setStartTime(new DateTime("20121203_200000"));
		event.setEndTime(new DateTime("20121203_210000"));
		event.setSummary("Le summary de l event\\avec - des tirets,");
		event.setUid("CELCAT_UID_DE_L_EVENT");
		event.setLocation("la salle");
		event.setDescription("la descritpnio");
		event.setCategories("la categorie");
		
		CalendarEventEntry cee = new CalendarEventEntry();

//		ArrayList<Week> wl = calendar.getWeeksList();
//		for (int i = 0 ; i < wl.size() ; i++){
//			Week w = wl.get(i);
//			ArrayList<Day> dl = w.getDaysList();
//			for (int j = 0 ; j < dl.size() ; j++){
//				Day d = dl.get(j);
//				ArrayList<Event> el = d.getEventsList();
//				for (int k = 0 ; k < el.size() ; k++){
//					Event currentEvent = el.get(k);
//					try {
//						GGLCreator.updateEvent(myService, currentEvent);
//					} catch (ServiceException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
		
//		try {
//			GGLCreator.updateEvent(myService, event);
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		calendar.update(calGoogleCours);
		calendar.merge(calGoogleNotCours);
		System.out.println(calendar.getGglAction().toString());
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		DateTime now = new DateTime();
		Week week = calGoogleCours.findWeek(now);
		mainFrame.getJWeek().addWeek(week);
		mainFrame.getWeekNumber().setText("Semaine " + week.getWeekOfYear());
		mainFrame.getWeekDetail().setText("Du " + week.getStartDate().toString() + " au " + week.getEndDate().toString());
		mainFrame.getJWeek().build();

	}
}
