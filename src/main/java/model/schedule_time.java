package model;

import org.hibernate.Session;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.*;

@Entity
@Table( schema="app", name="schedule_time" )
public class schedule_time implements Serializable {

	@Id
	@SequenceGenerator(name="schedule_time_generator", schema="app", sequenceName="schedule_time_seq", initialValue = 100)
	@GeneratedValue(strategy = GenerationType.AUTO, generator="schedule_time_generator")
	private Integer sch_id;

	@Column(columnDefinition = "bpchar")
	private String sch_code;

	private Short sch_dayofweek;

	private Boolean sch_allday;

	@Column(columnDefinition = "bpchar")
	private String sch_hourbegin;

	@Column(columnDefinition = "bpchar")
	private String sch_hourend;

	@Formula("to_number(substring( COALESCE(sch_hourbegin, '00:00') from '^\\d\\d' ), '99')")
	private short initTimeHours;

	@Formula("to_number(substring( COALESCE(sch_hourbegin, '00:00') from '\\d\\d$' ), '99')")
	private short initTimeMinutes;

	@Formula("to_number(substring( COALESCE(sch_hourend, '00:00') from '^\\d\\d' ), '99')")
	private short endTimeHours;

	@Formula("to_number(substring( COALESCE(sch_hourend, '00:00') from '\\d\\d$' ), '99')")
	private short endTimeMinutes;

	boolean isOnWorkTime(Calendar checkTime) {
		boolean result = checkTime.get(Calendar.DAY_OF_WEEK) == sch_dayofweek;

		if (result && !sch_allday) {
			result = (
					(checkTime.get(Calendar.HOUR_OF_DAY) > initTimeHours && checkTime.get(Calendar.HOUR_OF_DAY) < endTimeHours) ||
					(checkTime.get(Calendar.HOUR_OF_DAY) == initTimeHours && checkTime.get(Calendar.MINUTE) >= initTimeMinutes) ||
					(checkTime.get(Calendar.HOUR_OF_DAY) == endTimeHours && checkTime.get(Calendar.MINUTE) <= endTimeMinutes)
			);
		}

		return result;
	}

	public static List<schedule_time> getScheduleTimes(Session session, String sch_code) {
		List<schedule_time> schedule_times;
		EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

		try {
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<schedule_time> query = criteriaBuilder.createQuery(schedule_time.class);
			Root<schedule_time> from = query.from(schedule_time.class);

			CriteriaQuery<schedule_time> select = query.select(from);
			query.where(criteriaBuilder.equal(from.get("sch_code"), sch_code));

			TypedQuery<schedule_time> query1 = entityManager.createQuery(select);

			return query1.getResultList();
		} finally {
			entityManager.close();
		}
	}

	public static boolean isOnWorkTime(Session session, String sch_code, Date checkDate) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(checkDate);

		boolean result = false;
		Iterator<schedule_time> iterator = getScheduleTimes(session, sch_code).iterator();
		while (!result && iterator.hasNext()) {
			result = iterator.next().isOnWorkTime(cal);
		}
		return result;
	}
}
