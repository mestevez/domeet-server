package model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.Expose;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table( schema="app", name="subject_note" )
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class subject_note {

	@Id
	@SequenceGenerator(initialValue=101,name="note_generator", sequenceName="note_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="note_generator")
	@Expose
	private Integer note_id;

	@Expose
	private Integer subject_id;

	@Expose
	private Integer note_order;

	@Expose
	private String note_description;

	@Enumerated(EnumType.ORDINAL)
	@Column(columnDefinition = "int2")
	@Expose
	private SubjectNoteType note_type;

	public static subject_note addNote(
			Session session,
			subject subject,
			int order,
			String description,
			SubjectNoteType type
	) {
		subject_note note = new subject_note();

		try {
			session.beginTransaction();

			note.subject_id = subject.getSubjectId();
			note.note_order = order;
			note.note_description = description;
			note.note_type = type;
			subject.addNote(session, note);

			session.persist(note);

			session.getTransaction().commit();

		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		// Update subject notes list
		try {
			session.beginTransaction();
			session.refresh(subject);
		} finally {
			session.getTransaction().commit();
		}

		return note;
	}

	public static subject_note fromMap(Session session, Map<String, Object> map) {
		subject_note note = session.get(subject_note.class, ((Double)map.get("note_id")).intValue());

		note.note_description = map.get("note_description").toString();
		note.note_order = ((Double)map.get("note_order")).intValue();
		note.note_type = SubjectNoteType.fromKey(((Double)map.get("note_type")).shortValue());

		return note;
	}


	public int getNoteOrder() {
		return note_order;
	}

	public SubjectNoteType getNoteType() {
		return note_type;
	}

	public void deleteNote(Session session) {
		subject sbjobj = session.get(subject.class, subject_id);

		try {
			session.beginTransaction();

			session.remove(this);
			sbjobj.removeNote(session, this);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		// Update meet subjects list
		try {
			session.beginTransaction();
			session.refresh(sbjobj);
		} finally {
			session.getTransaction().commit();
		}
	}

	public void notifyNoteChanges(Session session) {
		session.get(subject.class, subject_id).notifySubjectChanges(session);
	}

	public void updateNote(Session session) {
		try {
			session.beginTransaction();

			session.merge(this);

			notifyNoteChanges(session);

			session.getTransaction().commit();

		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}
}
