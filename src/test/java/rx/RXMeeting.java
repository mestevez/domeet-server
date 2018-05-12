package rx;

import conf.database.JUnitDatabaseProps;
import hibernate.SessionFactoryProvider;
import model.attend;
import model.auth_user;
import model.meeting;
import model.user;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import util.BuildSamples;

import java.util.ArrayList;
import java.util.List;

public class RXMeeting {
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
	void backgroundExecution() throws InterruptedException {
		List<meeting> knownMeetingsUser1 = new ArrayList<>();
		List<meeting> knownMeetingsUser2 = new ArrayList<>();

		user meetUser1 = session.get(user.class, auth_user.getUser(session, "meetinguser1@test.es").getUserID());
		user meetUser2 = session.get(user.class, auth_user.getUser(session, "meetinguser2@test.es").getUserID());

		new Thread(() -> {
			try {
				for (int i=0; i < 3; i++) {
					Thread.sleep(400);
					new BuildSamples(session).buildMeetingsForUser(meetUser1.getUserAuth().getUserMail(), new String[]{}, 3, null);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				for (int i=0; i < 3; i++) {
					Thread.sleep(400);
					new BuildSamples(session).buildMeetingsForUser(meetUser2.getUserAuth().getUserMail(), new String[]{}, 3, null);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				Thread.sleep(1000);
				meeting meeting = model.meeting.getForthcommingMeetings(session, meetUser1.getUserId()).get(0);

				attend.addAttendant(session, meeting, meetUser2);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				user.getMeetingsObservable().subscribe((u) -> {
					synchronized (knownMeetingsUser1) {
						knownMeetingsUser1.clear();
						knownMeetingsUser1.addAll(meeting.getForthcommingMeetings(session, meetUser1.getUserId()));
					}
				});
				user.getMeetingsObservable().subscribe((u) -> {
					synchronized (knownMeetingsUser2) {
						knownMeetingsUser2.clear();
						knownMeetingsUser2.addAll(meeting.getForthcommingMeetings(session, meetUser2.getUserId()));
					}
				});

				Thread.sleep(1800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		Thread.sleep(2000);

		Assertions.assertEquals(9, knownMeetingsUser1.size());
		Assertions.assertEquals(10, knownMeetingsUser2.size());
	}
}
