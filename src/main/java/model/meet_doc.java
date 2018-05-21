package model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.Expose;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;

@Entity
@Table( schema="app", name="meet_doc" )
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class meet_doc {

	@Id
	@SequenceGenerator(name="doc_generator", schema="app", sequenceName="doc_seq", initialValue = 100)
	@GeneratedValue(strategy = GenerationType.AUTO, generator="doc_generator")
	@Expose
	private Integer doc_id;

	@Expose
	private Integer meet_id;

	@Expose
	private String doc_name;

	private byte[] doc_data;

	@Expose
	private String doc_mimetype;

	@Expose
	private int doc_size;

	public static meet_doc addDocument(
		Session session,
		meeting meet,
		String doc_name,
		byte[] doc_data,
		String doc_mimetype,
		long size
	) {
		meet_doc docobj = new meet_doc();

		try {
			session.beginTransaction();

			docobj.meet_id = meet.getMeetId();
			docobj.doc_name = doc_name;
			docobj.doc_data = doc_data;
			docobj.doc_mimetype = doc_mimetype;
			docobj.doc_size = (int) size;

			session.persist(docobj);

			session.getTransaction().commit();

			meet.addDocument(session, docobj);

		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}

		return docobj;
	}

	public String getDocumentName() {
		return doc_name;
	}

	public void deleteDocument(Session session) {
		meeting meet = session.get(meeting.class, meet_id);
		try {
			session.beginTransaction();

			meet.removeDocument(session, this);
			session.remove(this);

			session.getTransaction().commit();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		}
	}

	public byte[] getDocumentData() {
		return doc_data;
	}
}
