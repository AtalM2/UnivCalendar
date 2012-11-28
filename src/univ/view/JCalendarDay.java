package univ.view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import univ.calendar.Day;
import univ.calendar.Event;
import univ.util.Tools;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
class JCalendarDay extends JPanel {

	// Découpage en tranche de 15min de 7h à 22h
	private final int START_HOUR = 7;
	private final int END_HOUR = 21;
	private final int MINUTES_BY_SPLIT = 15;
	private final int NB_SPLIT = (END_HOUR - START_HOUR) * 60 / MINUTES_BY_SPLIT;
	private ArrayList<EventInfos[]> checkList;
	private ArrayList<EventInfos> eventsList;

	class EventInfos {

		EventInfos(Event e, int c, int w) {
			event = e;
			column = c;
			width = w;
		}
		Event event;
		int column;
		int width;
	}

	public JCalendarDay() {
		super(new MigLayout("insets 0, gapy 0", "[0%][grow]", "[grow, 0:5px:30px]"));
		eventsList = new ArrayList<>();
		checkList = new ArrayList<>();
		Random rand = new Random();
		int red = rand.nextInt(255);
		int green = rand.nextInt(255);
		int blue = rand.nextInt(255);
		setBackground(new Color(red, green, blue));
		for (int row = 0; row < NB_SPLIT; row++) {
			JLabel panel = new JLabel(" > ");
			red = rand.nextInt(255);
			green = rand.nextInt(255);
			blue = rand.nextInt(255);
			panel.setBackground(new Color(red, green, blue));
			add(new JPanel(), "grow, cell 0 " + row);
		}
	}

	public void addDay(Day day) {
		int startHour, startMin, endHour, endMin, startPosition, endPosition;
		EventInfos eventInfos, tempEvent;
		int col, row;
		boolean empty, done;
		for (Event event : day.getEventsList()) {
			startHour = event.getStartTime().getHour();
			startMin = event.getStartTime().getMinute();
			endHour = event.getEndTime().getHour();
			endMin = event.getEndTime().getMinute();
			startPosition = (startHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(startMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;
			startPosition = startPosition > 0 ? startPosition : 0;
			endPosition = (endHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(endMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;
			endPosition = endPosition < NB_SPLIT ? endPosition : NB_SPLIT;

			eventInfos = new EventInfos(event, 0, 0);
			col = 0;
			row = startPosition;
			done = false;

			while (!done) {
				if (checkList.size() < col + 1) {
					checkList.add(new EventInfos[NB_SPLIT]);
				}
				empty = true;
				while (empty && row < endPosition) {
					empty = checkList.get(col)[row] == null;
					row++;
				}
				if (empty) {
					eventInfos.width = col+1;
					eventInfos.column = col+1;
					eventsList.add(eventInfos);
					for (int i = 0; i < checkList.size(); i++) {
						for (int j = startPosition; j < endPosition; j++) {
							if (i == col) {
								checkList.get(col)[j] = eventInfos;
							} else {
								tempEvent = checkList.get(i)[j];
								if (tempEvent != null) {
									tempEvent.width = col+1;
								}
							}
						}
					}
					done = true;
				}
				col++;
			}
		}
		col = 0;
		for (EventInfos ev : eventsList) {
			addEvent(ev);
			col++;
		}
	}

	private void addEvent(EventInfos ev) {
		Event event = ev.event;
		int startHour = event.getStartTime().getHour();
		int startMin = event.getStartTime().getMinute();
		int endHour = event.getEndTime().getHour();
		int endMin = event.getEndTime().getMinute();
		int maxCol = checkList.size();

		int startPosition = (startHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(startMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;
		int endPosition = (endHour - START_HOUR) * (60 / MINUTES_BY_SPLIT) + Tools.floor(endMin, MINUTES_BY_SPLIT) / MINUTES_BY_SPLIT;

		JCalendarEvent jEvent = new JCalendarEvent(event);
		add(jEvent, "width 0:80%:80%, grow, cell " + ev.column + " " + startPosition + " " + maxCol/ev.width + " " + (endPosition - startPosition));
	}
}
