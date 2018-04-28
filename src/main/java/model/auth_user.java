package model;

import org.hibernate.Session;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		schema="app",
		name = "auth_user_role",
		joinColumns = { @JoinColumn(name = "user_id") },
		inverseJoinColumns = { @JoinColumn(name = "role_id") }
	)
	Set<auth_role> user_roles = new HashSet<>();

	public void addRole(auth_role role) {
		user_roles.add(role);
	}

	public int getUserID() {
		return user_id;
	}

	public String getUserMail() {
		return user_mail;
	}

	public final static List<auth_user> getUsersList(Session session) {
		List<auth_user> authUsers;

		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
		try {
			authUsers = entityManager.createQuery("SELECT a FROM auth_user a").getResultList();
		} finally {
			entityManager.close();
		}

		return authUsers;
	}

	public final static auth_user getUser(Session session, String user_mail) {
		auth_user user;
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
		try {
			user =  entityManager.createQuery("SELECT a FROM auth_user a WHERE user_mail = :user_mail", auth_user.class)
					.setParameter("user_mail", user_mail)
					.getSingleResult();
		} finally {
			entityManager.close();
		}

		return user;
	}

	public boolean hasRole(String role_code) {
		for (auth_role user_role : user_roles) {
			if (user_role.getRoleCode().equalsIgnoreCase(role_code))
				return true;
		}
		return false;
	}

	public void setUserMail(String user_mail) {
		this.user_mail = user_mail;
	}

	public void setUserPassword(String user_passhash) {
		this.user_passhash = user_passhash;
	}
}
