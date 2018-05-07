package fop;

import model.meeting;

import java.text.DateFormat;
import java.util.Locale;

public class FOPMeeting {

	private meeting meet;
	private Locale locale;
	private DateFormat dateFormat;
	private DateFormat timeFormat;

	public FOPMeeting(meeting meet, Locale locale) {
		this.meet = meet;
		this.locale = locale;
		this.dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		this.timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
	}

	public String getMeetTitle() {
		return this.meet.getMeetTitle();
	}

	public String getMeetDate() {
		return dateFormat.format(meet.getMeetDates().iterator().next().getMeetDate());
	}

	public String getMeetTimeStart() {
		return timeFormat.format(meet.getMeetTimeStart());
	}

	public String getMeetTimeEnd() {
		return timeFormat.format(meet.getMeetTimeEnd());
	}
}
