package rest.app;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.auth_user;
import model.meeting;
import org.hibernate.Session;
import rest.util.PageCommons;
import rest.util.RestSessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class HomeResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String home(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		auth_user authUser = RestSessionUtils.getUserAuth(request, session);

		Map<String, Object> appData = new HashMap<>();

		appData.put("meetingsonedit", meeting.getOnEditMeetings(session, authUser.getUserID()));
		appData.put("meetingsforthcomming", meeting.getForthcommingMeetings(session, authUser.getUserID()));
		appData.put("meetingsforegoing", meeting.getForegoingMeetings(session, authUser.getUserID()));

		Map<String, Object> navigationMap = new HashMap<>();
		navigationMap.put("meetentry", "/app/meet/entry");
		navigationMap.put("meetedit", "/app/meet/{meet_id}");
		appData.put("navigation", navigationMap);

		try {
			return FTLParser.getParsedStringFromFile(
					FTLConfiguration.getInstance(),
					PageCommons.getFTLHeaderInfo(
							request,
							session,
							"i18n/home",
							"home",
							"home.js",
							"home.css",
							appData),
					"webapp/vueapp.ftlh");
		} finally {
			session.close();
		}
	}

	@Path("/meetings")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserMeetings(@Context HttpServletRequest request) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		auth_user authUser = RestSessionUtils.getUserAuth(request, session);

		Map<String, Object> appData = new HashMap<>();

		appData.put("meetingsonedit", meeting.getOnEditMeetings(session, authUser.getUserID()));
		appData.put("meetingsforthcomming", meeting.getForthcommingMeetings(session, authUser.getUserID()));
		appData.put("meetingsforegoing", meeting.getForegoingMeetings(session, authUser.getUserID()));

		try {
			return Response.ok().entity(appData).build();
		} finally {
			session.close();
		}
	}
}
