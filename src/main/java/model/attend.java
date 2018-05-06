package model;

import com.google.gson.annotations.Expose;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table( schema="app", name="attend" )
public class attend implements Serializable {

	@Id
	@Expose
	private Integer meet_id;

	@Id
	@ManyToOne
	@JoinColumn(name="user_id")
	@Expose
	private user user_id;

	@Enumerated(EnumType.ORDINAL)
	@Column(columnDefinition = "int2")
	@Expose
	private AttendantState attd_status;

	@Expose
	private Short attd_rate;

	@Expose
	private String attd_comment;

	public static attend addAttendant(
			Session session,
			meeting meet,
			user user
	) {
		attend atobj = new attend();
		atobj.meet_id = meet.getMeetId();
		atobj.user_id = user;
		atobj.attd_status = AttendantState.PENDING;
		meet.addAttendant(atobj);

		try {
			session.beginTransaction();

			session.persist(atobj);

			session.getTransaction().commit();

		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		return atobj;
	}

	public static attend getAttend(Session session, int meet_id, int user_id) {
		return session.get(meeting.class, meet_id).getMeetAttendant(user_id);
	}

	public static attend fromMap(Session session, Map<String, Object> map) {
		Map<String, Object> userData = (Map<String, Object>) map.get("user_id");
		attend attd = getAttend(session, ((Double)map.get("meet_id")).intValue(), ((Double)userData.get("user_id")).intValue());

		attd.attd_comment = map.get("attd_comment").toString();
		attd.attd_rate = ((Double)map.get("attd_rate")).shortValue();
		attd.attd_status = AttendantState.fromKey(((Double)map.get("attd_status")).shortValue());

		return attd;
	}

	public void deleteAttendant(Session session) {
		meeting meet = session.get(meeting.class, meet_id);
		meet.removeAttendant(this);

		try {session.beginTransaction();
			session.remove(this);
			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public user getUser() {
		return user_id;
	}
}
