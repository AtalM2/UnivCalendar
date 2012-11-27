/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.calendar;

import univ.util.DateTime;
import java.util.ArrayList;
import java.util.Collections;

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
		for (int i = 0; i < weeksList.size(); i++) {
			week = weeksList.get(i);
			if (date.inWeek(week)) {
				finded = true;
				break;
			}
		}
		if (!finded) {
			dayOfWeek = date.getDayOfWeek();
			if(dayOfWeek != 1) {
				// Si on est pas lundi, on soustrait la position du jour
				date = date.addDay(-(dayOfWeek-1));
			}
			week = new Week(date);
			weeksList.add(week);
			Collections.sort(weeksList);
		}
		return week;
	}

	public String toString() {
		String ret = "CALENDAR\n";
		ret += weeksList.toString();
		return ret;
	}
}