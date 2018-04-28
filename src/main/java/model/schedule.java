package model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table( schema="app", name="schedule" )
public class schedule implements Serializable {

	@Id
	@Column(columnDefinition = "bpchar")
	private String sch_code;

	private String sch_description;

	@OneToMany(targetEntity=schedule_time.class, mappedBy="sch_code", fetch=FetchType.EAGER)
	private Set<schedule_time> schedule_times;

	public static List<schedule> getSchedulesList(SessionFactory hibernateSessionFactory) {
		EntityManager entityManager = hibernateSessionFactory.createEntityManager();

		try {
			return entityManager.createQuery("SELECT a FROM schedule a").getResultList();
		} finally {
			entityManager.close();
		}
	}

	public static schedule getSchedule(Session session, String sch_code) {
		return session.get(schedule.class, String.format("%1$-" + 10 + "s", sch_code));
	}

	public String getScheduleCode() {
		// Apply trim because as bpchar returns all its length definition, appending whitespaces at the end
		return sch_code.trim();
	}

	public String getScheduleDescription() {
		return sch_description;
	}

	public boolean isOnWorkTime(Date checkDate) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(checkDate);

		boolean result = false;
		Iterator<schedule_time> iterator = schedule_times.iterator();
		while (!result && iterator.hasNext()) {
			result = iterator.next().isOnWorkTime(cal);
		}
		return result;
	}
}
