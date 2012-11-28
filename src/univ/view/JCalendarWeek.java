package univ.view;

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JPanel;
import univ.calendar.Day;
import univ.calendar.Week;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class JCalendarWeek extends JPanel {
	
	private ArrayList<JCalendarDay> daysList;
	
	public JCalendarWeek() {
		super(new GridLayout(0,7));
		daysList = new ArrayList<>();
		for (int i=0; i<7 ;i++) {
			daysList.add(new JCalendarDay());
		}
		for (JCalendarDay day : daysList) {
			add(day);
		}
	}
	
	public void addWeek(Week c) {
		for (Day day : c.getDaysList()) {
			JCalendarDay jDay = daysList.get(day.getDayOfWeek()-1);
			jDay.addDay(day);
		}
	}
}
