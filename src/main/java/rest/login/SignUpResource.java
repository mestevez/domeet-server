package rest.login;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.user;
import org.hibernate.SessionFactory;
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
		return Response.accepted().entity(
				FTLParser.getParsedString(FTLConfiguration.getInstance(), PageCommons.getFTLHeaderInfo(request, "Sign Up"), "webapp/login/signup.ftlh")
		).build();
		//return FTLParser.getParsedString(FTLConfiguration.getInstance(), PageCommons.getFTLHeaderInfo(request, "Sign Up"), "webapp/login/signup.ftlh");
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response signUp(
			@Context HttpServletRequest request,
			@FormParam("email") String email,
			@FormParam("password") String password
	) {
		SessionFactory sessionFactory = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps());

		user.addUser(
				sessionFactory,
				email,
				password,
				email,
				null,
				null,
				null,
				null);

		return Response.seeOther(URI.create("/login")).build();
	}
}