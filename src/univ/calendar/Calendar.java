/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.calendar;

import java.util.ArrayList;
import java.util.Collections;
import univ.util.DateTime;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class Calendar {

	private ArrayList<Week> weeksList;

	public Calendar() {
		weeksList = new ArrayList<>();
	}

	public Week findWeek(DateTime date) {
		int dayOfWeek;
		boolean finded = false;
		Week week = null;
		for (int i = 0; i < getWeeksList().size(); i++) {
			week = getWeeksList().get(i);
			if (date.inWeek(week)) {
				finded = true;
				break;
			}
		}
		if (!finded) {
			dayOfWeek = Integer.parseInt(date.getDayOfWeek());
			if (dayOfWeek != 1) {
				// Si on est pas lundi, on soustrait la position du jour
				date = date.addDay(-(dayOfWeek - 1));
			}
			week = new Week(date);
			getWeeksList().add(week);
			Collections.sort(getWeeksList());
		}
		return week;
	}

	@Override
	public String toString() {
		String ret = "CALENDAR\n";
		ret += getWeeksList().toString();
		return ret;
	}

	public ArrayList<Week> getWeeksList() {
		return weeksList;
	}

	public void setWeeksList(ArrayList<Week> weeksList) {
		this.weeksList = weeksList;
	}
}
