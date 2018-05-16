package model;

import conf.database.JUnitDatabaseProps;
import hibernate.SessionFactoryProvider;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

class MeetingIssueTest {
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
	void validate() {
		meeting meet = model.meeting.addMeeting(
				session,
				auth_user.getUser(session, "meetinguser1@test.es").getUserID(),
				"JUnit Meet",
				"Meeting created from JUnit",
				new Short("30"),
				MeetingType.UNDETERMINED
		);

		List<MeetingIssue> issues = meet.validate(session, Locale.getDefault());

		Iterator<MeetingIssue> iterator = issues.iterator();
		Assertions.assertTrue(iterator.hasNext());
		Assertions.assertEquals(MeetingIssueCode.EDIT_DATEOVERDUE, iterator.next().getIssueCode());
		Assertions.assertTrue(iterator.hasNext());
		Assertions.assertEquals(MeetingIssueCode.EDIT_SUBJECTS_ZERO, iterator.next().getIssueCode());
		Assertions.assertTrue(iterator.hasNext());
		Assertions.assertEquals(MeetingIssueCode.EDIT_ATTENDANTS_ZERO, iterator.next().getIssueCode());
	}
}