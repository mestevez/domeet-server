package model;

import hibernate.SessionFactoryProvider;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class test_auth_user {

	@Test
	void checkTestUser() {
		EntityManager entityManager = SessionFactoryProvider.getSessionFactory().createEntityManager();
		auth_user user = entityManager.find(auth_user.class, 1);

		assertEquals("testuser@domeet.cat", user.getUser_mail());

		entityManager.close();
	}

	@Test
	void listUsers() {
		EntityManager entityManager = SessionFactoryProvider.getSessionFactory().createEntityManager();

		List<auth_user> authUsers = entityManager.createQuery("SELECT a FROM auth_user a").getResultList();

		assertTrue(authUsers.size() > 1);
		assertEquals("testuser@domeet.cat", authUsers.get(0).getUser_mail());

		entityManager.close();
	}
}