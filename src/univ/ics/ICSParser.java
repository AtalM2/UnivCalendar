/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.ics;

import univ.calendar.Calendar;
import univ.calendar.Day;
import univ.calendar.Event;
import univ.calendar.Week;
import univ.util.DateTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class ICSParser {

	private static final String VEVENT = "VEVENT";

	public static Calendar parse(ArrayList<String> ics) {
		Calendar calendar = new Calendar();
		String line;
		Week currentWeek = null;
		Day currentDay = null;
		Event currentEvent = null;
		for (int i = 0; i < ics.size(); i++) {
			line = ics.get(i);
			if (isBegin(line)) {
				if (getValue(line).equalsIgnoreCase(VEVENT)) {
					currentEvent = new Event();
				}
			}
			if (isDtStart(line)) {
				DateTime date = new DateTime(getValue(line));
				currentEvent.setStartTime(date);
				if (currentDay == null || !currentEvent.inDay(currentDay)) {
					currentWeek = calendar.findWeek(date);
					currentDay = currentWeek.findDay(date);
				}
				currentDay.getEventsList().add(currentEvent);
				Collections.sort(currentDay.getEventsList());
			}
			if (isDtEnd(line)) {
				currentEvent.setDateEnd(new DateTime(getValue(line)));
			}
			if (isUID(line)) {
				currentEvent.setUid(getValue(line));
			}
			if (isSummary(line)) {
				currentEvent.setSummary(getValue(line));
			}
			if (isLocation(line)) {
				currentEvent.setLocation(getValue(line));
			}
			if (isDescription(line)) {
				currentEvent.setDescription(getValue(line));
			}
			if (isCategories(line)) {
				currentEvent.setCategories(getValue(line));
			}
			if (isEnd(line)) {
				if (getValue(line).equalsIgnoreCase(VEVENT)) {
					currentEvent = null;
				}
			}
		}

		return calendar;
	}

	private static boolean isBegin(String line) {
		return line.toLowerCase().startsWith("BEGIN:".toLowerCase());
	}

	private static boolean isDtStart(String line) {
		return line.toLowerCase().startsWith("DTSTART:".toLowerCase());
	}

	private static boolean isDtEnd(String line) {
		return line.toLowerCase().startsWith("DTEND:".toLowerCase());
	}

	private static boolean isUID(String line) {
		return line.toLowerCase().startsWith("UID:".toLowerCase());
	}

	private static boolean isSummary(String line) {
		return line.toLowerCase().startsWith("SUMMARY:".toLowerCase());
	}

	private static boolean isLocation(String line) {
		return line.toLowerCase().startsWith("LOCATION:".toLowerCase());
	}

	private static boolean isDescription(String line) {
		return line.toLowerCase().startsWith("DESCRIPTION:".toLowerCase());
	}

	private static boolean isCategories(String line) {
		return line.toLowerCase().startsWith("CATEGORIES:".toLowerCase());
	}
	
	private static boolean isEnd(String line) {
		return line.toLowerCase().startsWith("END:".toLowerCase());
	}

	private static String getValue(String line) {
		int position = line.indexOf(":");
		return line.substring(position + 1, line.length());
	}
}
