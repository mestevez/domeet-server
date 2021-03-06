package rest.login;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.user;
import org.hibernate.Session;
import rest.util.PageCommons;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

@Path("/signup")
public class SignUpResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response form(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return Response.accepted().entity(
					FTLParser.getParsedStringFromFile(
							FTLConfiguration.getInstance(),
							PageCommons.getFTLHeaderInfo(
									request,
									session,
									"i18n/signup",
									"signup",
									"signup.js",
									"signup.css",
									null),
							"webapp/vueapp.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

	@Path("/terms")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response termsAndConditions(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return Response.accepted().entity(
					FTLParser.getParsedStringFromFile(
							FTLConfiguration.getInstance(),
							PageCommons.getFTLHeaderInfo(
									request,
									session,
									"i18n/terms",
									"terms",
									"terms.js",
									"terms.css",
									null),
							"webapp/vueapp.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response signUp(
			@Context HttpServletRequest request,
			@FormParam("email") String email,
			@FormParam("password") String password
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			user.addUser(
					session,
					email,
					password,
					email,
					null,
					null,
					null,
					null,
					null);

			return Response.seeOther(URI.create("/login")).build();
		} finally {
			session.close();
		}
	}
}