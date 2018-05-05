package model;

import conf.database.JUnitDatabaseProps;
import hibernate.SessionFactoryProvider;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import util.DateUtils;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

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

		meeting meet1 = model.meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED)
				.setMeetState(session, MeetingState.READY);
		meeting meet2 = meeting.addMeeting(session, meetUser1Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED )
				.startMeeting(session);
		meeting meet3 = meeting.addMeeting(session, meetUser1Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);

		meeting.addMeeting(session, meetUser2Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser2Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser2Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);

		session.beginTransaction();

		meeting_date meetDate1 = meet1.getMeetDates().iterator().next();
		meetDate1.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate1);

		meeting_date meetDate2 = meet2.getMeetDates().iterator().next();
		meetDate1.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate2);

		meeting_date meetDate3 = meet3.getMeetDates().iterator().next();
		meetDate3.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
		session.persist(meetDate3);

		session.getTransaction().commit();

		List<meeting> onEditMeetings = meeting.getForegoingMeetings(session, meetUser1Id);

		Assertions.assertEquals(1, onEditMeetings.size());
	}

	@Test
	void getForthcommingMeetingsList() throws ParseException {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();
		int meetUser2Id = auth_user.getUser(session, "meetinguser2@test.es").getUserID();

		meeting meet1 = model.meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED)
				.setMeetState(session, MeetingState.READY);
		meeting meet2 = model.meeting.addMeeting(session, meetUser1Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED)
				.startMeeting(session);
		meeting meet3 = model.meeting.addMeeting(session, meetUser1Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED)
				.setMeetState(session, MeetingState.READY);

		session.beginTransaction();

		meeting_date meetDate1 = meet1.getMeetDates().iterator().next();
		meetDate1.setMeetDate(DateUtils.parseISOTimestampString2Java("2015-07-04T12:08:56.235Z"));
		session.persist(meetDate1);

		meeting_date meetDate2 = meet2.getMeetDates().iterator().next();
		meetDate2.setMeetDate(DateUtils.parseISOTimestampString2Java("2016-07-04T12:08:56.235Z"));
		session.persist(meetDate2);

		meeting_date meetDate3 = meet3.getMeetDates().iterator().next();
		meetDate3.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
		session.persist(meetDate3);

		session.getTransaction().commit();

		meeting.addMeeting(session, meetUser2Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser2Id, "Meet 2", null, new Short("30"), MeetingType.UNDETERMINED );
		meeting.addMeeting(session, meetUser2Id, "Meet 3", null, new Short("30"), MeetingType.UNDETERMINED )
				.setMeetState(session, MeetingState.READY);

		List<meeting> onEditMeetings = meeting.getForthcommingMeetings(session, meetUser1Id);

		Assertions.assertEquals(2, onEditMeetings.size());
	}

	@Test
	void addSubject()  {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		subject.addSubject(session, meet, 0, "First subject", new Short("10"), SubjectPriority.NORMAL);
		subject.addSubject(session, meet, 10, "Second subject", new Short("10"), SubjectPriority.NORMAL);
		subject.addSubject(session, meet, 5, "Third subject", new Short("10"), SubjectPriority.NORMAL);

		Assertions.assertEquals(3, meet.getMeetSubjects().size());
		Assertions.assertEquals("First subject", meet.getMeetSubjects().get(0).getTitle());
		Assertions.assertEquals("Third subject", meet.getMeetSubjects().get(1).getTitle());
		Assertions.assertEquals("Second subject", meet.getMeetSubjects().get(2).getTitle());
	}

	@Test
	void getMaxSubjectOrder()  {
		int meetUser1Id = auth_user.getUser(session, "meetinguser1@test.es").getUserID();

		meeting meet = meeting.addMeeting(session, meetUser1Id, "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

		subject first_subject = subject.addSubject(session, meet, meet.getMaxOrder() < 0 ? 0 : meet.getMaxOrder() + 5, "First subject", new Short("10"), SubjectPriority.NORMAL);
		subject second_subject = subject.addSubject(session, meet, meet.getMaxOrder() < 0 ? 0 : meet.getMaxOrder() + 5, "Second subject", new Short("10"), SubjectPriority.NORMAL);
		subject third_subject = subject.addSubject(session, meet, meet.getMaxOrder() < 0 ? 0 : meet.getMaxOrder() + 5, "Third subject", new Short("10"), SubjectPriority.NORMAL);

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
}