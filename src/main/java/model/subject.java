package model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.Expose;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Table( schema="app", name="subject" )
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
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

	@OneToMany(targetEntity=subject_note.class, mappedBy="subject_id", fetch=FetchType.EAGER)
	@OrderBy("note_type, note_order")
	@Expose
	private Set<subject_note> notes = new HashSet<>();

	public void addNote(subject_note note) {
		notes.add(note);
	}

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
		meet.addSubject(subj);

		try {
			session.beginTransaction();

			session.persist(subj);

			session.getTransaction().commit();

		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		// Update meet subjects list
		try {
			session.beginTransaction();
			session.refresh(meet);
		} finally {
			session.getTransaction().commit();
		}

		return subj;
	}

	public void deleteSubject(Session session) {
		meeting meet = session.get(meeting.class, meet_id);
		meet.removeSubject(this);

		try {session.beginTransaction();
			session.remove(this);
			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		// Update meet subjects list
		try {
			session.beginTransaction();
			session.refresh(meet);
		} finally {
			session.getTransaction().commit();
		}
	}

	public static subject fromMap(Session session, Map<String, Object> map) {
		subject sbjobj = session.get(subject.class, ((Double)map.get("subject_id")).intValue());

		sbjobj.subject_title = map.get("subject_title").toString();
		sbjobj.subject_order = ((Double)map.get("subject_order")).intValue();
		sbjobj.subject_duration = ((Double)map.get("subject_duration")).shortValue();
		sbjobj.subject_priority = SubjectPriority.fromKey(((Double)map.get("subject_priority")).shortValue());

		return sbjobj;
	}

	public int getNotesMaxOrder(SubjectNoteType type) {
		AtomicInteger maxOrder = new AtomicInteger(-1);
		notes.forEach((v) -> {
			if (v.getNoteType() == type && v.getNoteOrder() > maxOrder.get()) {
				maxOrder.set(v.getNoteOrder());
			}
		});
		return maxOrder.get();
	}

	public String getTitle() {
		return subject_title;
	}

	public int getSubjectOrder() {
		return subject_order;
	}

	public boolean isExecuting() {
		return subject_time_start != null && subject_time_end == null;
	}

	public void finishExecution() {
		subject_time_end = new Timestamp(System.currentTimeMillis());
	}

	public void startSubject(Session session) {
		session.get(meeting.class, meet_id).finishRunningSubjects(session);

		try {
			session.beginTransaction();

			this.subject_time_start = new Timestamp(System.currentTimeMillis());

			session.persist(this);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public Integer getSubjectId() {
		return subject_id;
	}

	public Collection<subject_note> getSubjectNotes() {
		return notes;
	}

	public void removeNote(subject_note note) {
		notes.remove(note);
	}
}
