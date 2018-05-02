package model;

import com.google.gson.annotations.Expose;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table( schema="app", name="subject" )
public class subject implements Serializable {

	@Id
	@SequenceGenerator(name="subject_generator", sequenceName="subject_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="subject_generator")
	@Expose
	private Integer subject_id;

	@Expose
	private Integer meet_id;

	@Expose
	private Integer subject_order;

	@Expose
	private String subject_title;

	@Expose
	private short subject_duration;

	@Enumerated(EnumType.ORDINAL)
	@Column(columnDefinition = "int2")
	@Expose
	private SubjectPriority subject_priority;

	@Expose
	private Timestamp subject_time_start;

	@Expose
	private Timestamp subject_time_end;

	public static subject addSubject(
			Session session,
			meeting meet,
			int order,
			String title,
			short duration,
			SubjectPriority priority
	) {
		subject subj = new subject();
		subj.meet_id = meet.getMeetId();
		subj.subject_order = order;
		subj.subject_title = title;
		subj.subject_duration = duration;
		subj.subject_priority = priority;

		try {
			session.beginTransaction();

			session.persist(subj);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		// Update subjects list on meeting
		session.refresh(meet);

		return subj;
	}

	public void deleteSubject(Session session) {
		meeting meet = session.get(meeting.class, meet_id);

		try {session.beginTransaction();
			session.remove(this);
			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		// Update meeting list
		session.refresh(meet);
	}

	public String getTitle() {
		return subject_title;
	}
}
