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
	private DateTime startDate;
	private DateTime endDate;

	public Week(DateTime d) {
		daysList = new ArrayList<>();
		startDate = d;
		endDate = d.addDay(5);

		DateTime dayDate = new DateTime(startDate);
		Day day;
		for (int i = 0; i < 6; i++) {
			day = new Day(dayDate);
			daysList.add(day);
			dayDate = dayDate.addDay(1);
		}
		Collections.sort(daysList);
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
		return startDate.compareTo(otherWeek.getStartDate());
	}

	@Override
	public String toString() {
		String ret = "WEEK - DateBegin : " + startDate.toString() + " - DateEnd : " + endDate.toString() + "\n";
		ret += daysList.toString();
		return ret;
	}

	public int getWeekOfYear() {
		return startDate.getWeekOfYear();
	}

	public ArrayList<Day> getDaysList() {
		return daysList;
	}

	public void setDaysList(ArrayList<Day> daysList) {
		this.daysList = daysList;
	}

	public DateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}
}
