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
	
	public GGLAction(Event e, String t) {
		if (t.equals(UPDATE) || t.equals(INSERT) || t.equals(DELETE)) {
			type = t;
		} else {
			type = UPDATE;
		}
		event = e;
	}
	
	@Override
	public String toString() {
		return "Type : " + type + "\n" + event.toString();
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
	
}
