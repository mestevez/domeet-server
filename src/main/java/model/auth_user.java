package model;

import hibernate.SessionFactoryProvider;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table( schema="app", name="auth_user" )
public class auth_user {

	@Id
	private Integer user_id;

	private String user_mail;

	public int getUserID() {
		return user_id;
	}

	public String getUserMail() {
		return user_mail;
	}

	public final static List<auth_user> getUsersList() {
		EntityManager entityManager = SessionFactoryProvider.getSessionFactory().createEntityManager();

		List<auth_user> authUsers = entityManager.createQuery("SELECT a FROM auth_user a").getResultList();

		entityManager.close();

		return authUsers;
	}
}
