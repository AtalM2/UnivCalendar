package univ.calendar;

import java.util.ArrayList;
import java.util.Collections;
import univ.util.DateTime;

/**
 * Classe représentant un calendrier complet. Ce calendrier correspond soit à l'ICS,
 * soit aux évènements Google. Un calendrier contient la liste des semaines.
 * 
 * @authors Noémi Salaün, Joseph Lark
 */
public class Calendar {

	private ArrayList<Week> weeksList;

	public Calendar() {
		weeksList = new ArrayList<>();
	}

	/**
	 * Permet de récupérer la Week contenant la DateTime. La Week est créée si
	 * elle n'est pas trouvée
	 * 
	 * @param date La date que doit contenir la Week à trouver
	 * @return La Week correspondante
	 */
	public Week findWeek(DateTime date) {
		int dayOfWeek;
		boolean finded = false;
		Week week = null;
		// On parcourt l'ensemble des Weeks du Calendar pour voir si elle existe déjà
		for (int i = 0; i < getWeeksList().size(); i++) {
			week = getWeeksList().get(i);
			if (date.inWeek(week)) {
				finded = true;
				break;
			}
		}
		// Si elle n'est pas trouvée, on la crée
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
	
	/**
	 * Permet de fusionner un Calendar avec le Calendar courant
	 * @param otherCalendar Le calendar à fusionner
	 */
	public void merge(Calendar otherCalendar) {
		Week currentWeek;
		Day currentDay;
		for (Week week : otherCalendar.getWeeksList()) {
			for (Day day : week.getDaysList()) {
				for (Event event : day.getEventsList()) {
					DateTime date = event.getStartTime();
					currentWeek = findWeek(date);
					currentDay = currentWeek.findDay(date);
					currentDay.getEventsList().add(event);
					Collections.sort(currentDay.getEventsList());
				}
				
			}
		}
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
