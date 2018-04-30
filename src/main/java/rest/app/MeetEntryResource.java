package rest.app;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.MeetingType;
import model.meeting;
import org.hibernate.Session;
import rest.util.PageCommons;
import rest.util.RestSessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/meet")
public class MeetEntryResource {

	@Path("/entry")
	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response meetingEntryPage(@Context HttpServletRequest request) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			int meet_id = meeting.addMeeting(
					session,
					RestSessionUtils.getUserAuth(request, session).getUserID(),
					"[ Auto ]",
					null,
					new Short("30"),
					MeetingType.UNDETERMINED
			).getMeetId();

			return Response.seeOther(UriBuilder.fromPath("/app/meet/edit/{meet_id}").build(meet_id)).build();
		} finally {
			session.close();
		}
	}

	@Path("/edit/{meet_id}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response meetingEditPage(
		@Context HttpServletRequest request,
		@PathParam("meet_id") int meet_id
	) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			String bundlepath = "i18n/meetedit";
			Map<String, Object> appData = new HashMap<>();

			meeting meet = session.get(meeting.class, meet_id);

			ResourceBundle bundle = ResourceBundle.getBundle(bundlepath, request.getLocale());
			List<Map<String, Object>> meetingTypes = new ArrayList<>();
			for (MeetingType meetingTypeValue : MeetingType.values()) {
				Map<String, Object> meetingType = new HashMap<>();
				meetingType.put("value", meetingTypeValue.ordinal());
				meetingType.put("text", bundle != null ? bundle.getString("meet_type." + meetingTypeValue.name()) : meetingTypeValue.name());
				meetingTypes.add(meetingType);
			}

			appData.put("meet_title", meet.getMeetTitle());
			appData.put("meet_description", meet.getMeetDescription());
			appData.put("meet_duration", meet.getMeetDuration());
			appData.put("meet_type", meet.getMeetType().ordinal());
			appData.put("meet_date", meet.getMeetDates().iterator().next().getMeetDate());
			appData.put("meet_types", meetingTypes);

			Map<String, String> navigation = new HashMap<>();
			navigation.put("back", "/");
			appData.put("navigation", navigation);

			return Response.accepted().entity(
					FTLParser.getParsedString(
							FTLConfiguration.getInstance(),
							PageCommons.getFTLHeaderInfo(
									request,
									session,
									bundlepath,
									"meetedit",
									"meetedit.js",
									"meetedit.css",
									appData),
							"webapp/vueapp.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

}
