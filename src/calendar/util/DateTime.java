/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calendar.util;

import calendar.Day;
import calendar.Week;

/**
 *
 * @author Noémi Salaün <noemi.salaun@etu.univ-nantes.fr>
 */
public class DateTime {

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;

	public DateTime(String dateTime) {
		year = Integer.parseInt(dateTime.substring(0, 4));
		month = Integer.parseInt(dateTime.substring(4, 6));
		day = Integer.parseInt(dateTime.substring(6, 8));
		hour = Integer.parseInt(dateTime.substring(9, 11));
		minute = Integer.parseInt(dateTime.substring(11, 13));
		second = Integer.parseInt(dateTime.substring(13, 15));
	}

	public DateTime(DateTime cloneDateTime) {
		year = cloneDateTime.getYear();
		month = cloneDateTime.getMonth();
		day = cloneDateTime.getDay();
		hour = cloneDateTime.getHour();
		minute = cloneDateTime.getMinute();
		second = cloneDateTime.getSecond();
	}

	public boolean inDay(Day day) {
		return (this.compareTo(day.getDate()) == 0);
	}

	public boolean inWeek(Week week) {
		return (this.compareTo(week.getDateBegin()) >= 0 && this.compareTo(week.getDateEnd()) <= 0);
	}

	public String toString(boolean withTime) {
		if (withTime) {
			return Integer.toString(day) + "/"
					+ Integer.toString(month) + "/"
					+ Integer.toString(year) + " "
					+ Integer.toString(hour) + ":"
					+ Integer.toString(minute) + ":"
					+ Integer.toString(second);
		} else {
			return Integer.toString(day) + "/"
					+ Integer.toString(month) + "/"
					+ Integer.toString(year);
		}
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toSimpleString(boolean withTime) {
		if (withTime) {
			return Integer.toString(year)
					+ Integer.toString(month)
					+ Integer.toString(day)
					+ Integer.toString(hour)
					+ Integer.toString(minute)
					+ Integer.toString(second);
		} else {
			return Integer.toString(year)
					+ Integer.toString(month)
					+ Integer.toString(day);
		}
	}

	public String toSimpleString() {
		return toSimpleString(false);
	}

	public int compareTo(DateTime otherDateTime, boolean compareTime) {
		if (year > otherDateTime.getYear()) {
			return 1;
		} else if (year < otherDateTime.getYear()) {
			return -1;
		} else {
			if (month > otherDateTime.getMonth()) {
				return 1;
			} else if (month < otherDateTime.getMonth()) {
				return -1;
			} else {
				if (day > otherDateTime.getDay()) {
					return 1;
				} else if (day < otherDateTime.getDay()) {
					return -1;
				} else {
					if (compareTime) {
						if (hour > otherDateTime.getHour()) {
							return 1;
						} else if (hour < otherDateTime.getHour()) {
							return -1;
						} else {
							if (minute > otherDateTime.getMinute()) {
								return 1;
							} else if (minute < otherDateTime.getMinute()) {
								return -1;
							} else {
								if (second > otherDateTime.getSecond()) {
									return 1;
								} else if (second < otherDateTime.getSecond()) {
									return -1;
								} else {
									return 0;
								}
							}
						}
					} else {
						return 0;
					}
				}
			}
		}
	}

	public int compareTo(DateTime otherDateTime) {
		return compareTo(otherDateTime, false);
	}

	public void addDays(int nbDays) {
		day += nbDays;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}
}