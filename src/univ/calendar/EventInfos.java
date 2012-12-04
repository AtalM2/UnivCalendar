package univ.calendar;

import java.awt.Color;

/**
 * Classe permettant d'ajouter des informations à un event. Ces
 * infos seront utilisées par l'IHM pour mettre en forme le calendrier
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class EventInfos {
	
	public static final Color GOOGLE_UNIV = new Color(0, 128, 210);
	public static final Color ICS_UNIV = new Color(65, 185, 255);
	public static final Color GOOGLE_EVENT = new Color(140, 140, 140);

	private Color color; /** Couleur d'affichage de l'Event **/
	private Event event;
	private int column; /** Index de la colonne d'affichage de l'Event **/
	private int width; /** Largeur de l'event, pour gérer les Events simultanés **/
	
	public EventInfos(Event e, int c, int w, Color co) {
		event = e;
		column = c;
		width = w;
		color = co;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
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
}
