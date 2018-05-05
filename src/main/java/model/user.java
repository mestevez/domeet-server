package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.gson.annotations.Expose;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Indexed
@Table( schema="app", name="user" )
public class user implements Serializable {

	@Id
	@Expose
	@JsonProperty("user_id")	// Necessary because jackson converts attribute name to CamelCase style
	private Integer user_id;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Expose
	@JsonProperty("user_firstname")	// Necessary because jackson converts attribute name to CamelCase style
	private String user_firstname;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Expose
	@JsonProperty("user_lastname")	// Necessary because jackson converts attribute name to CamelCase style
	private String user_lastname;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Expose
	@JsonProperty("user_company")	// Necessary because jackson converts attribute name to CamelCase style
	private String user_company;

	@Expose
	private String user_phone;

	@Expose
	@JsonProperty("user_photo")	// Necessary because jackson converts attribute name to CamelCase style
	private byte[] user_photo;

	@ManyToOne
	@JoinColumn(name="user_schedule")
	@Expose
	private schedule user_schedule;

	@IndexedEmbedded
	@OneToOne
	@PrimaryKeyJoinColumn
	private auth_user auth_user;

	public static user addUser(
			Session session,
			String auth_mail,
			String auth_password,
			String user_firstname,
			String user_lastname,
			String user_company,
			String user_phone,
			byte[] user_photo,
			String user_schedule
	) {
		user newUser;

		try {
			auth_user newAuthUser = new auth_user();
			newAuthUser.setUserMail(auth_mail);
			newAuthUser.setUserPassword(auth_password);
			newAuthUser.addRole(session.get(auth_role.class, auth_role.getRole(session, "regularuser").getRoleID()));

			newUser = new user();
			newUser.user_firstname = user_firstname;
			newUser.user_lastname = user_lastname;
			newUser.user_company = user_company;
			newUser.user_phone = user_phone;
			newUser.user_photo = user_photo;
			newUser.user_schedule = user_schedule == null ? null : schedule.getSchedule(session, user_schedule);
			newUser.auth_user = newAuthUser;

			session.beginTransaction();

			session.persist(newAuthUser);

			newUser.user_id = newAuthUser.getUserID();

			session.save(newUser);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		return newUser;
	}

	public static boolean deleteUser(Session session, Integer user_id) {
		user user = getUser(session, user_id);

		if (user == null) {
			return false;
		} else {
			try {
				session.beginTransaction();

				session.delete(user);

				session.getTransaction().commit();

				return true;
			} catch (HibernateException ex){
				session.getTransaction().rollback();
				throw ex;
			}
		}
	}

	public static user getUser(Session session, Integer user_id) {
		return session.get(user.class, user_id);
	}

	public auth_user getUserAuth() {
		return auth_user;
	}

	public String getUserFirstname() {
		return user_firstname;
	}

	public Integer getUserId() {
		return user_id;
	}

	public String getUserLastname() {
		return user_lastname;
	}

	public static List<user> getUsersList(Session session) {
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

		try {
			return entityManager.createQuery("SELECT a FROM user a").getResultList();
		} finally {
			entityManager.close();
		}
	}

	public String getUserCompany() {
		return user_company;
	}

	public String getUserPhone() {
		return user_phone;
	}

	public byte[] getUserPhoto() {
		return user_photo;
	}

	public schedule getUserSchedule() {
		return user_schedule;
	}

	public static List<user> searchUsers(Session session, String query) throws InterruptedException {
		EntityManager em = session.getEntityManagerFactory().createEntityManager();

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
		fullTextEntityManager.createIndexer().startAndWait();

		em.getTransaction().begin();

		// create native Lucene query unsing the query DSL
		// alternatively you can write the Lucene query using the Lucene query parser
		// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
		QueryBuilder qb = fullTextEntityManager.getSearchFactory()
							.buildQueryBuilder().forEntity(user.class).get();

		org.apache.lucene.search.Query luceneQuery = qb
				.keyword()
				.fuzzy()
				.onFields("user_firstname", "user_lastname", "user_company", "auth_user.user_mail")
				.matching(query)
				.createQuery();

		// wrap Lucene query in a javax.persistence.Query
		FullTextQuery jpaQuery =
				fullTextEntityManager.createFullTextQuery(luceneQuery, user.class);

		org.apache.lucene.search.Sort sort = new Sort(SortField.FIELD_SCORE);
		jpaQuery.setSort(sort);

		// execute search
		List result = jpaQuery.getResultList();

		em.getTransaction().commit();
		em.close();

		return result;
	}

	/**
	 * Modify user personal data
	 *
	 * @param session			Hibernate database configuration
	 * @param user_firstname	New user first name
	 * @param user_lastname		New user last name
	 * @param user_company		New user company
	 * @param user_phone		New user phone
	 * @param user_photo		New user photo
	 * @param user_schedule		New user schedule
	 */
	public void updateUser(
		Session session,
		String 	user_firstname,
		String 	user_lastname,
		String 	user_company,
		String 	user_phone,
		byte[] 	user_photo,
		String 	user_schedule
	) {
		this.user_firstname = user_firstname;
		this.user_lastname = user_lastname;
		this.user_company = user_company;
		this.user_phone = user_phone;
		this.user_photo = user_photo;
		this.user_schedule = user_schedule == null ? null : schedule.getSchedule(session, user_schedule);

		try {
			session.beginTransaction();

			session.merge(this);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public String toString() {
		return String.format("[%d] %s, Name: %s %s, Company: %s", auth_user.getUserID(), auth_user.getUserMail(), user_firstname, user_lastname, user_company);
	}
}
