/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.calendar;

import java.util.ArrayList;
import univ.util.DateTime;

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
		return (date.compareTo(week.getStartDate()) >= 1 && date.compareTo(week.getEndDate()) <= -1);
	}

	@Override
	public int compareTo(Object t) {
		Day otherDay = (Day) t;
		return date.compareTo(otherDay.getDate());
	}

	@Override
	public String toString() {
		String ret = "\tDAY - Date : " + date.getDayOfWeek() + " " + date.toString() + "\n";
		ret += eventsList.toString() + "\n";
		return ret;
	}

	public String getDayOfWeek() {
		return getDayOfWeek(false);
	}

	public String getDayOfWeek(boolean inText) {
		return date.getDayOfWeek(inText);
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
