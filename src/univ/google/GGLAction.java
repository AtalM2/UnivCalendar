/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.google;

import univ.calendar.Event;

/**
 * Classe d�finissant les appels aux actions possibles sur le google calendar.
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class GGLAction {

	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	public static final String INSERT = "insert";
	private String type;
	private Event event;
	private Event oldEvent;

	public GGLAction(Event e, String t) {
		this(e, t, null);
	}

	public GGLAction(Event e, String t, Event old) {
		event = e;
		if (t.equals(INSERT) || t.equals(DELETE)) {
			type = t;
		} else {
			type = UPDATE;
			if (old != null) {
				oldEvent = old;
			}
		}
	}

	@Override
	public String toString() {
		if (type.equals(UPDATE) && oldEvent != null) {
			return "Type : " + type + "\n\tOLD : " + oldEvent.toString() + "\n\tNEW : " + event.toString();
		} else {
			return "Type : " + type + "\n" + event.toString();
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type.equals(UPDATE) || type.equals(INSERT) || type.equals(DELETE)) {
			this.type = type;
		} else {
			this.type = UPDATE;
		}
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Event getOldEvent() {
		return oldEvent;
	}

	public void setOldEvent(Event oldEvent) {
		this.oldEvent = oldEvent;
	}
}
