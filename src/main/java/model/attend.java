package model;

import com.google.gson.annotations.Expose;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table( schema="app", name="attend" )
public class attend implements Serializable {

	@Id
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
