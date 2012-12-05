package univ.calendar;

import java.awt.Color;

/**
 * Classe permettant d'ajouter des informations à un event. Ces infos seront
 * utilisées par l'IHM pour mettre en forme le calendrier
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class EventInfos {

	public static final Color GOOGLE_UNIV = new Color(0, 128, 210);
	public static final Color ICS_UNIV = new Color(65, 185, 255);
	public static final Color GOOGLE_EVENT = new Color(140, 140, 140);
	private Event event;
	private int column;
	/**
	 * Index de la colonne d'affichage de l'Event *
	 */
	private int width;
	/**
	 * Largeur de l'event, pour gérer les Events simultanés *
	 */
	private boolean selected;

	/**
	 * Permet de savoir quel Event on garde en cas d'Events simultanés *
	 */
	public EventInfos(Event e, int c, int w, boolean s) {
		event = e;
		column = c;
		width = w;
		selected = s;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean isSelected) {
		this.selected = isSelected;
	}
}
