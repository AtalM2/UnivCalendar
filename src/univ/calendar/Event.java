package univ.calendar;

import univ.util.DateTime;

/**
 * Classe représentant un évènement. Il peut s'agir d'un évènement Google ou
 * d'un cours de l'ICS.
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class Event implements Comparable {

	public static final String TYPE_UNIV_ICS = "univ-ics";
	public static final String TYPE_UNIV_GGL = "univ-ggl";
	public static final String TYPE_EVENT_GGL = "event-ggl";
	/**
	 * Attribut public utilisé lors du parcourt des Calendar pour la fusion *
	 */
	public boolean checked;
	/**
	 * Heure de début de l'évènement
	 */
	private DateTime startTime;
	/**
	 * Heure de fin de l'évènement
	 */
	private DateTime endTime;
	private String uid;
	private String summary;
	private String location;
	private String description;
	private String type;

	public Event() {
		checked = false;
		uid = "";
		summary = "";
		location = "";
		description = "";
		type = TYPE_EVENT_GGL;
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

	public boolean equals(Event event) {
		return (event.getUid().trim().equals(uid.trim())
				&& event.getDescription().trim().equals(description.trim())
				&& (event.getEndTime().compareTo(endTime, true) == 0)
				&& (event.getStartTime().compareTo(startTime, true) == 0)
				&& event.getLocation().trim().equals(location.trim())
				&& event.getSummary().trim().equals(summary.trim()));
	}

	@Override
	public String toString() {
		String ret = "\t\tEVENT - DateStart : " + startTime.toString(true) + " - DateEnd : " + endTime.toString(true) + "\n";
		ret += "\t\t\t Type : " + type + "\n";
		ret += "\t\t\t UID : " + uid + "\n";
		ret += "\t\t\t Summary : " + summary + "\n";
		ret += "\t\t\t Location : " + location + "\n";
		ret += "\t\t\t Description : " + description + "\n";
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type == null) {
			this.type = TYPE_EVENT_GGL;
		} else {
			if (!type.equals(TYPE_UNIV_GGL) && !type.equals(TYPE_UNIV_ICS)) {
				this.type = TYPE_EVENT_GGL;
			} else {
				this.type = type;
			}
		}
	}
}
