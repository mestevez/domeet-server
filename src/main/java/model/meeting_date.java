package model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table( schema="app", name="meeting_date" )
public class meeting_date implements Serializable {
	@Id
	private Integer meet_id;

	@Id
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
}
