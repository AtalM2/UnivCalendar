package univ.google;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import univ.calendar.Calendar;
import univ.calendar.Day;
import univ.calendar.Event;
import univ.calendar.Week;
import univ.util.DateTime;

/**
 * Classe permettant de parser les évènements Google vers notre modèle
 * univ.calendar
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class GGLParser {

	// The base URL for a user's calendar metafeed (needs a username appended).
	private static final String METAFEED_URL_BASE =
			"https://www.google.com/calendar/feeds/";
	// The string to add to the user's metafeedUrl to access the event feed for
	// their primary calendar.
	private static final String EVENT_FEED_URL_SUFFIX = "/private/full";
	// The URL for the metafeed of the specified user.
	// (e.g. http://www.google.com/feeds/calendar/jdoe@gmail.com)
	private static URL metafeedUrl = null;
	// The URL for the event feed of the specified user's primary calendar.
	// (e.g. http://www.googe.com/feeds/calendar/jdoe@gmail.com/private/full)
	private static URL eventFeedUrl = null;

	public static Calendar parse(CalendarService service) {
		Calendar calendar = new Calendar();


		String userName = "atal.univ.nantes@gmail.com";
		String userPassword = "jnatal44";

		// Create the necessary URL objects.
		try {
			metafeedUrl = new URL(METAFEED_URL_BASE + userName);
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ EVENT_FEED_URL_SUFFIX);
		} catch (MalformedURLException e) {
			// Bad URL
			System.err.println("Uh oh - you've got an invalid URL.");
			e.printStackTrace();
		}

		try {
			service.setUserCredentials(userName, userPassword);

			CalendarEventFeed resultFeed = service.getFeed(eventFeedUrl,
					CalendarEventFeed.class);

			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				CalendarEventEntry entry = resultFeed.getEntries().get(i);

				Event currentEvent = new Event();

				List<When> times = entry.getTimes();
				if (!times.isEmpty()) {
					String date = times.get(0).getStartTime().toString();
					if (date.length() > 10) {
						DateTime datetime = new DateTime("000000000000000");
						datetime.setYear(Integer.parseInt(date.substring(0, 4)));
						datetime.setMonth(Integer.parseInt(date.substring(5, 7)));
						datetime.setDay(Integer.parseInt(date.substring(8, 10)));

						datetime.setHour(Integer.parseInt(date.substring(11, 13)));
						datetime.setMinute(Integer.parseInt(date.substring(14, 16)));
						datetime.setSecond(Integer.parseInt(date.substring(17, 19)));

						DateTime datetimeEnd = new DateTime(datetime);
						if (date.length() > 10) {
							datetimeEnd.setHour(Integer.parseInt(date.substring(11, 13)) + 1);
						}

						String dateEnd = times.get(0).getEndTime().toString();
						if (dateEnd != null) {
							datetimeEnd.setYear(Integer.parseInt(dateEnd.substring(0, 4)));
							datetimeEnd.setMonth(Integer.parseInt(dateEnd.substring(5, 7)));
							datetimeEnd.setDay(Integer.parseInt(dateEnd.substring(8, 10)));
							if (dateEnd.length() > 10) {
								datetimeEnd.setHour(Integer.parseInt(dateEnd.substring(11, 13)));
								datetimeEnd.setMinute(Integer.parseInt(dateEnd.substring(14, 16)));
								datetimeEnd.setSecond(Integer.parseInt(dateEnd.substring(17, 19)));
							}
						}

						Week currentWeek = null;
						Day currentDay = null;
						currentEvent.setStartTime(datetime);
						currentEvent.setEndTime(datetimeEnd);
						if (currentDay == null || !currentEvent.inDay(currentDay)) {
							currentWeek = calendar.findWeek(datetime);
							currentDay = currentWeek.findDay(datetime);
						}
						currentDay.getEventsList().add(currentEvent);
						Collections.sort(currentDay.getEventsList());
					}
					String summary = entry.getTitle().getPlainText();

					currentEvent.setSummary(summary);
					currentEvent.setDescription(null);
					currentEvent.setLocation(null);
					currentEvent.setUid(entry.getIcalUID());
					currentEvent.setCategories(null);

					//System.out.println(newEvent.toString());
				}
			}

		} catch (IOException e) {
			// Communications error
			System.err.println("There was a problem communicating with the service.");
			e.printStackTrace();
		} catch (ServiceException e) {
			// Server side error
			System.err.println("The server had a problem handling your request.");
			e.printStackTrace();
		}

		return calendar;
	}
}
