package util;

import model.*;
import org.hibernate.Session;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class BuildSamples {

	private Session session;

	public BuildSamples(Session session) {
		this.session = session;
	}

	public BiConsumer forthCommingMeetings = (BiConsumer<meeting, Integer>) (meet, index) -> {
		try {
			meet.setMeetState(session, MeetingState.READY);
			meeting_date meetDate = meet.getMeetDates().iterator().next();
			meetDate.setMeetDate(DateUtils.parseISOTimestampString2Java("2101-07-04T12:08:56.235Z"));
			session.persist(meetDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	};

	public List<meeting> buildMeetingsForUser(
			String leader,
			String[] attendants,
			int numberOfMeetings,
			BiConsumer<meeting, Integer> eachFn
	)
	{
		List<meeting> meetingsList = new ArrayList<>();
		auth_user userLeader = auth_user.getUser(session, leader);

		boolean isOnTrx = session.getTransaction() != null && session.getTransaction().getStatus() == TransactionStatus.ACTIVE;
		if (!isOnTrx)
			session.beginTransaction();

		for (int imeet = 0; imeet < numberOfMeetings; imeet++) {
			meeting meet = model.meeting.addMeeting(session, userLeader.getUserID(), "Meet 1", null, new Short("30"), MeetingType.UNDETERMINED);

			if (eachFn != null)
				eachFn.accept(meet, imeet);
			else
				forthCommingMeetings.accept(meet, imeet);

			meetingsList.add(meet);
		}

		if (!isOnTrx)
			session.getTransaction().commit();

		return meetingsList;
	}
}
