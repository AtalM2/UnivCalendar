package univ.view;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import univ.calendar.Day;
import univ.calendar.Event;
import univ.calendar.EventInfos;
import univ.util.DateTime;
import univ.util.Tools;

/**
 * Classe gérant l'affichage d'un jour dans le calendrier
 *
 * @authors Noémi Salaün, Joseph Lark
 */
class JCalendarDay extends JPanel {

	// Découpage en tranche de 15min de 7h à 22h
	private int START_HOUR;
	private int END_HOUR;
	private int MINUTES_BY_SPLIT;
	private int NB_SPLIT;
	private ArrayList<EventInfos[]> checkList; // Tableau représentant l'ensemble des plages horaires affichées
	private ArrayList<EventInfos> eventsList; // Tableau comprenant la liste des Events du jour
	private JLabel title;
	private JPanel content;

	public JCalendarDay(int start_hour, int end_hour, int minutes_by_split) {
		super(new MigLayout("insets 0, gapy 0", "[grow]", "[0:30px:30px][grow]"));

		START_HOUR = start_hour;
		END_HOUR = end_hour;
		MINUTES_BY_SPLIT = minutes_by_split;
		NB_SPLIT = (END_HOUR - START_HOUR) * 60 / MINUTES_BY_SPLIT;

		eventsList = new ArrayList<>();
		checkList = new ArrayList<>();
		JPanel header = new JPanel();
		add(header, "grow, cell 0 0");
		header.setBorder(BorderFactory.createLineBorder(Color.black));
		content = new JPanel(new MigLayout("insets 4px 0px 5px 5px, gapy 0", "[1px][grow]", "[grow, 0:5px:30px]"));
		add(content, "grow, cell 0 1");
		content.setBorder(BorderFactory.createLineBorder(Color.black));
		title = new JLabel();
		header.add(title);
		// La première colonne de la grille du layout est rempli avec des Panels de taille 1*1 pour fixer la grille
		for (int row = 0; row < NB_SPLIT; row++) {
			content.add(new JPanel(), "width 1px:1px:1px, grow, cell 0 " + row);
		}
	}

	/**
	 * Ajout d'une Day, avec parcourt des différents Events et gestion des
	 * Events en conflit
	 *
	 * @param day La Day à ajouter
	 * @param color La couleur d'affichage des Events
	 */
	public void addDay(Day day, Color color) {
		int startHour, startMin, endHour, endMin, startPosition, endPosition;
		DateTime date = day.getDate();
		String dayName = date.getDayOfWeek(true) + " " + date.toString();

		title.setText(dayName);
		EventInfos eventInfos, tempEvent;
		int col, row;
		boolean empty, done;
		// On parcourt tous les Events de la Day passé en paramètre
		for (Event event : day.getEventsList()) {
			startHour = event.getStartTime().getHour();
						startMin = event.getStartTime().getMinute();
			endHour = event.getEndTime().getHour();
			endMin = event.getEndTime().getMinute();
			startPosition = (startHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(startMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;
			startPosition = startPosition > 0 ? startPosition : 0;
			endPosition = (endHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(endMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;
			endPosition = endPosition < NB_SPLIT ? endPosition : NB_SPLIT;

			eventInfos = new EventInfos(event, 0, 0, color);
			col = 0;
			row = startPosition;
			done = false;
			if (startHour < START_HOUR || endHour > END_HOUR) {
				done = true;
			}

			while (!done) {
				// On vérifie si le tableau contient assez de colonnes
				if (checkList.size() < col + 1) {
					checkList.add(new EventInfos[NB_SPLIT]);
				}
				empty = true;
				// On vérifie que la colonne courante est bien vide là où on veut ajouter l'Event
				while (empty && row < endPosition) {
					empty = checkList.get(col)[row] == null;
					if (!empty) {
						// Si l'event est déjà présent sur Google (même UID) on le considère DONE
						if (checkList.get(col)[row].getEvent().getUid().equals(event.getUid())) {
							done = true;
						}
					}
					row++;
				}
				// Si la position est libre, on ajoute l'Event
				if (empty) {
					eventInfos.setWidth(col + 1);
					eventInfos.setColumn(col + 1);
					eventsList.add(eventInfos); // On ajoute l'Event à la liste des Events
					// Pour chaque Event de la checkList on met à jour les largeurs
					for (int i = 0; i < checkList.size(); i++) {
						for (int j = startPosition; j < endPosition; j++) {
							if (i == col) {
								// Si c'est la colonne courante, on ajoute simplement l'Event courant
								checkList.get(i)[j] = eventInfos;
							} else {
								// Si c'est une autre colonne, on renseigne le fait qu'il y a des Events simultanés
								tempEvent = checkList.get(i)[j];
								if (tempEvent != null) {
									tempEvent.setWidth(col + 1);
								}
							}
						}
					}
					done = true;
				}
				// Si la position n'est pas libre, on passe à la colonne suivante
				col++;
			}
		}
	}

	/**
	 * Construit l'affichage du calendrier
	 */
	public void build() {
		int col = 0;
		// D'abord on le vide
		for (Component component : getComponents()) {
			if (component.getClass() == JCalendarEvent.class) {
				remove(component);
			}
		}
		// Puis on le rempli
		for (EventInfos ev : eventsList) {
			addEvent(ev);
			col++;
		}
	}

	/**
	 * Ajout d'un Event dans l'IHM
	 * 
	 * @param ev L'Event avec ses infos complémentaires
	 */
	private void addEvent(EventInfos ev) {
		Event event = ev.getEvent();
		int startHour = event.getStartTime().getHour();
		int startMin = event.getStartTime().getMinute();
		int endHour = event.getEndTime().getHour();
		int endMin = event.getEndTime().getMinute();
		int maxCol = checkList.size();

		int startPosition = (startHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(startMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;
		int endPosition = (endHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(endMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;

		JCalendarEvent jEvent = new JCalendarEvent(event, ev.getColor());
		content.add(jEvent, "width 0:100%:100%, grow, cell " + ev.getColumn() + " " + startPosition + " " + maxCol / ev.getWidth() + " " + (endPosition - startPosition));
	}
}
