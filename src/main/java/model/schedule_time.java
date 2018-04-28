package model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity
@Table( schema="app", name="schedule_time" )
public class schedule_time implements Serializable {

	@Id
	@Column(columnDefinition = "bpchar")
	private String sch_code;

	@Id
	private Short sch_dayofweek;

	private Boolean sch_allday;

	@Id
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
}
