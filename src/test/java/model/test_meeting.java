package model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.database.JUnitDatabaseProps;
import fop.FOPMeeting;
import freemarker.template.TemplateException;
import hibernate.SessionFactoryProvider;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;
import util.BuildSamples;
import util.DateUtils;
import util.Path;

import javax.mail.MessagingException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("model")
class test_meeting {
	private static Session session;

	@BeforeAll
	static void beforeAll() {
		session = SessionFactoryProvider.getSessionFactory(JUnitDatabaseProps.getDatabaseProps()).openSession();
	}

	@AfterAll
	static void afterAll() {
		session.close();
	}

	@BeforeEach
	void beforeEach() {
		try {
			session.clear();

			// Clean previous data
			Transaction transaction = session.beginTransaction();
			session.createQuery("DELETE FROM meeting WHERE meet_id > 50").executeUpdate();
			session.createQuery("DELETE FROM auth_user WHERE user_id > 50").executeUpdate();
			transaction.commit();

			// Insert test data
			user.addUser(session, "meetinguser1@test.es", "12345", "John", "Smith", null, null, null, "OFFICE");
			user.addUser(session, "meetinguser2@test.es", "12345", "Mary", "Wilson", null, null, null, "OFFICE");
			user.addUser(session, "meetinguser3@test.es", "12345", "Wilson", "Mars", null, null, null, "OFFICE");
			user.addUser(session, "meetinguser4@test.es", "12345", "Jessica", "Donald", null, null, null, "OFFICE");
			user.addUser(session, "meetinguser5@test.es", "12345", "Peter", "Machine", null, null, null, "OFFICE");
			user.addUser(session, "meetinguser6@test.es", "12345", "Olivia", "Maxwell", null, null, null, "OFFICE");

			user.addUser(session, "mestevez85@gmail.com", "12345", "Marc", "Estévez", "UOC", null, null, "OFFICE");
			user.addUser(session, "raquel.cubina@gmail.com", "12345", "Raquel", "Cubiñá", null, null, null, "OFFICE");
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	@Test
	void createMeeting() {
		int meet_id = meeting.addMeeting(
				session,
				auth_user.getUser(session, "meetinguser1@test.es").getUserID(),
				"JUnit Meet",
				"Meeting created from JUnit",
				new Short("30"),
				MeetingType.UNDETERMINED
		).getMeetId();

		meeting meet = session.get(meeting.class, meet_id);
		Assertions.assertEquals("JUnit Meet", meet.getMeetTitle());
		Assertions.assertEquals("meetinguser1@test.es", meet.getMeetLeader().getUserAuth().getUserMail());
	}

	@Test
	void onEditMeetingsList() {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		int meetUser2Id = auth_user.getUser(session, "meetinguser2@test.es").getUserID();

		meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser1Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser1Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);
		meeting.addMeeting(session, meetUser2Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser2Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser2Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);

		List<meeting> onEditMeetings = meeting.getOnEditMeetings(session, meetUser1Id);

		Assertions.assertEquals(2, onEditMeetings.size());
	}

	@Test
	void getForegoingMeetingsList() {
		auth_user meetUser1 = auth_user.getUser(session, "meetinguser1@test.es");
		auth_user meetUser2 = auth_user.getUser(session, "meetinguser2@test.es");

		session.beginTransaction();

		BiConsumer prepareFn = (BiConsumer<meeting, Integer>) (meet, index) -> {
			try {
				switch (index) {
					case 0: {
						meet.setMeetState(session, MeetingState.READY);
						meeting_date meetDate = meet.getMeetDates().iterator().next();
						meetDate.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
						session.persist(meetDate);
						break;
					} case 1: {
						meet.startMeeting(session);
						meeting_date meetDate = meet.getMeetDates().iterator().next();
						meetDate.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
						session.persist(meetDate);
						break;
					} case 2: {
						meet.setMeetState(session, MeetingState.READY);
						meeting_date meetDate = meet.getMeetDates().iterator().next();
						meetDate.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
						session.persist(meetDate);
						break;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		};

		BuildSamples sampler = new BuildSamples(session);

		List<meeting> meetingUser1 = sampler.buildMeetingsForUser(meetUser1.getUserMail(), new String[]{}, 3, prepareFn);

		sampler.buildMeetingsForUser(meetUser2.getUserMail(), new String[]{}, 3, prepareFn);

		session.getTransaction().commit();

		Assertions.assertEquals(1, meeting.getForegoingMeetings(session, meetUser1.getUserID()).size());
		Assertions.assertEquals(1, meeting.getForegoingMeetings(session, meetUser2.getUserID()).size());

		attend.addAttendant(session, meetingUser1.get(0), session.get(user.class, meetUser2.getUserID()));
		Assertions.assertEquals(1, meeting.getForegoingMeetings(session, meetUser1.getUserID()).size());
		Assertions.assertEquals(2, meeting.getForegoingMeetings(session, meetUser2.getUserID()).size());
	}

	@Test
	void getForthcommingMeetingsList() {
		auth_user meetUser1 = auth_user.getUser(session, "meetinguser1@test.es");
		auth_user meetUser2 = auth_user.getUser(session, "meetinguser2@test.es");

		session.beginTransaction();

		BiConsumer prepareFn = (BiConsumer<meeting, Integer>) (meet, index) -> {
			try {
				switch (index) {
					case 0: {
						meet.setMeetState(session, MeetingState.READY);
						meeting_date meetDate = meet.getMeetDates().iterator().next();
						meetDate.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
						session.persist(meetDate);
						break;
					} case 1: {
						meet.startMeeting(session);
						meeting_date meetDate = meet.getMeetDates().iterator().next();
						meetDate.setMeetDate(DateUtils.parseISOTimestampString2Java("2016-07-04T12:08:56.235Z"));
						session.persist(meetDate);
						break;
					} case 2: {
						meet.setMeetState(session, MeetingState.READY);
						meeting_date meetDate = meet.getMeetDates().iterator().next();
						meetDate.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
						session.persist(meetDate);
						break;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		};

		BuildSamples sampler = new BuildSamples(session);

		List<meeting> meetingsUser1 = sampler.buildMeetingsForUser(meetUser1.getUserMail(), new String[]{}, 3, prepareFn);

		sampler.buildMeetingsForUser(meetUser2.getUserMail(), new String[]{}, 3, prepareFn);

		session.getTransaction().commit();

		Assertions.assertEquals(2, meeting.getForthcommingMeetings(session, meetUser1.getUserID()).size());
		Assertions.assertEquals(2, meeting.getForthcommingMeetings(session, meetUser2.getUserID()).size());

		attend.addAttendant(session, meetingsUser1.get(2), session.get(user.class, meetUser2.getUserID()));
		Assertions.assertEquals(2, meeting.getForthcommingMeetings(session, meetUser1.getUserID()).size());
		Assertions.assertEquals(3, meeting.getForthcommingMeetings(session, meetUser2.getUserID()).size());
	}

	@Test
	void addSubject()  {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		subject.addSubject(session, meet, 0, "First subject", new Short("10"), SubjectPriority.NORMAL);
		subject.addSubject(session, meet, 10, "Second subject", new Short("10"), SubjectPriority.NORMAL);
		subject.addSubject(session, meet, 5, "Third subject", new Short("10"), SubjectPriority.NORMAL);

		Iterator<subject> iterator = meet.getMeetSubjects().iterator();
		Assertions.assertTrue(iterator.hasNext());
		Assertions.assertEquals("First subject", iterator.next().getTitle());
		Assertions.assertTrue(iterator.hasNext());
		Assertions.assertEquals("Third subject", iterator.next().getTitle());
		Assertions.assertTrue(iterator.hasNext());
		Assertions.assertEquals("Second subject", iterator.next().getTitle());
		Assertions.assertFalse(iterator.hasNext());
	}

	@Test
	void getMaxSubjectOrder()  {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		subject first_subject = subject.addSubject(session, meet, meet.getSubjectMaxOrder() < 0 ? 0 : meet.getSubjectMaxOrder() + 5, "First subject", new Short("10"), SubjectPriority.NORMAL);
		subject second_subject = subject.addSubject(session, meet, meet.getSubjectMaxOrder() < 0 ? 0 : meet.getSubjectMaxOrder() + 5, "Second subject", new Short("10"), SubjectPriority.NORMAL);
		subject third_subject = subject.addSubject(session, meet, meet.getSubjectMaxOrder() < 0 ? 0 : meet.getSubjectMaxOrder() + 5, "Third subject", new Short("10"), SubjectPriority.NORMAL);

		Assertions.assertEquals(0, first_subject.getSubjectOrder());
		Assertions.assertEquals(5, second_subject.getSubjectOrder());
		Assertions.assertEquals(10, third_subject.getSubjectOrder());
	}

	@Test
	void addAttendant() {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		user user1 = user.getUser(session, auth_user.getUser(session, "meetinguser2@test.es").getUserID());
		user user2 = user.getUser(session, auth_user.getUser(session, "meetinguser3@test.es").getUserID());

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		attend.addAttendant(session, meet, user1);
		attend.addAttendant(session, meet, user2);

		Assertions.assertEquals(2, meet.getMeetAttendants().size());
	}

	@Test
	void removeAttendant() {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		user user1 = user.getUser(session, auth_user.getUser(session, "meetinguser2@test.es").getUserID());
		user user2 = user.getUser(session, auth_user.getUser(session, "meetinguser3@test.es").getUserID());

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		attend attdobj = attend.addAttendant(session, meet, user1);
		attend.addAttendant(session, meet, user2);
		attdobj.deleteAttendant(session);

		Assertions.assertEquals(1, meet.getMeetAttendants().size());
		Iterator<attend> iterator = meet.getMeetAttendants().iterator();
		Assertions.assertEquals("meetinguser3@test.es", iterator.next().getUser().getUserAuth().getUserMail());
	}

	@Test
	void searchAttendants() throws InterruptedException {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		user user1 = user.getUser(session, auth_user.getUser(session, "meetinguser2@test.es").getUserID());

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		attend.addAttendant(session, meet, user1);

		meet.searchPendingAttendants(session, "M");
		meet.searchPendingAttendants(session, "Ma");
		meet.searchPendingAttendants(session, "Mar");
		List<user> listUsers = meet.searchPendingAttendants(session, "mary");

		assertEquals(1, listUsers.size());
		assertEquals("meetinguser3@test.es", listUsers.get(0).getUserAuth().getUserMail());
	}

	@Test
	void meetingMapper() throws IOException {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		user user1 = user.getUser(session, auth_user.getUser(session, "meetinguser2@test.es").getUserID());
		user user2 = user.getUser(session, auth_user.getUser(session, "meetinguser3@test.es").getUserID());

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);
		subject first_subject = subject.addSubject(session, meet, 0, "First subject", new Short("10"), SubjectPriority.NORMAL);
		subject third_subject = subject.addSubject(session, meet, 10, "Third subject", new Short("10"), SubjectPriority.NORMAL);
		subject second_subject = subject.addSubject(session, meet, 5, "Second subject", new Short("10"), SubjectPriority.NORMAL);

		attend.addAttendant(session, meet, user1);
		attend.addAttendant(session, meet, user2);

		Map<String, Object> mapData = new HashMap();
		mapData.put("user", user.getUser(session, meetUser1Id));
		mapData.put("meet", meet);

		ObjectMapper objectMapper = new ObjectMapper();
		String jsonstr = objectMapper.writeValueAsString(mapData);
		System.out.println(jsonstr);
		JsonNode jsonNode = objectMapper.reader().readTree(jsonstr);
		JsonNode meetJSON = jsonNode.get("meet");
		Assertions.assertEquals(meet.getMeetId().intValue(), meetJSON.get("meet_id").asInt());

		Iterator<JsonNode> meet_subjects = meetJSON.get("meet_subjects").elements();
		Assertions.assertTrue(meet_subjects.hasNext());
		Assertions.assertEquals(first_subject.getSubjectOrder(), meet_subjects.next().get("subject_order").asInt());
		Assertions.assertTrue(meet_subjects.hasNext());
		Assertions.assertEquals(second_subject.getSubjectOrder(), meet_subjects.next().get("subject_order").asInt());
		Assertions.assertTrue(meet_subjects.hasNext());
		Assertions.assertEquals(third_subject.getSubjectOrder(), meet_subjects.next().get("subject_order").asInt());
		Assertions.assertFalse(meet_subjects.hasNext());

		Iterator<JsonNode> meet_attendants = meetJSON.get("meet_attendants").elements();
		Assertions.assertTrue(meet_attendants.hasNext());
		meet_attendants.next();
		Assertions.assertTrue(meet_attendants.hasNext());
		meet_attendants.next();
		Assertions.assertFalse(meet_attendants.hasNext());
	}

	@Test
	void addSubjectNote() {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		subject first_subject = subject.addSubject(session, meet, 0, "First subject", new Short("10"), SubjectPriority.NORMAL);
//		subject second_subject = subject.addSubject(session, meet, 10, "Second subject", new Short("10"), SubjectPriority.NORMAL);
//		subject third_subject = subject.addSubject(session, meet, 5, "Third subject", new Short("10"), SubjectPriority.NORMAL);

		int nextOrder = first_subject.getNotesMaxOrder(SubjectNoteType.AGREEMENT) < 0 ? 0 : first_subject.getNotesMaxOrder(SubjectNoteType.AGREEMENT) + 5;
		subject_note.addNote(session, first_subject, nextOrder, "First agreement", SubjectNoteType.AGREEMENT);
		nextOrder = first_subject.getNotesMaxOrder(SubjectNoteType.AGREEMENT) < 0 ? 0 : first_subject.getNotesMaxOrder(SubjectNoteType.AGREEMENT) + 5;
		subject_note.addNote(session, first_subject, nextOrder, "Second agreement", SubjectNoteType.AGREEMENT);
		nextOrder = first_subject.getNotesMaxOrder(SubjectNoteType.AGREEMENT) < 0 ? 0 : first_subject.getNotesMaxOrder(SubjectNoteType.AGREEMENT) + 5;
		subject_note.addNote(session, first_subject, nextOrder, "Third agreement", SubjectNoteType.AGREEMENT);
		nextOrder = first_subject.getNotesMaxOrder(SubjectNoteType.COMMENT) < 0 ? 0 : first_subject.getNotesMaxOrder(SubjectNoteType.COMMENT) + 5;
		subject_note.addNote(session, first_subject, nextOrder, "First comment", SubjectNoteType.COMMENT);
		nextOrder = first_subject.getNotesMaxOrder(SubjectNoteType.COMMENT) < 0 ? 0 : first_subject.getNotesMaxOrder(SubjectNoteType.COMMENT) + 5;
		subject_note.addNote(session, first_subject, nextOrder, "Second comment", SubjectNoteType.COMMENT);
		nextOrder = first_subject.getNotesMaxOrder(SubjectNoteType.COMMENT) < 0 ? 0 : first_subject.getNotesMaxOrder(SubjectNoteType.COMMENT) + 5;
		subject_note.addNote(session, first_subject, nextOrder, "Third comment", SubjectNoteType.COMMENT);

		Assertions.assertEquals(6, first_subject.getSubjectNotes().size());
//		Assertions.assertEquals("First subject", meet.getMeetSubjects().get(0).getTitle());
//		Assertions.assertEquals("Third subject", meet.getMeetSubjects().get(1).getTitle());
//		Assertions.assertEquals("Second subject", meet.getMeetSubjects().get(2).getTitle());
	}

	@Test
	void minutesOfTheMeeting() throws IOException, SAXException, TransformerException, TemplateException, MessagingException {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();

		meeting meet = meeting.addMeeting(
				session,
				meetUser1Id,
				"Axional v.2018.1 project",
				"The objective of the meeting is following up the state of the project.\n" +
						"After evaluating its state, it has to be decided if is necessary to apply any " +
						"measure to redirect the project or if it can be concluded it in time.",
				new Short("60"),
				MeetingType.DECISION_MAKING
		);

		attend.addAttendant(session, meet, session.get(user.class, auth_user.getUser(session, "meetinguser2@test.es").getUserID()));
		attend.addAttendant(session, meet, session.get(user.class, auth_user.getUser(session, "meetinguser3@test.es").getUserID()));
		attend.addAttendant(session, meet, session.get(user.class, auth_user.getUser(session, "meetinguser4@test.es").getUserID()));
		attend.addAttendant(session, meet, session.get(user.class, auth_user.getUser(session, "meetinguser5@test.es").getUserID()));
		attend.addAttendant(session, meet, session.get(user.class, auth_user.getUser(session, "meetinguser6@test.es").getUserID()));

		subject sbjDelivery = model.subject.addSubject(session, meet, 0, "Delivery of new version to EcoCompany", new Short("20"), SubjectPriority.ESSENTIAL);
		subject sbjLowIssues = subject.addSubject(session, meet, 10, "Review and prioritize open issues", new Short("20"), SubjectPriority.ESSENTIAL);
		subject sbjHighIssues = model.subject.addSubject(session, meet, 20, "Estimate time to solve high priority issues", new Short("10"), SubjectPriority.NORMAL);
		subject sbjSpeedIssues = model.subject.addSubject(session, meet, 30, "Causes of decreasing efficiency of issues resolution", new Short("10"), SubjectPriority.NORMAL);
		subject sbjSystemSpeed = subject.addSubject(session, meet, 40, "System speed", new Short("10"), SubjectPriority.NORMAL);
		subject sbjAnalize = model.subject.addSubject(session, meet, 50, "Analyze the state o the project", new Short("20"), SubjectPriority.NORMAL);
		subject sbjVersConclude = subject.addSubject(session, meet, 60, "Estimate a concluding date", new Short("10"), SubjectPriority.NORMAL);

		meet.createMeeting(session);
		meet.startMeeting(session);

		subject_note.addNote(session, sbjDelivery, 0,
				"Today, the 18th of may of 2018 the version 2018.1 is concluded and be delivered to EcoCompany for the initial phases of the project"
				, SubjectNoteType.AGREEMENT);
		subject_note.addNote(session, sbjDelivery, 10,
				"By the 25 of may of 2018 the project must be ready for the EcoCompany testing"
				, SubjectNoteType.AGREEMENT);
		subject_note.addNote(session, sbjDelivery, 0,
				"The 4 pending critical issues has been resolved, and are pending to verify by internal testing team."
				, SubjectNoteType.COMMENT);

		subject_note.addNote(session, sbjLowIssues, 0,
				"It still exists open low priority issues, which will be resolved in further phases."
				, SubjectNoteType.UNSETTLED);

		subject_note.addNote(session, sbjHighIssues, 0,
				"It exists 20 solved bugs pending to test by the internal testing team."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjHighIssues, 10,
				"It exists 8 bugs currently being solved."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjHighIssues, 20,
				"It exists 29 pending to resolve with HIGH priority."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjHighIssues, 30,
				"It exists 66 pending to resolve with LOW priority."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjHighIssues, 40,
				"It exists 48 enhancements. Some of them requires an special attention."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjHighIssues, 50,
				"It exists 3 bugs which cannot be reproduced by the development team."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjHighIssues, 0,
				"Do not use the images added to Trello cards as card cover, in order to minimize each issue height."
				, SubjectNoteType.DECISION);

		subject_note.addNote(session, sbjSpeedIssues, 0,
				"In front of the feeling that issue resolving speed has considerably decreased during the last week," +
						"the development team argued that has been working on 2 essential new features for the EcoCompany version." +
						"Those features has been finished, so issue resolving time has to increase again."
				, SubjectNoteType.COMMENT);

		subject_note.addNote(session, sbjSystemSpeed, 0,
				"It has been exposed the perception of a decrease of system speed while using the application.\n" +
						"As an example, when accessing a page for the fist time it last about 5 seconds, when in previous " +
						"versions this action was executed immediately."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjSystemSpeed, 0,
				"The development team will investigate those issues and expose the possible solutions and consequences in the next meeting."
				, SubjectNoteType.DECISION);
		subject_note.addNote(session, sbjAnalize, 0,
				"Despite of the mentioned issues, the application is ready to be delivered for being used in the first phases of a project."
				, SubjectNoteType.COMMENT);
		subject_note.addNote(session, sbjVersConclude, 0,
				"The development team has agreed to deliver a version TODAY"
				, SubjectNoteType.AGREEMENT);
		subject_note.addNote(session, sbjVersConclude, 0,
				"The pending issues will be fixed on the version branch, so further new implementation will not affect the version"
				, SubjectNoteType.DECISION);

		meet.endMeeting(session);
		meet.concludeMeeting(session, false);

		Locale locale = Locale.getDefault();

		File file = new File(Path.getTempPathFile("fop", "minute", "pdf"));
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		new FOPMeeting(meet, locale).getMinutesOfTheMeeting().write(fileOutputStream);
		fileOutputStream.close();
	}

	@Test
	void sendInviteNotificationEmail() throws IOException, TemplateException, MessagingException {
		int meetUser1Id = auth_user.getUser(session, "mestevez85@gmail.com").getUserID();
		user user1 = user.getUser(session, auth_user.getUser(session, "meetinguser2@test.es").getUserID());
		user user2 = user.getUser(session, auth_user.getUser(session, "meetinguser3@test.es").getUserID());
		user user3 = user.getUser(session, auth_user.getUser(session, "raquel.cubina@gmail.com").getUserID());
		meeting meet = meeting.addMeeting(session, meetUser1Id, "Progression control and exercices unification", null, new Short("30"), MeetingType.UNDETERMINED);

		attend.addAttendant(session, meet, user1);
		attend.addAttendant(session, meet, user2);
		attend.addAttendant(session, meet, user3);

		meet.createMeeting(session);
	}

	@Test
	void sendMinutesOfTheMeetingEmail() throws IOException, TemplateException, MessagingException, TransformerException, SAXException {
		int meetUser1Id = auth_user.getUser(session, "mestevez85@gmail.com").getUserID();
		user user1 = user.getUser(session, auth_user.getUser(session, "meetinguser2@test.es").getUserID());
		user user2 = user.getUser(session, auth_user.getUser(session, "meetinguser3@test.es").getUserID());
		user user3 = user.getUser(session, auth_user.getUser(session, "raquel.cubina@gmail.com").getUserID());
		meeting meet = meeting.addMeeting(session, meetUser1Id, "Progression control and exercices unification", null, new Short("30"), MeetingType.UNDETERMINED);

		attend.addAttendant(session, meet, user1);
		attend.addAttendant(session, meet, user2);
		attend.addAttendant(session, meet, user3);

		meet.createMeeting(session);
		meet.startMeeting(session);
		meet.endMeeting(session);
		meet.concludeMeeting(session, true);
	}
}