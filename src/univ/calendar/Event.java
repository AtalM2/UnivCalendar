package univ.calendar;

import univ.util.DateTime;

/**
 * Classe représentant un évènement. Il peut s'agir d'un évènement Google ou d'un
 * cours de l'ICS.
 *
 * @author @authors Noémi Salaün, Joseph Lark
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
		uid = "";
		summary = "";
		location = "";
		description = "";
		categories = "";
	}

	/**
	 * Vérifie si la date de l'Event correspond à la date de la Day
	 * 
	 * @param day La Day à comparer avec la date de l'Event
	 * @return Vrai sir la date correspond, faux sinon
	 */
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
		if (uid == null) {
			this.uid = "";
		} else {
			this.uid = uid;
		}
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		if (summary == null) {
			this.summary = "";
		} else {
			this.summary = summary;
		}
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		if (location == null) {
			this.location = "";
		} else {
			this.location = location;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description == null) {
			this.description = "";
		} else {
			this.description = description;
		}
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		if (categories == null) {
			this.categories = "";
		} else {
			this.categories = categories;
		}
	}
}
