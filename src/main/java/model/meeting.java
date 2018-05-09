package model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.Expose;
import freemarker.template.TemplateException;
import mail.MailConfiguration;
import mail.MailMeetInvitation;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import util.DateUtils;

import javax.mail.MessagingException;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Entity
@Table( schema="app", name="meeting" )
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class meeting implements Serializable {

	@Id
	@SequenceGenerator(name="meeting_generator", sequenceName="meeting_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="meeting_generator")
	@Expose
	private Integer meet_id;

	@Expose
	private String meet_title;

	@Expose
	private String meet_description;

	@ManyToOne
	@JoinColumn(name="meet_leader")
	@Expose
	private user meet_leader;

	@Expose
	private short meet_duration;

	@Enumerated(EnumType.ORDINAL)
	@Column(columnDefinition = "int2")
	@Expose
	private MeetingType meet_type;

	@Enumerated(EnumType.ORDINAL)
	@Column(columnDefinition = "int2")
	@Expose
	private MeetingState meet_state;

	@Expose
	private Timestamp meet_time_start;

	@Expose
	private Timestamp meet_time_end;

	@OneToMany(targetEntity=meeting_date.class, mappedBy="meet_id", fetch=FetchType.EAGER)
	@Expose
	private Set<meeting_date> meet_dates;

	@OneToMany(targetEntity=subject.class, mappedBy="meet_id", fetch=FetchType.EAGER)
	@OrderBy("subject_order")
	@Expose
	private Set<subject> subjects = new HashSet<>();

	@OneToMany(targetEntity=attend.class, mappedBy="meet_id", fetch=FetchType.EAGER)
	@Expose
	Set<attend> attendants = new HashSet<>();

	public void addAttendant(attend atobj) {
		attendants.add(atobj);
	}

	public static meeting addMeeting(
			Session session,
			int meet_leader,
			String meet_title,
			String meet_description,
			short meet_duration,
			MeetingType meet_type
	) {
		meeting newMeeting;

		meeting_date meeting_date = new meeting_date();
		meeting_date.setMeetDate(new Timestamp(System.currentTimeMillis()));

		newMeeting = new meeting();
		newMeeting.meet_title = meet_title;
		newMeeting.meet_leader = session.get(user.class, meet_leader);
		newMeeting.meet_description = meet_description;
		newMeeting.meet_duration = meet_duration;
		newMeeting.meet_type = meet_type;
		newMeeting.meet_state = MeetingState.EDIT;

		newMeeting.meet_dates = new HashSet<>();
		newMeeting.meet_dates.add(meeting_date);

		try {
			session.beginTransaction();

			session.save(newMeeting);

			meeting_date.setMeetId(newMeeting.meet_id);
			session.persist(meeting_date);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		return newMeeting;
	}

	public void addSubject(subject subject) {
		subjects.add(subject);
	}

	public int getSubjectMaxOrder() {
		int max = -1;
		for (subject subject : subjects) {
			if (subject.getSubjectOrder() > max)
				max = subject.getSubjectOrder();
		}
		return max;
	}

	public void finishRunningSubjects(Session session) {
		boolean isOnTrx = session.getTransaction() != null && session.getTransaction().getStatus() == TransactionStatus.ACTIVE;
		try {
			if (!isOnTrx)
				session.beginTransaction();

			// FINISH PREVIOUS SUBJECTS
			getMeetSubjects().forEach((sbjobj) -> {
				if (sbjobj.isExecuting()) {
					sbjobj.finishExecution();
					session.persist(sbjobj);
				}
			});

			if (!isOnTrx)
				session.getTransaction().commit();
		} catch (HibernateException ex){
			if (!isOnTrx)
				session.getTransaction().rollback();
			throw ex;
		}
	}

	public void removeSubject(subject subject) {
		subjects.remove(subject);
	}

	public void removeAttendant(attend attdobj) {
		attendants.remove(attdobj);
	}

	public static meeting fromMap(Session session, Map<String, Object> map) throws ParseException {
		meeting meet = session.get(meeting.class, ((Double)map.get("meet_id")).intValue());
		Map meetLeader = (Map) map.get("meet_leader");

		meet.meet_id = ((Double)map.get("meet_id")).intValue();
		meet.meet_title = map.get("meet_title").toString();
		meet.meet_description = (String) map.get("meet_description");
		meet.meet_leader = session.get(user.class, ((Double)meetLeader.get("user_id")).intValue());
		meet.meet_duration = ((Double)map.get("meet_duration")).shortValue();
		meet.meet_type = MeetingType.fromKey(((Double)map.get("meet_type")).shortValue());
		meet.meet_state = MeetingState.fromKey(((Double)map.get("meet_state")).shortValue());

		Iterator<meeting_date> dateIterator = meet.meet_dates.iterator();
		for (Map map_meet_date : (List<Map>) map.get("meet_dates")) {
			dateIterator.next()
					.setMeetDate(DateUtils.parseISOTimestampString2Java(map_meet_date.get("meet_date").toString()));
		}

		return meet;
	}

	public List<user> searchPendingAttendants(Session session, String query) throws InterruptedException {
		EntityManager em = session.getEntityManagerFactory().createEntityManager();

		try {
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
			fullTextEntityManager.createIndexer().startAndWait();

			// create native Lucene query unsing the query DSL
			// alternatively you can write the Lucene query using the Lucene query parser
			// or the Lucene programmatic API. The Hibernate Search DSL is recommended though
			QueryBuilder qb = fullTextEntityManager
					.getSearchFactory()
					.buildQueryBuilder()
					.forEntity(user.class)
					.get();

			BooleanJunction whereClause = qb
					.bool()
					.must(
							qb.keyword()
									.fuzzy()
									.onFields("user_firstname", "user_lastname", "user_company", "auth_user.user_mail")
									.matching(query)
									.createQuery()
					).must(
							qb.simpleQueryString().onField("user_id").matching(Integer.toString(meet_leader.getUserId())).createQuery()
					).not();

			Iterator<attend> iterator = attendants.iterator();
			while (iterator.hasNext()) {
				whereClause.must(
						qb.simpleQueryString().onField("user_id").matching(Integer.toString(iterator.next().getUser().getUserId())).createQuery()
				).not();
			}

			// wrap Lucene query in a javax.persistence.Query
			FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(whereClause.createQuery(), user.class);

			org.apache.lucene.search.Sort sort = new Sort(SortField.FIELD_SCORE);
			jpaQuery.setSort(sort);

			// execute search
			return jpaQuery.getResultList();
		} finally {
			em.close();
		}
	}

	public meeting setMeetState(Session session, MeetingState state) {
		try {
			this.meet_state = state;

			session.beginTransaction();

			session.persist(this);

			session.getTransaction().commit();

			return this;
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public meeting startMeeting(Session session) {
		try {
			this.meet_state = MeetingState.STARTED;
			this.meet_time_start = new Timestamp(System.currentTimeMillis());

			session.beginTransaction();

			session.persist(this);

			session.getTransaction().commit();

			return this;
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public meeting concludeMeeting(Session session) {
		try {
			this.meet_state = MeetingState.CONCLUDED;

			session.beginTransaction();

			session.persist(this);

			session.getTransaction().commit();

			return this;
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public meeting endMeeting(Session session) {
		try {
			this.meet_state = MeetingState.ENDED;
			this.meet_time_end = new Timestamp(System.currentTimeMillis());

			session.beginTransaction();

			finishRunningSubjects(session);

			session.persist(this);

			session.getTransaction().commit();

			return this;
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public Set<meeting_date> getMeetDates() {
		return meet_dates;
	}

	public Integer getMeetId() {
		return meet_id;
	}

	public String getMeetTitle() {
		return meet_title;
	}

	public String getMeetDescription() {
		return meet_description;
	}

	public short getMeetDuration() {
		return meet_duration;
	}

	public MeetingType getMeetType() {
		return meet_type;
	}

	public static List<meeting> getOnEditMeetings(Session session, int meet_leader) {
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

		try {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<meeting> query = criteriaBuilder.createQuery(meeting.class);
			Root<meeting> from = query.from(meeting.class);

			Join<meeting_date, Object> meet_dates = from.join("meet_dates", JoinType.INNER);

			query.orderBy(criteriaBuilder.asc(meet_dates.get("meet_date")));

			CriteriaQuery<meeting> select = query.select(from);

			query.where(
					criteriaBuilder.equal(from.get("meet_leader"), meet_leader),
					criteriaBuilder.equal(from.get("meet_state"), MeetingState.EDIT)
			);

			TypedQuery<meeting> query1 = entityManager.createQuery(select);

			return query1.getResultList();
		} finally {
			entityManager.close();
		}
	}

	public static List<meeting> getForegoingMeetings(Session session, int user_id) {
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

		try {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<meeting> query = criteriaBuilder.createQuery(meeting.class);
			Root<meeting> from = query.from(meeting.class);

			Join<meeting_date, Object> meet_dates = from.join("meet_dates", JoinType.INNER);

			Subquery<attend> attendantSubQuery = query.subquery(attend.class);
			Root<attend> attendantsFrom = attendantSubQuery.from(attend.class);
			attendantSubQuery.where(criteriaBuilder.equal(attendantsFrom.get("meet_id"), from.get("meet_id")));
			attendantSubQuery.select(attendantsFrom);

			query.orderBy(criteriaBuilder.asc(meet_dates.get("meet_date")));

			CriteriaQuery<meeting> select = query.select(from);

			query.where(
				criteriaBuilder.or(
					criteriaBuilder.equal(from.get("meet_leader"), user_id),
					criteriaBuilder.exists(attendantSubQuery)
				),
				criteriaBuilder.notEqual(from.get("meet_state"), MeetingState.STARTED),
				criteriaBuilder.or(
					criteriaBuilder.greaterThanOrEqualTo(from.get("meet_state"), MeetingState.CANCELLED),
					criteriaBuilder.lessThanOrEqualTo(meet_dates.get("meet_date"), criteriaBuilder.currentTimestamp())
				)
			);

			TypedQuery<meeting> query1 = entityManager.createQuery(select);

			return query1.getResultList();
		} finally {
			entityManager.close();
		}
	}

	public static  List<meeting> getForthcommingMeetings(Session session, int user_id) {
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

		try {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<meeting> query = criteriaBuilder.createQuery(meeting.class);
			Root<meeting> from = query.from(meeting.class);

			Join<meeting_date, Object> meet_dates = from.join("meet_dates", JoinType.INNER);

			Subquery<attend> attendantSubQuery = query.subquery(attend.class);
			Root<attend> attendantsFrom = attendantSubQuery.from(attend.class);
			attendantSubQuery.where(criteriaBuilder.equal(attendantsFrom.get("meet_id"), from.get("meet_id")));
			attendantSubQuery.select(attendantsFrom);

			query.orderBy(criteriaBuilder.asc(meet_dates.get("meet_date")));

			CriteriaQuery<meeting> select = query.select(from);

			query.where(
				criteriaBuilder.or(
					criteriaBuilder.equal(from.get("meet_leader"), user_id),
					criteriaBuilder.exists(attendantSubQuery)
				),
				criteriaBuilder.or(
						criteriaBuilder.equal(from.get("meet_state"), MeetingState.STARTED),
						criteriaBuilder.and(
								criteriaBuilder.lessThan(from.get("meet_state"), MeetingState.CANCELLED),
								criteriaBuilder.greaterThan(meet_dates.get("meet_date"), criteriaBuilder.currentTimestamp())
						)
				)
			);

			TypedQuery<meeting> query1 = entityManager.createQuery(select);

			return query1.getResultList();
		} finally {
			entityManager.close();
		}
	}

	public static void updateMeeting(Session session, int meet_id, String meet_title) {
		meeting meeting = session.get(meeting.class, meet_id);
		meeting.meet_title = meet_title;

		try {
			session.beginTransaction();

			session.persist(meeting);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public Set<attend> getMeetAttendants() {
		return attendants;
	}

	public attend getMeetAttendant(int user_id) {
		attend attd = null;

		Iterator<attend> iterator = attendants.iterator();
		while (attd == null && iterator.hasNext()) {
			attend attd_tmp = iterator.next();
			if (attd_tmp.getUser().getUserId() == user_id)
				attd = attd_tmp;
		}

		return attd;
	}

	public user getMeetLeader() {
		return meet_leader;
	}

	public Collection<subject> getMeetSubjects() {
		return subjects;
	}

	public MeetingState getMeetState() {
		return meet_state;
	}

	public Timestamp getMeetTimeStart() {
		return meet_time_start;
	}

	public Timestamp getMeetTimeEnd() {
		return meet_time_end;
	}

	public void createMeeting(Session session) throws IOException, TemplateException, MessagingException {
		session.get(meeting.class, meet_id).setMeetState(session, MeetingState.READY);
		MailConfiguration.getInstance().sendMail(new MailMeetInvitation(this).getMail());
	}
}
