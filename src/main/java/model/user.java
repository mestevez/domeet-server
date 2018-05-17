package model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import gson.GsonUserPhotoAdapter;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import rx.exception.RXNotificationException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Indexed
@Table( schema="app", name="user" )
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class user implements Serializable {

	@Id
	@Expose
	private Integer user_id;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Expose
	private String user_firstname;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Expose
	private String user_lastname;

	@Field(index=Index.YES, analyze=Analyze.YES, store=Store.NO)
	@Expose
	private String user_company;

	@Expose
	private String user_phone;

	@Expose
	@JsonAdapter(GsonUserPhotoAdapter.class)
	private byte[] user_photo;

	@JsonGetter("user_photo")
	public String getUserPhotoAsURL() {
		if (user_photo == null || user_photo.length == 0)
			return null;
		else
			return "/app/resources/userimage/" + user_id;
	}

	@ManyToOne
	@JoinColumn(name="user_schedule")
	@Expose
	private schedule user_schedule;

	@IndexedEmbedded
	@OneToOne
	@PrimaryKeyJoinColumn
	private auth_user auth_user;

	// Java RX
	static Subject<user> meetingsObservable = PublishSubject.create();

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

	public static Subject<user> getMeetingsObservable() {
		return meetingsObservable;
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

	private void _notifyMeetingUpdate(Session session, long init) {
		if (!session.isOpen() || session.getTransaction().getStatus() != TransactionStatus.ACTIVE) {
			meetingsObservable.onNext(this);
		} else {
			new Thread(() -> {
				try {
					if (new Date().getTime() - init > 10000)
						throw new RXNotificationException("Notification meeting timeout");

					Thread.sleep(100);

					_notifyMeetingUpdate(session, init);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (RXNotificationException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	public void notifyLeadingMeeting(Session session) {
		_notifyMeetingUpdate(session, new Date().getTime());
	}

	public void notifyMeetingAttendance(Session session) {
		_notifyMeetingUpdate(session, new Date().getTime());
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
	 * @param user_schedule		New user schedule
	 */
	public void updateUser(
		Session session,
		String 	user_firstname,
		String 	user_lastname,
		String 	user_company,
		String 	user_phone,
		String 	user_schedule
	) {
		updateUser(session, user_firstname, user_lastname, user_company, user_phone, user_photo, user_schedule);
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
