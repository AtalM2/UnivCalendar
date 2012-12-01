/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ggl;

import univ.calendar.Calendar;
import univ.calendar.Day;
import univ.calendar.Event;
import univ.calendar.Week;
import univ.util.DateTime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;





import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.ServiceException;

import univ.calendar.Calendar;
import univ.ics.ICSFinder;
import univ.ics.ICSParser;





import com.google.gdata.client.Query;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
//import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.calendar.CalendarEntry;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.calendar.CalendarFeed;
import com.google.gdata.data.calendar.WebContent;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.Reminder.Method;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
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
	
	
	public static Calendar parse(CalendarService service){
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
//      System.out.println("Print myService");
      
      CalendarEventFeed resultFeed = service.getFeed(eventFeedUrl,
    	        CalendarEventFeed.class);
      
      for (int i = 0; i < resultFeed.getEntries().size(); i++) {
          CalendarEventEntry entry = resultFeed.getEntries().get(i);
          List<When> times = entry.getTimes();
          String date = times.get(0).getStartTime().toString();
          System.out.println(date);
          
          
          DateTime datetime = new DateTime("000000000000000");
          DateTime datetimeEnd = new DateTime("000000000000000");
          
          datetime.setYear(Integer.parseInt(date.substring(0, 4)));
          datetime.setMonth(Integer.parseInt(date.substring(5, 7)));
          datetime.setDay(Integer.parseInt(date.substring(8, 10)));
          if (date.length() > 10){
	          datetime.setHour(Integer.parseInt(date.substring(11, 13)));
	          datetime.setMinute(Integer.parseInt(date.substring(14, 16)));
	          datetime.setSecond(Integer.parseInt(date.substring(17, 19)));
          }
          String summary = entry.getTitle().getPlainText();
          
          Week currentWeek = null;
          Day currentDay = null;
          Event currentEvent = new Event();
          
          currentEvent.setStartTime(datetime);
          
          if (currentDay == null || !currentEvent.inDay(currentDay)) {
				currentWeek = calendar.findWeek(datetime);
				currentDay = currentWeek.findDay(datetime);
			}
			currentDay.getEventsList().add(currentEvent);
			Collections.sort(currentDay.getEventsList());
          
          currentEvent.setSummary(summary);
          currentEvent.setEndTime(datetimeEnd);
          currentEvent.setDescription(null);
          currentEvent.setLocation(null);
          currentEvent.setUid(null);
          currentEvent.setCategories(null);
          
//          System.out.println(newEvent.toString());
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
