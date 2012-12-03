package univ.calendar;

import java.util.ArrayList;
import java.util.Collections;
import univ.util.DateTime;

/**
 * Classe représentant une semaine. Elle contient la liste des jours qui la composent.
 *
 * @author @authors Noémi Salaün, Joseph Lark
 */
public class Week implements Comparable {

	private ArrayList<Day> daysList;
	private DateTime startDate;
	private DateTime endDate;

	public Week(DateTime d) {
		daysList = new ArrayList<>();
		startDate = d;
		endDate = d.addDay(6);

		DateTime dayDate = new DateTime(startDate);
		Day day;
		// On initialise la semaine avec ses jours
		for (int i = 0; i < 6; i++) {
			day = new Day(dayDate);
			daysList.add(day);
			dayDate = dayDate.addDay(1);
		}
		Collections.sort(daysList);
	}

	/**
	 * Permet de récupérer la Day contenant la DateTime. La Day est créée si
	 * elle n'est pas trouvée
	 * 
	 * @param date La date que doit contenir la Day à trouver
	 * @return La Day correspondante
	 */
	public Day findDay(DateTime date) {
		boolean finded = false;
		Day day = null;
		// On parcourt l'ensemble des Days du Calendar pour voir si elle existe déjà
		for (int i = 0; i < daysList.size(); i++) {
			day = daysList.get(i);
			if (date.inDay(day)) {
				finded = true;
				break;
			}
		}		
		// Si elle n'est pas trouvée, on la crée
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
