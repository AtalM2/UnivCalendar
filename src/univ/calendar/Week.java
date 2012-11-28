package univ.calendar;

import univ.util.DateTime;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class Week implements Comparable {

	private ArrayList<Day> daysList;
	private DateTime dateBegin;
	private DateTime dateEnd;

	public Week(DateTime d) {
		daysList = new ArrayList<>();
		dateBegin = d;
		DateTime end = d.addDay(6);
		dateEnd = new DateTime(end);
	}

	public Day findDay(DateTime date) {
		boolean finded = false;
		Day day = null;
		for (int i = 0; i < daysList.size(); i++) {
			day = daysList.get(i);
			if (date.inDay(day)) {
				finded = true;
				break;
			}
		}
		if (!finded) {
			day = new Day(date);
			daysList.add(day);
			Collections.sort(daysList);
		}
		return day;
	}

	@Override
	public int compareTo(Object t) {
		Week otherWeek = (Week) t;
		return dateBegin.compareTo(otherWeek.getDateBegin());
	}
	
	@Override
	public String toString() {
		String ret = "WEEK - DateBegin : " + dateBegin.toString() + " - DateEnd : " + dateEnd.toString() + "\n";
		ret += daysList.toString();
		return ret;
	}
	
	public int getWeekOfYear() {
		return dateBegin.getWeekOfYear();
	}

	public ArrayList<Day> getDaysList() {
		return daysList;
	}

	public void setDaysList(ArrayList<Day> daysList) {
		this.daysList = daysList;
	}

	public DateTime getDateBegin() {
		return dateBegin;
	}

	public void setDateBegin(DateTime dateBegin) {
		this.dateBegin = dateBegin;
	}

	public DateTime getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(DateTime dateEnd) {
		this.dateEnd = dateEnd;
	}
}
