package model;

import conf.database.JUnitDatabaseProps;
import hibernate.SessionFactoryProvider;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.xml.bind.DatatypeConverter;
import java.util.List;

class test_schedule {

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
			session.createQuery("DELETE FROM schedule WHERE sch_code NOT IN ('OFFICE', 'FULL', 'MORNING', 'EVE')").executeUpdate();
			session.createQuery("DELETE FROM auth_user WHERE user_id > 50").executeUpdate();
			transaction.commit();

			// Insert test data
			user.addUser(session, "scheduleuser1@test.es", "12345", "John", "Smith", null, null, null, "OFFICE");
			user.addUser(session, "scheduleuser2@test.es", "12345", "Mary", "Hayle", null, null, null, "FULL");
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	@Test
	void listSchedules() {
		List<schedule> schedules = schedule.getSchedulesList(session);

		Assertions.assertNotEquals(0, schedules.size());
		Assertions.assertEquals("OFFICE", schedules.get(0).getScheduleCode());
		Assertions.assertEquals("Office hours", schedules.get(0).getScheduleDescription());
	}

	@ParameterizedTest
	@CsvSource({
		"scheduleuser1@test.es, 2018-04-28T00:00:00.0Z, false",
		"scheduleuser1@test.es, 2018-04-28T10:00:00.0Z, false",
		"scheduleuser1@test.es, 2018-04-27T10:00:00.0Z, true",
		"scheduleuser1@test.es, 2018-04-27T15:00:00.0Z, false",
		"scheduleuser1@test.es, 2018-04-27T17:00:00.0Z, true",
		"scheduleuser1@test.es, 2018-04-27T16:35:00.0Z, true",

		"scheduleuser2@test.es, 2018-04-28T00:00:00.0Z, true",
		"scheduleuser2@test.es, 2018-04-28T10:00:00.0Z, true",
		"scheduleuser2@test.es, 2018-04-27T10:00:00.0Z, true",
		"scheduleuser2@test.es, 2018-04-27T15:00:00.0Z, true",
		"scheduleuser2@test.es, 2018-04-27T17:00:00.0Z, true",
		"scheduleuser2@test.es, 2018-04-27T16:35:00.0Z, true"
	})
	void isUserAvailable(String userMail, String testdate, boolean result) {
		user 		appuser 		= user.getUser(session, auth_user.getUser(session, userMail).getUserID());
		schedule	userSchedule	= appuser.getUserSchedule();

		Assertions.assertNotNull(userSchedule);
		Assertions.assertEquals(result, schedule_time.isOnWorkTime(session, userSchedule.getScheduleCode(), DatatypeConverter.parseDateTime(testdate).getTime()));
	}
}