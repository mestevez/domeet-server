package model;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( schema="app", name="meeting" )
public class meeting implements Serializable{

	@Id
	@SequenceGenerator(name="meeting_generator", sequenceName="meeting_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="meeting_generator")
	private Integer meet_id;

	private String meet_title;
	private String meet_description;
	private int meet_leader;
	private short meet_duration;

	@Enumerated
	@Column(columnDefinition = "int2")
	private MeetingType meet_type;

	@Enumerated
	@Column(columnDefinition = "int2")
	private MeetingState meet_state;

	@OneToMany(targetEntity=meeting_date.class, mappedBy="meet_id", fetch=FetchType.EAGER)
	private Set<meeting_date> meet_dates;

	public static meeting addMeeting(
			Session session,
			int meet_leader,
			String meet_title,
			String meet_description,
			short meet_duration,
			MeetingType meet_type
	) {
		meeting newMeeting;

		try {
			meeting_date meeting_date = new meeting_date();
			meeting_date.setMeetDate(new Timestamp(System.currentTimeMillis()));

			newMeeting = new meeting();
			newMeeting.meet_title = meet_title;
			newMeeting.meet_leader = meet_leader;
			newMeeting.meet_description = meet_description;
			newMeeting.meet_duration = meet_duration;
			newMeeting.meet_type = meet_type;
			newMeeting.meet_state = MeetingState.EDIT;

			newMeeting.meet_dates = new HashSet<>();
			newMeeting.meet_dates.add(meeting_date);

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
}
