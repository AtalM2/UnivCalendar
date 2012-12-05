/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package univ.view.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import univ.view.JCalendarEvent;

/**
 * Listener qui gère la sélection d'un évènement
 *
 * @authors Noémi Salaün, Joseph Lark
 */
public class MouseSelectListener implements MouseListener {

	private JCalendarEvent event;

	public MouseSelectListener(JCalendarEvent e) {
		event = e;
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		if (!event.isSelected()) {
			event.getParentDay().setSelected(event);
		}
	}

	@Override
	public void mousePressed(MouseEvent me) {
	}

	@Override
	public void mouseReleased(MouseEvent me) {
	}

	@Override
	public void mouseEntered(MouseEvent me) {
	}

	@Override
	public void mouseExited(MouseEvent me) {
	}
}
