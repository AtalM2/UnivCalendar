package univ.view;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import univ.calendar.Event;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
class JCalendarEvent extends JPanel {
	
	private Event event;
	
	public JCalendarEvent(Event e) {
		super();
		event = e;
		setBackground(Color.blue);
		add(new JLabel(event.getStartTime().getHour()+" -fsdf sdf sdf sdf sd fsd sd fsd fs d fds f "+event.getEndTime().getHour()));
	}
	
}
