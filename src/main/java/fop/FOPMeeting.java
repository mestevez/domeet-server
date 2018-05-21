package fop;

import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import model.*;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class FOPMeeting {

	private meeting meet;
	private Locale locale;
	private DateFormat dateFormat;
	private DateFormat timeFormat;

	public FOPMeeting(meeting meet, Locale locale) {
		this.meet = meet;
		this.locale = locale;
		this.dateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale);
		this.timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
	}

	public String getMeetTitle() {
		return this.meet.getMeetTitle();
	}

	public String getMeetDescription() {
		return this.meet.getMeetDescription();
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

	public String getMeetLeader() {
		user meetLeader = meet.getMeetLeader();
		return meetLeader.getUserFirstname() + " " + meetLeader.getUserLastname();
	}

	public List<String> getMeetAttendants() {
		List<String> attendants = new ArrayList<>();

		Iterator<attend> iterator = meet.getMeetAttendants().iterator();
		while (iterator.hasNext()) {
			user meetAttend = iterator.next().getUser();
			attendants.add(meetAttend.getUserFirstname() + " " + meetAttend.getUserLastname());
		}

		return attendants;
	}

	public List<String> getMeetSubjectsName() {
		List<String> subjectNames = new ArrayList<>();

		Iterator<subject> iterator = meet.getMeetSubjects().iterator();
		while (iterator.hasNext()) {
			subjectNames.add(iterator.next().getTitle());
		}

		return subjectNames;
	}

	public List<Map<String, Object>> getMeetSubjectsWithNotes() {
		List<Map<String, Object>> subjects = new ArrayList<>();

		Iterator<subject> itSubjects = meet.getMeetSubjects().iterator();
		while (itSubjects.hasNext()) {
			subject subject = itSubjects.next();
			Map<String, Object> subjectData =  new HashMap<>();

			List<Map<String, Object>> note_types = new ArrayList<>();
			List<String> notes = new ArrayList<>();

			Iterator<subject_note> itNotes = subject.getSubjectNotes().iterator();
			String prvNoteType = null;
			while (itNotes.hasNext()) {
				subject_note note = itNotes.next();

				if (prvNoteType != null && !prvNoteType.equalsIgnoreCase(note.getNoteType().toString())) {
					Map<String, Object> noteTypeData =  new HashMap<>();
					noteTypeData.put("note_type", prvNoteType);
					noteTypeData.put("notes", notes.toArray(new String[notes.size()]));
					note_types.add(noteTypeData);

					notes.clear();
				}
				notes.add(note.getNoteDescription());
				prvNoteType = note.getNoteType().toString();
			}

			if (notes.size() > 0) {
				Map<String, Object> noteTypeData =  new HashMap<>();
				noteTypeData.put("note_type", prvNoteType);
				noteTypeData.put("notes", notes.toArray(new String[notes.size()]));
				note_types.add(noteTypeData);
			}

			if (note_types.size() > 0) {
				subjectData.put("subject_title", subject.getTitle());
				subjectData.put("subject_note_type", note_types);
				subjects.add(subjectData);
			}
		}

		return subjects;
	}

	public StreamingOutput getMinutesOfTheMeeting() throws IOException, TemplateException, SAXException, TransformerException {
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/minutes", locale);
		Map<String, Object> data =  new HashMap<>();
		data.put("meet", this);
		data.put("i18n", bundle);

		String templateOutput = FTLParser.getParsedStringFromFile(FTLConfiguration.getInstance(), data, "minute.ftl");

		FOPProducer fopProducer = new FOPProducer(
			FOPConfiguration.getInstance(),
			templateOutput
		);

		fopProducer.transform();

		byte[] filedata = FileUtils.readFileToByteArray(fopProducer.getPDF());

		fopProducer.clean();

		return output -> {
			output.write(filedata);
			output.flush();
		};
	}

	public FOPProducer getMinutesOfTheMeetingAsFile() throws IOException, TemplateException, SAXException, TransformerException {
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/minutes", locale);

		Map<String, Object> data =  new HashMap<>();
		data.put("meet", this);
		data.put("i18n", bundle);

		String templateOutput = FTLParser.getParsedStringFromFile(FTLConfiguration.getInstance(), data, "minute.ftl");

		FOPProducer fopProducer = new FOPProducer(
			FOPConfiguration.getInstance(),
			templateOutput
		);

		fopProducer.transform();

		return fopProducer;
	}
}
