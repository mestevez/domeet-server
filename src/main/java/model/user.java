package model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
	private Integer user_id;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	private String user_firstname;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	private String user_lastname;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	private String user_company;

	private String user_phone;
	private byte[] user_photo;

	@IndexedEmbedded
	@OneToOne(cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	private auth_user auth_user;

	public static user addUser(
			SessionFactory hibernateSessionFactory,
			String auth_mail,
			String auth_password,
			String user_firstname,
			String user_lastname,
			String user_company,
			String user_phone,
			byte[] user_photo
	) {
		Session session = hibernateSessionFactory.openSession();

		auth_user newAuthUser = new auth_user();
		newAuthUser.setUserMail(auth_mail);
		newAuthUser.setUserPassword(auth_password);
		newAuthUser.addRole(session.get(auth_role.class, auth_role.getRole(hibernateSessionFactory, "regularuser").getRoleID()));

		user newUser = new user();
		newUser.user_firstname = user_firstname;
		newUser.user_lastname = user_lastname;
		newUser.user_company = user_company;
		newUser.user_phone = user_phone;
		newUser.user_photo = user_photo;
		newUser.auth_user = newAuthUser;

		try {
			session.beginTransaction();

			session.persist(newAuthUser);

			newUser.user_id = newAuthUser.getUserID();

			session.save(newUser);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		} finally {
			session.close();
		}

		return newUser;
	}

	public static boolean deleteUser(SessionFactory hibernateSessionFactory, Integer user_id) {
		user user = getUser(hibernateSessionFactory, user_id);

		if (user == null) {
			return false;
		} else {
			Session session = hibernateSessionFactory.openSession();
			try {
				session.beginTransaction();

				session.delete(user);

				session.getTransaction().commit();

				return true;
			} catch (HibernateException ex){
				session.getTransaction().rollback();
				throw ex;
			} finally {
				session.close();
			}
		}
	}

	public static user getUser(SessionFactory hibernateSessionFactory, Integer user_id) {
		EntityManager entityManager = hibernateSessionFactory.createEntityManager();

		try {
			return entityManager.find(user.class, user_id);
		} finally {
			entityManager.close();
		}
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

	public static List<user> getUsersList(SessionFactory hibernateSessionFactory) {
		EntityManager entityManager = hibernateSessionFactory.createEntityManager();

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

	public static List<user> searchUsers(SessionFactory hibernateSessionFactory, String query) {
		EntityManager em = hibernateSessionFactory.createEntityManager();

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);

//		fullTextEntityManager
//				.createIndexer(user.class)
//				.startAndWait();

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

//		org.apache.lucene.search.Sort sort = new Sort(SortField.FIELD_SCORE);
//		jpaQuery.setSort(sort);

		// execute search
		List result = jpaQuery.getResultList();

		em.getTransaction().commit();
		em.close();

		return result;
	}

	/**
	 * Modify user personal data
	 *
	 * @param hibernateSessionFactory	Hibernate database configuration
	 * @param user_id					Id of the user to be updated
	 * @param user_firstname			New user first name
	 * @param user_lastname				New user last name
	 * @param user_company				New user company
	 * @param user_phone				New user phone
	 * @param user_photo				New user photo
	 */
	public static void updateUser(
			SessionFactory hibernateSessionFactory,
			Integer user_id,
			String 	user_firstname,
			String 	user_lastname,
			String 	user_company,
			String 	user_phone,
			byte[] 	user_photo
	) {
		user updUser = getUser(hibernateSessionFactory, user_id);
		updUser.user_firstname = user_firstname;
		updUser.user_lastname = user_lastname;
		updUser.user_company = user_company;
		updUser.user_phone = user_phone;
		updUser.user_photo = user_photo;

		Session session = hibernateSessionFactory.openSession();
		try {
			session.beginTransaction();

			session.merge(updUser);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		} finally {
			session.close();
		}
	}

	public String toString() {
		return String.format("[%d] %s, Name: %s %s, Company: %s", auth_user.getUserID(), auth_user.getUserMail(), user_firstname, user_lastname, user_company);
	}
}
