package model;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table( schema="app", name="meeting_date" )
public class meeting_date implements Serializable {
	@Id
	@SequenceGenerator(name="meeting_date_generator", schema="app", sequenceName="meeting_date_seq", initialValue = 100)
	@GeneratedValue(strategy = GenerationType.AUTO, generator="meeting_date_generator")
	private Integer meet_date_id;

	private Integer meet_id;

	@Expose
	private Timestamp meet_date;

	public Timestamp getMeetDate() {
		return meet_date;
	}

	public void setMeetDate(Timestamp meet_date) {
		this.meet_date = meet_date;
	}

	public void setMeetId(Integer meet_id) {
		this.meet_id = meet_id;
	}

	public Integer getMeetId() {
		return meet_id;
	}
}
