package univ.calendar;

import java.awt.Color;

/**
 * Classe représentant permettant d'ajouter des informations à un event. Ces
 * infos seront utilisées par l'IHM pour mettre en forme le calendrier
 *
 * @author
 * @authors Noémi Salaün, Joseph Lark
 */
public class EventInfos {

	private Color color;
	private Event event;
	private int column; // Index de la colonne d'affichage de l'Event
	private int width; // Largeur de l'event, pour gérer les Events simultanés
	
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
