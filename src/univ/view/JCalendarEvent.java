package univ.view;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import univ.calendar.Event;
import univ.calendar.EventInfos;

/**
 * Classe gérant l'affichage d'un Event dans le calendrier
 *
 * @authors Noémi Salaün, Joseph Lark
 */
class JCalendarEvent extends JPanel {

	private Event event;
	private Color color;
	private JLabel labelSummary;
	private JLabel labelLocation;

	public JCalendarEvent(Event e, boolean isSelected) {
		super(new MigLayout("wrap"));
		event = e;
		switch (event.getType()) {
			case Event.TYPE_EVENT_GGL:
				color = EventInfos.GOOGLE_EVENT;
				break;
			case Event.TYPE_UNIV_GGL:
				color = EventInfos.GOOGLE_UNIV;
				break;
			case Event.TYPE_UNIV_ICS:
				color = EventInfos.ICS_UNIV;
				break;
			default:
				color = EventInfos.GOOGLE_EVENT;

		}
		String startHour, startMinutes, endHour, endMinutes;
		String summary = event.getSummary();
		summary = summary.replace("\\,", ",");

		String description = event.getDescription();
		description = description.replace("\\n", "<br/>");
		description = description.replace("\\,", ",");

		String location = event.getLocation();

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
		labelSummary = new JLabel(summary);
		labelLocation = new JLabel(location);
		add(labelSummary);
		add(labelLocation);
		select(isSelected);
	}

	public void select(boolean select) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		if (select) {
			setBackground(new Color(red, green, blue));
			labelSummary.setForeground(Color.BLACK);
			labelLocation.setForeground(Color.BLACK);
		} else {
			setBackground(new Color(red, green, blue, 150));
			labelSummary.setForeground(new Color(0, 0, 0, 150));
			labelLocation.setForeground(new Color(0, 0, 0, 150));
		}
	}
}
