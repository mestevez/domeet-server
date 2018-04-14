package model;

import org.hibernate.SessionFactory;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table( schema="app", name="auth_user" )
public class auth_user implements Serializable {

	@Id
	@SequenceGenerator(name="auth_generator", sequenceName="auth_user_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="auth_generator")
	private Integer user_id;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	private String user_mail;

	private String user_passhash;

	public int getUserID() {
		return user_id;
	}

	public String getUserMail() {
		return user_mail;
	}

	public final static List<auth_user> getUsersList(SessionFactory hibernateSessionFactory) {
		EntityManager entityManager = hibernateSessionFactory.createEntityManager();

		List<auth_user> authUsers = entityManager.createQuery("SELECT a FROM auth_user a").getResultList();

		entityManager.close();

		return authUsers;
	}

	public void setUserMail(String user_mail) {
		this.user_mail = user_mail;
	}

	public void setUserPassword(String user_passhash) {
		this.user_passhash = user_passhash;
	}
}
