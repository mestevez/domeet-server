package rest.admin;

import conf.database.DatabaseProps;
import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.auth_user;
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
		return FTLParser.getParsedString(FTLConfiguration.getInstance(), PageCommons.getFTLHeaderInfo(request, "Admin page"), "webapp/admin/index.ftlh");
	}

	@Path("userslist")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String usersList(@Context HttpServletRequest request) throws IOException, TemplateException {
		Map<String, Object> dataModel = PageCommons.getFTLHeaderInfo(request, "Admin page");
		dataModel.put("usersList", auth_user.getUsersList(SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps())));
		return FTLParser.getParsedString(FTLConfiguration.getInstance(), dataModel, "webapp/admin/userslist.ftlh");
	}
}