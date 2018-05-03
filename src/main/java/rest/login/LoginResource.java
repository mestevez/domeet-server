package rest.login;

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
import java.net.URI;

@Path("/")
public class LoginResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response login(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return Response.accepted().entity(
					FTLParser.getParsedStringFromFile(
							FTLConfiguration.getInstance(),
							PageCommons.getFTLHeaderInfo(
									request,
									session,
									"i18n/login",
									"login",
									"login.js",
									"login.css",
									null),
							"webapp/vueapp.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

	@Path("failed")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response failed(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return Response.accepted().entity(
					FTLParser.getParsedStringFromFile(
						FTLConfiguration.getInstance(),
						PageCommons.getFTLHeaderInfo(
								request,
								session,
								"i18n/login",
								"login",
								"login.js",
								"login.css",
								null),
							"webapp/vueapp.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

	@Path("/logout")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response logout(@Context HttpServletRequest request) {
		request.getSession().invalidate();
		return Response.seeOther(URI.create("/")).build();
	}
}