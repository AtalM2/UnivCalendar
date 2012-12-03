package univ.view;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import univ.calendar.Event;

/**
 * Classe gérant l'affichage d'un Event dans le calendrier
 *
 * @authors Noémi Salaün, Joseph Lark
 */
class JCalendarEvent extends JPanel {

	private Event event;

	public JCalendarEvent(Event e, Color color) {
		super(new MigLayout("wrap"));
		event = e;
		String startHour, startMinutes, endHour, endMinutes;
		String summary = event.getSummary();
		summary = summary.replace("\\,", ",");

		String description = event.getDescription();
		description = description.replace("\\n", "<br/>");
		description = description.replace("\\,", ",");

		startHour = new Integer(event.getStartTime().getHour()).toString();
		startMinutes = new Integer(event.getStartTime().getMinute()).toString();
		endHour = new Integer(event.getEndTime().getHour()).toString();
		endMinutes = new Integer(event.getEndTime().getMinute()).toString();
		if (startHour.length() < 2) {
			startHour = "0" + startHour;
		}
		if (startMinutes.length() < 2) {
			startMinutes = "0" + startMinutes;
		}
		if (endHour.length() < 2) {
			endHour = "0" + endHour;
		}
		if (endMinutes.length() < 2) {
			endMinutes = "0" + endMinutes;
		}
		setToolTipText("<html>" + event.getStartTime().toString() + "<br/><b>De " + startHour + ":" + startMinutes + " à " + endHour + ":" + endMinutes + "<br/><br/>"
				+ summary + "</b><br/><br/>"
				+ description);
		setBackground(color);
		JLabel labelSummary = new JLabel(summary);
		add(labelSummary);
		add(new JLabel(event.getLocation()));
	}
}
