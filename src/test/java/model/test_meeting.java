package model;

import conf.database.JUnitDatabaseProps;
import hibernate.SessionFactoryProvider;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

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

		Assertions.assertEquals("JUnit Meet", session.get(meeting.class, meet_id).getMeetTitle());
	}
}