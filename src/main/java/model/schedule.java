package model;

import org.hibernate.Session;

import javax.persistence.*;
import java.util.*;

@Entity
@Table( schema="app", name="schedule" )
public class schedule {

	@Id
	@Column(columnDefinition = "bpchar")
	private String sch_code;

	private String sch_description;

	public static List<schedule> getSchedulesList(Session session) {
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

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
}
