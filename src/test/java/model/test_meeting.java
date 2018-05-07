package model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import conf.database.JUnitDatabaseProps;
import fop.FOPConfiguration;
import fop.FOPMeeting;
import fop.FOPProducer;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import org.apache.commons.io.FileUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.xml.sax.SAXException;
import util.DateUtils;
import util.Path;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

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
	void getForegoingMeetingsList() throws ParseException {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		int meetUser2Id = auth_user.getUser(session, "meetinguser2@test.es").getUserID();

		meeting meet1_usr1 = model.meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED)
				.setMeetState(session, MeetingState.READY);
		meeting meet2_usr1 = meeting.addMeeting(session, meetUser1Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED )
				.startMeeting(session);
		meeting meet3_usr1 = meeting.addMeeting(session, meetUser1Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);

		meeting meet1_usr2 = meeting.addMeeting(session, meetUser2Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);;
		meeting meet2_usr2 = meeting.addMeeting(session, meetUser2Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED )
				.startMeeting(session);
		meeting meet3_usr2 = meeting.addMeeting(session, meetUser2Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);

		session.beginTransaction();

		meeting_date meetDate1_usr1 = meet1_usr1.getMeetDates().iterator().next();
		meetDate1_usr1.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate1_usr1);

		meeting_date meetDate2_usr1 = meet2_usr1.getMeetDates().iterator().next();
		meetDate1_usr1.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate2_usr1);

		meeting_date meetDate3_usr1 = meet3_usr1.getMeetDates().iterator().next();
		meetDate3_usr1.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
		session.persist(meetDate3_usr1);

		meeting_date meetDate1_usr2 = meet1_usr2.getMeetDates().iterator().next();
		meetDate1_usr2.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate1_usr2);

		meeting_date meetDate2_usr2 = meet2_usr2.getMeetDates().iterator().next();
		meetDate1_usr2.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate2_usr2);

		meeting_date meetDate3_usr2 = meet3_usr2.getMeetDates().iterator().next();
		meetDate3_usr2.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
		session.persist(meetDate3_usr2);

		session.getTransaction().commit();

		Assertions.assertEquals(1, meeting.getForegoingMeetings(session, meetUser1Id).size());
		Assertions.assertEquals(1, meeting.getForegoingMeetings(session, meetUser2Id).size());

		attend.addAttendant(session, meet1_usr1, session.get(user.class, meetUser2Id));
		Assertions.assertEquals(1, meeting.getForegoingMeetings(session, meetUser1Id).size());
		Assertions.assertEquals(2, meeting.getForegoingMeetings(session, meetUser2Id).size());
	}

	@Test
	void getForthcommingMeetingsList() throws ParseException {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		int meetUser2Id = auth_user.getUser(session, "meetinguser2@test.es").getUserID();

		meeting meet1_usr1 = model.meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED)
				.setMeetState(session, MeetingState.READY);
		meeting meet2_usr1 = model.meeting.addMeeting(session, meetUser1Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED)
				.startMeeting(session);
		meeting meet3_usr1 = model.meeting.addMeeting(session, meetUser1Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED)
				.setMeetState(session, MeetingState.READY);

		meeting meet1_usr2 = meeting.addMeeting(session, meetUser2Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED)
				.setMeetState(session, MeetingState.READY);
		meeting meet2_usr2 = meeting.addMeeting(session, meetUser2Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED)
				.startMeeting(session);
		meeting meet3_usr2 = meeting.addMeeting(session, meetUser2Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);

		session.beginTransaction();

		meeting_date meetDate1_usr1 = meet1_usr1.getMeetDates().iterator().next();
		meetDate1_usr1.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate1_usr1);

		meeting_date meetDate2_usr1 = meet2_usr1.getMeetDates().iterator().next();
		meetDate2_usr1.setMeetDate(DateUtils.parseISOTimestampString2Java("2016-07-04T12:08:56.235Z"));
		session.persist(meetDate2_usr1);

		meeting_date meetDate3_usr1 = meet3_usr1.getMeetDates().iterator().next();
		meetDate3_usr1.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
		session.persist(meetDate3_usr1);

		meeting_date meetDate1_usr2 = meet1_usr2.getMeetDates().iterator().next();
		meetDate1_usr2.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate1_usr2);

		meeting_date meetDate2_usr2 = meet2_usr2.getMeetDates().iterator().next();
		meetDate2_usr2.setMeetDate(DateUtils.parseISOTimestampString2Java("2016-07-04T12:08:56.235Z"));
		session.persist(meetDate2_usr2);

		meeting_date meetDate3_usr2 = meet3_usr2.getMeetDates().iterator().next();
		meetDate3_usr2.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
		session.persist(meetDate3_usr2);

		session.getTransaction().commit();

		Assertions.assertEquals(2, meeting.getForthcommingMeetings(session, meetUser1Id).size());
		Assertions.assertEquals(2, meeting.getForthcommingMeetings(session, meetUser2Id).size());

		attend.addAttendant(session, meet3_usr1, session.get(user.class, meetUser2Id));
		Assertions.assertEquals(2, meeting.getForthcommingMeetings(session, meetUser1Id).size());
		Assertions.assertEquals(3, meeting.getForthcommingMeetings(session, meetUser2Id).size());
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
	void minutesOfTheMeeting() throws IOException, SAXException, TransformerException, TemplateException {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Progression control and exercices unification", null, new Short("30"), MeetingType.UNDETERMINED);
		meet.startMeeting(session);
		meet.endMeeting(session);
		meet.concludeMeeting(session);

		Locale locale = Locale.getDefault();
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/minutes", locale);

		Map<String, Object> data =  new HashMap<>();
		data.put("meet", new FOPMeeting(meet, locale));
		data.put("i18n", bundle);

		String templateOutput = FTLParser.getParsedStringFromFile(FTLConfiguration.getTestInstance(), data, "minute.ftl");

		FOPProducer fopProducer = new FOPProducer(
			FOPConfiguration.getInstance(),
			templateOutput
		);

		fopProducer.transform();

		FileUtils.copyFile(fopProducer.getPDF(), new File(Path.getTempPathFile("fop", "minute", "pdf")));

		fopProducer.clean();
	}
}