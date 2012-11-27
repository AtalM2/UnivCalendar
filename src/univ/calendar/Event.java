/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.calendar;

import univ.util.DateTime;


/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class Event implements Comparable {
		
	private DateTime dateStart;
	private DateTime dateEnd;
	private String uid;
	private String summary;
	private String location;
	private String description;
	private String categories;
	
	public Event() {	
	}
	
	public boolean inDay(Day day) {
		return (dateStart.compareTo(day.getDate()) == 0);
	}
	
	@Override
	public int compareTo(Object t) {
		Event otherEvent = (Event) t;
		return dateStart.compareTo(otherEvent.getDateStart(), true);
	}
	
	@Override
	public String toString() {
		String ret = "\t\tEVENT - DateStart : " + dateStart.toString(true) + " - DateEnd : " + dateEnd.toString(true) + "\n";
		ret += "\t\t\t UID : " + uid + "\n";
		ret += "\t\t\t Summary : " + summary + "\n";
		ret += "\t\t\t Location : " + location + "\n";
		ret += "\t\t\t Description : " + description + "\n";
		ret += "\t\t\t Categories : " + categories + "\n";
		return ret;
	}

	public DateTime getDateStart() {
		return dateStart;
	}

	public void setDateStart(DateTime dateStart) {
		this.dateStart = dateStart;
	}

	public DateTime getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(DateTime dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}		
}
