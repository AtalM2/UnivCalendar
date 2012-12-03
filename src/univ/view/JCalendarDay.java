package univ.view;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import univ.calendar.Day;
import univ.calendar.Event;
import univ.util.DateTime;
import univ.util.Tools;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
class JCalendarDay extends JPanel {

	// Découpage en tranche de 15min de 7h à 22h
	private int START_HOUR;
	private int END_HOUR;
	private int MINUTES_BY_SPLIT;
	private int NB_SPLIT;
	private ArrayList<EventInfos[]> checkList;
	private ArrayList<EventInfos> eventsList;
	private JLabel title;
	private JPanel content;

	class EventInfos {

		EventInfos(Event e, int c, int w, Color co) {
			event = e;
			column = c;
			width = w;
			color = co;
		}
		Color color;
		Event event;
		int column;
		int width;
	}

	public JCalendarDay(int start_hour, int end_hour, int minutes_by_split) {
		super(new MigLayout("insets 0, gapy 0", "[grow]", "[0:30px:30px][grow]"));

		START_HOUR = start_hour;
		END_HOUR = end_hour;
		MINUTES_BY_SPLIT = minutes_by_split;
		NB_SPLIT = (END_HOUR - START_HOUR) * 60 / MINUTES_BY_SPLIT;

		eventsList = new ArrayList<>();
		checkList = new ArrayList<>();
		JPanel header = new JPanel();
		add(header, "grow, cell 0 0");
		header.setBorder(BorderFactory.createLineBorder(Color.black));
		content = new JPanel(new MigLayout("insets 4px 0px 5px 5px, gapy 0", "[1px][grow]", "[grow, 0:5px:30px]"));
		add(content, "grow, cell 0 1");
		content.setBorder(BorderFactory.createLineBorder(Color.black));
		title = new JLabel();
		header.add(title);
		for (int row = 0; row < NB_SPLIT; row++) {
			content.add(new JPanel(), "width 1px:1px:1px, grow, cell 0 " + row);
		}
	}
	
	public void addDay(Day day, Color color) {
		int startHour, startMin, endHour, endMin, startPosition, endPosition;
		DateTime date = day.getDate();
		String dayName = date.getDayOfWeek(true) + " " + date.toString();

		title.setText(dayName);
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

			eventInfos = new EventInfos(event, 0, 0, color);
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
					if (!empty) {
						if(checkList.get(col)[row].event.getUid().equals(event.getUid())) {
							done = true;	// Si l'event est déjà présent sur Google (même UID) on le considère DONE
						}
					}
					row++;
				}
				if (empty) {
					eventInfos.width = col + 1;
					eventInfos.column = col + 1;
					eventsList.add(eventInfos);
					for (int i = 0; i < checkList.size(); i++) {
						for (int j = startPosition; j < endPosition; j++) {
							if (i == col) {
								checkList.get(i)[j] = eventInfos;
							} else {
								tempEvent = checkList.get(i)[j];
								if (tempEvent != null) {
									tempEvent.width = col + 1;
								}
							}
						}
					}
					done = true;
				}
				col++;
			}
		}
	}

	public void build() {
		int col = 0;
		for (Component component : getComponents()) {
			if (component.getClass() == JCalendarEvent.class) {
				remove(component);
			}
		}
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

		JCalendarEvent jEvent = new JCalendarEvent(event, ev.color);
		content.add(jEvent, "width 0:100%:100%, grow, cell " + ev.column + " " + startPosition + " " + maxCol / ev.width + " " + (endPosition - startPosition));
	}
}
