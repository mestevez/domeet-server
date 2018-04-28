package model;

import conf.database.JUnitDatabaseProps;
import hibernate.SessionFactoryProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class test_auth_user {

	private static Session session;

	@BeforeAll
	static void beforeAll() {
		session = SessionFactoryProvider.getSessionFactory(JUnitDatabaseProps.getDatabaseProps()).openSession();
	}

	@AfterAll
	static void afterAll() {
		session.close();
	}

	@Test
	void checkTestUser() {
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
		auth_user user = entityManager.find(auth_user.class, 1);

		assertEquals("testuser@domeet.cat", user.getUserMail());

		entityManager.close();
	}

	@Test
	void listUsers() {
		List<auth_user> authUsers = auth_user.getUsersList(session);

		assertTrue(authUsers.size() > 1);
		assertEquals("testuser@domeet.cat", authUsers.get(0).getUserMail());
	}
}