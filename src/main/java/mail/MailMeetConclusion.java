package mail;

import fop.FOPMeeting;
import fop.FOPProducer;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import model.attend;
import model.meeting;
import model.user;
import org.xml.sax.SAXException;
import util.Path;

import javax.activation.FileDataSource;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.*;

public class MailMeetConclusion {
	meeting meet;
	Locale locale;
	private DateFormat dateFormat;
	private DateFormat timeFormat;

	public MailMeetConclusion(meeting meet, Locale locale) {
		this.meet = meet;
		this.locale = locale;
		this.dateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
		this.timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
	}

	public MailMeetConclusion(meeting meet) {
		this(meet, Locale.getDefault());
	}

	public String getMeetTitle() {
		return this.meet.getMeetTitle();
	}

	public String getMeetURL() throws UnknownHostException {
		return "http://" + InetAddress.getLocalHost().getHostName() + ":8081/app/meet/" + meet.getMeetId();
	}

	public String getMeetDate() {
		return dateFormat.format(meet.getMeetDates().iterator().next().getMeetDate());
	}

	public String getMeetOrganizer() {
		user meetLeader = meet.getMeetLeader();
		return meetLeader.getUserFirstname() + " " + meetLeader.getUserLastname();
	}

	public List<String> getMeetAttendants() {
		List<String> attendants = new ArrayList<>();

		for (attend attend : meet.getMeetAttendants()) {
			user meetLeader = attend.getUser();
			attendants.add(meetLeader.getUserFirstname() + " " + meetLeader.getUserLastname());
		}

		return attendants;
	}

	public String getMeetTimeStart() {
		return timeFormat.format(meet.getMeetDates().iterator().next().getMeetDate());
	}

	public String getMeetTimeEnd() {
		Timestamp meetDate = meet.getMeetDates().iterator().next().getMeetDate();
		return timeFormat.format(new Timestamp(meetDate.getTime() + meet.getMeetDuration()*60*1000));
	}

	public Mail getMail() throws IOException, TemplateException, TransformerException, SAXException {
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/mail_conclusion", locale);

		Map<String, Object> data =  new HashMap<>();
		data.put("meet", this);
		data.put("i18n", bundle);

		List<String> mailTo = new ArrayList<>();
		mailTo.add(meet.getMeetLeader().getUserAuth().getUserMail());
		for (attend attend : meet.getMeetAttendants()) {
			mailTo.add(attend.getUser().getUserAuth().getUserMail());
		}

		FOPProducer fopProducer = new FOPMeeting(meet, locale).getMinutesOfTheMeetingAsFile();

		return new Mail(
				mailTo,
				null,
				"DoMeet - " + meet.getMeetTitle(),
				FTLParser.getParsedStringFromFile(FTLConfiguration.getInstance(), data, "mail_conclusion.ftl")
		).setHTML().addFile(new FileDataSource(fopProducer.getPDF()));
	}
}
