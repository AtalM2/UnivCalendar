package univ.google;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import univ.calendar.Event;

/**
 * Classe des actions sur le google calendar. 
 * 
 * @authors No√©mi Sala√ºn, Joseph Lark
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
	 * Ajoute l'evenement du modele au google calendar
	 * 
	 * @param service Le calendrier google
	 * @param event L'evenement du modele
	 * @return
	 * @throws ServiceException
	 * @throws IOException
	 */
	public static CalendarEventEntry createEvent(CalendarService service,
			Event event) throws ServiceException, IOException {

//		String userName = "univcalendar@gmail.com";
		String userName = "atal.univ.nantes@gmail.com";
		//String userPassword = "jnatal44";

		CalendarEventEntry myEntry = new CalendarEventEntry();

		myEntry.setTitle(new PlainTextConstruct(event.getSummary()));
		myEntry.setContent(new PlainTextConstruct(event.getUid()  + "\n" + 
				event.getLocation() + "\n" + 
				event.getDescription() + "\n" ));

		// If a recurrence was requested, add it. Otherwise, set the
		// time (the current date and time) and duration (30 minutes)
		// of the event.
		univ.util.DateTime start = event.getStartTime();
		univ.util.DateTime end = event.getEndTime();
		DateTime startTime = new DateTime(new Date(start.getYear()-1900,start.getMonth()-1,start.getDay(),start.getHour()+1,start.getMinute(),start.getSecond()));
		DateTime endTime = new DateTime(new Date(end.getYear()-1900,end.getMonth()-1,end.getDay(),end.getHour()+1,end.getMinute(),end.getSecond()));

		When eventTimes = new When();
		eventTimes.setStartTime(startTime);
		eventTimes.setEndTime(endTime);
		myEntry.addTime(eventTimes);

		try {
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ EVENT_FEED_URL_SUFFIX);
		} catch (MalformedURLException e) {
			// Bad URL
			System.err.println("Uh oh - you've got an invalid URL.");
			e.printStackTrace();
		}
		// Send the request and receive the response:
		return service.insert(eventFeedUrl, myEntry);
	}

	/**
	 * Appel ‡ la fonction qui ajoute l'evenement du modele au google calendar
	 * 
	 * @param service Le calendrier google
	 * @param e L'evenement du modele
	 * @return
	 * @throws ServiceException
	 * @throws IOException
	 */
	public static CalendarEventEntry createSingleEvent(CalendarService service,
			Event e) throws ServiceException,
			IOException {
		return createEvent(service, e);
	}

	/**
	 * Met ‡ jour la description de l'evenement dans le google calendar
	 * 
	 * @param entry L'evenement google
	 * @param newTitle La nouvelle description
	 * @return
	 * @throws ServiceException
	 * @throws IOException
	 */
	public static CalendarEventEntry updateContent(CalendarEventEntry entry,
			String newTitle) throws ServiceException, IOException {
		entry.setTitle(new PlainTextConstruct(newTitle));
		return entry.update();
	}

	/**
	 * Met ‡ jour l'evenement en entier dans le google calendar
	 * 
	 * @param service Le calendrier google
	 * @param event L'evenement ‡ changer
	 * @throws ServiceException
	 * @throws IOException
	 */
	public static void updateEvent(CalendarService service, Event event) throws ServiceException, IOException {
		Event e = new Event();
		e.setSummary(event.getSummary());
		e.setUid(event.getUid());
		e.setStartTime(event.getStartTime());
		e.setEndTime(event.getEndTime());
		e.setDescription(event.getDescription());
		e.setLocation(event.getLocation());

//		String userName = "univcalendar@gmail.com";
		String userName = "atal.univ.nantes@gmail.com";
		
		try {
			eventFeedUrl = new URL(METAFEED_URL_BASE + userName
					+ EVENT_FEED_URL_SUFFIX);
		} catch (MalformedURLException err) {
			// Bad URL
			System.err.println("Uh oh - you've got an invalid URL.");
			err.printStackTrace();
			return;
		}
		CalendarEventFeed resultFeed = service.getFeed(eventFeedUrl,
				CalendarEventFeed.class);

		for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			CalendarEventEntry entry = resultFeed.getEntries().get(i);
			if (entry.getPlainTextContent().toString().split("\n")[0].equals(event.getUid())){
				deleteEvent(service,entry);
			}
		}

		createSingleEvent(service,e);
	}


	/**
	 * Supprime l'evenement du calendrier google
	 * 
	 * @param service Le calendrier google
	 * @param eventToDelete L'evenement ‡ supprimer
	 * @throws ServiceException
	 * @throws IOException
	 */
	public static void deleteEvent(CalendarService service,
			CalendarEventEntry eventToDelete) throws ServiceException,
			IOException {

		// Add each item in eventsToDelete to the batch request.
		CalendarEventFeed batchRequest = new CalendarEventFeed();

		// Modify the entry toDelete with batch ID and operation type.
		//BatchUtils.setBatchId(eventToDelete, String.valueOf(i));
		BatchUtils.setBatchOperationType(eventToDelete, BatchOperationType.DELETE);
		batchRequest.getEntries().add(eventToDelete);	    

		// Get the URL to make batch requests to
		CalendarEventFeed feed = service.getFeed(eventFeedUrl,
				CalendarEventFeed.class);
		Link batchLink = feed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
		URL batchUrl = new URL(batchLink.getHref());

		// Submit the batch request
		CalendarEventFeed batchResponse = service.batch(batchUrl, batchRequest);

	}
}