package rest.admin;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.auth_user;
import org.hibernate.Session;
import rest.util.PageCommons;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

@Path("/")
public class AdminResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String admin(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return FTLParser.getParsedString(
					FTLConfiguration.getInstance(),
					PageCommons.getFTLHeaderInfo(request, session, "i18n/admin", null, null, null, null),
					"webapp/admin/index.ftlh");
		} finally {
			session.close();
		}
	}

	@Path("userslist")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String usersList(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			Map<String, Object> dataModel = PageCommons.getFTLHeaderInfo(
					request,
					session,
					"i18n/admin",
					null,
					null,
					null,
					null);
			dataModel.put("usersList", auth_user.getUsersList(session));
			return FTLParser.getParsedString(FTLConfiguration.getInstance(), dataModel, "webapp/admin/userslist.ftlh");
		} finally {
			session.close();
		}
	}
}