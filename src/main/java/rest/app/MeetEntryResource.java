package rest.app;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import org.hibernate.Session;
import rest.util.PageCommons;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Path("/meet/entry")
public class MeetEntryResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response meetingEntryPage(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			Map<String, Object> ftlHeaderInfo = PageCommons.getFTLHeaderInfo(request, session, "ftl/webapp/meeting/meetentry");

			return Response.accepted().entity(
					FTLParser.getParsedString(
							FTLConfiguration.getInstance(),
							ftlHeaderInfo,
							"webapp/meeting/meetentry.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

}
