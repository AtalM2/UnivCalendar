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
		
	private DateTime startTime;
	private DateTime endTime;
	private String uid;
	private String summary;
	private String location;
	private String description;
	private String categories;
	
	public Event() {	
	}
	
	public boolean inDay(Day day) {
		return (startTime.compareTo(day.getDate()) == 0);
	}
	
	@Override
	public int compareTo(Object t) {
		Event otherEvent = (Event) t;
		return startTime.compareTo(otherEvent.getStartTime(), true);
	}
	
	@Override
	public String toString() {
		String ret = "\t\tEVENT - DateStart : " + startTime.toString(true) + " - DateEnd : " + endTime.toString(true) + "\n";
		ret += "\t\t\t UID : " + uid + "\n";
		ret += "\t\t\t Summary : " + summary + "\n";
		ret += "\t\t\t Location : " + location + "\n";
		ret += "\t\t\t Description : " + description + "\n";
		ret += "\t\t\t Categories : " + categories + "\n";
		return ret;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
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
