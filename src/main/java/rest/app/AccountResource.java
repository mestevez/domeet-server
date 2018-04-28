package rest.app;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.user;
import org.hibernate.Session;
import rest.util.PageCommons;
import rest.util.RestSessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

@Path("/account")
public class AccountResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response userAccount(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			Map<String, Object> ftlHeaderInfo = PageCommons.getFTLHeaderInfo(request, session, "ftl/webapp/account/account");
			user app_user = RestSessionUtils.getUserApp(request, session);

			ftlHeaderInfo.put("user_email", app_user.getUserAuth().getUserMail());
			ftlHeaderInfo.put("user_firstname", app_user.getUserFirstname());
			ftlHeaderInfo.put("user_lastname", app_user.getUserLastname());
			ftlHeaderInfo.put("user_company", app_user.getUserCompany());
			ftlHeaderInfo.put("user_phone", app_user.getUserPhone());
			ftlHeaderInfo.put("user_photo", app_user.getUserPhoto() != null ? "/app/account/photo" : null);
			ftlHeaderInfo.put("user_schedule", null);
			ftlHeaderInfo.put("working_hours", new ArrayList<>());

			return Response.accepted().entity(
					FTLParser.getParsedString(
							FTLConfiguration.getInstance(),
							ftlHeaderInfo,
							"webapp/account/account.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

	@Path("/photo")
	@GET
	@Produces("image/*")
	public Response userPhoto(@Context HttpServletRequest request) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			user app_user = RestSessionUtils.getUserApp(request, session);

			InputStream is = new BufferedInputStream(new ByteArrayInputStream(app_user.getUserPhoto()));

			return Response.accepted().entity(is).build();
		} finally {
			session.close();
		}
	}
}
