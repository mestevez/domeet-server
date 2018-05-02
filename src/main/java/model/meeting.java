package model;

import com.google.gson.annotations.Expose;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import util.DateUtils;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

@Entity
@Table( schema="app", name="meeting" )
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
	private List<subject> subjects = new ArrayList<>();

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

	public void setMeetState(Session session, MeetingState state) {
		try {
			this.meet_state = state;

			session.beginTransaction();

			session.persist(this);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public void startMeeting(Session session) {
		try {
			this.meet_state = MeetingState.STARTED;
			this.meet_time_start = new Timestamp(System.currentTimeMillis());

			session.beginTransaction();

			session.persist(this);

			session.getTransaction().commit();
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

			query.orderBy(criteriaBuilder.asc(meet_dates.get("meet_date")));

			CriteriaQuery<meeting> select = query.select(from);

			query.where(
				criteriaBuilder.equal(from.get("meet_leader"), user_id),
				criteriaBuilder.notEqual(from.get("meet_state"), MeetingState.EDIT),
				criteriaBuilder.lessThanOrEqualTo(meet_dates.get("meet_date"), criteriaBuilder.currentTimestamp())
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

			query.orderBy(criteriaBuilder.asc(meet_dates.get("meet_date")));

			CriteriaQuery<meeting> select = query.select(from);

			query.where(
				criteriaBuilder.equal(from.get("meet_leader"), user_id),
				criteriaBuilder.notEqual(from.get("meet_state"), MeetingState.EDIT),
				criteriaBuilder.greaterThan(meet_dates.get("meet_date"), criteriaBuilder.currentTimestamp())
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

	public user getMeetLeader() {
		return meet_leader;
	}

	public List<subject> getMeetSubjects() {
		return subjects;
	}
}