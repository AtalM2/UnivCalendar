package univ.google;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import univ.calendar.Event;

/**
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class GGLCreator {

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

	/**
	 * Helper method to create either single-instance or recurring events. For
	 * simplicity, some values that might normally be passed as parameters (such
	 * as author name, email, etc.) are hard-coded.
	 *
	 * @param service An authenticated CalendarService object.
	 * @param eventTitle Title of the event to create.
	 * @param eventContent Text content of the event to create.
	 * @return The newly-created CalendarEventEntry.
	 * @throws ServiceException If the service is unable to handle the request.
	 * @throws IOException Error communicating with the server.
	 */
	public static CalendarEventEntry createEvent(CalendarService service,
			Event event) throws ServiceException, IOException {
		CalendarEventEntry myEntry = new CalendarEventEntry();

		myEntry.setTitle(new PlainTextConstruct(event.getSummary()));
		myEntry.setContent(new PlainTextConstruct(event.getUid()));

		// If a recurrence was requested, add it. Otherwise, set the
		// time (the current date and time) and duration (30 minutes)
		// of the event.
		univ.util.DateTime start = event.getStartTime();
		univ.util.DateTime end = event.getEndTime();
		DateTime startTime = new DateTime(new Date(start.getYear(), start.getMonth(), start.getDay(), start.getHour(), start.getMinute(), start.getSecond()));
		DateTime endTime = new DateTime(new Date(end.getYear(), end.getMonth(), end.getDay(), end.getHour(), end.getMinute(), end.getSecond()));

		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);

		// Send the request and receive the response:
		return service.insert(eventFeedUrl, myEntry);
	}
//	/**
//	 * Creates a single-occurrence event.
//	 * 
//	 * @param service An authenticated CalendarService object.
//	 * @param eventTitle Title of the event to create.
//	 * @param eventContent Text content of the event to create.
//	 * @return The newly-created CalendarEventEntry.
//	 * @throws ServiceException If the service is unable to handle the request.
//	 * @throws IOException Error communicating with the server.
//	 */
//	private static CalendarEventEntry createSingleEvent(CalendarService service,
//			Event event) throws ServiceException,
//			IOException {
//		return createEvent(service, event);
//	}
}
