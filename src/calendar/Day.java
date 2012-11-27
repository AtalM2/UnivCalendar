/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar;

import calendar.util.DateTime;
import java.util.ArrayList;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class Day implements Comparable {

	private ArrayList<Event> eventsList;
	private DateTime date;

	public Day(DateTime d) {
		eventsList = new ArrayList<>();
		date = d;
	}

	public boolean inWeek(Week week) {
		return (date.compareTo(week.getDateBegin()) >= 1 && date.compareTo(week.getDateEnd()) <= -1);
	}

	@Override
	public int compareTo(Object t) {
		Day otherDay = (Day) t;
		return date.compareTo(otherDay.getDate());
	}

	@Override
	public String toString() {
		String ret = "\tDAY - Date : " + date.toString() + "\n";
		ret += eventsList.toString() + "\n";
		return ret;
	}

	public ArrayList<Event> getEventsList() {
		return eventsList;
	}

	public void setEventsList(ArrayList<Event> eventsList) {
		this.eventsList = eventsList;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}
}
