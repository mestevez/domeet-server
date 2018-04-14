package rest.admin;

import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import model.auth_user;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Path("/")
public class AdminResource {

	private static Map<String, Object> _getAdminFTLDataModel(HttpServletRequest request) {
		Map<String, Object> dataModel = new HashMap<>();
		Locale locale = request.getLocale();
		dataModel.put("lang", locale.getLanguage());
		dataModel.put("charset", "UTF-8");
		dataModel.put("pagetitle", "Admin page");

		return dataModel;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String admin(@Context HttpServletRequest request) throws IOException, TemplateException {
		return FTLParser.getParsedString(FTLConfiguration.getInstance(), _getAdminFTLDataModel(request), "webapp/admin/index.ftlh");
	}

	@Path("userslist")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String usersList(@Context HttpServletRequest request) throws IOException, TemplateException {
		Map<String, Object> dataModel = _getAdminFTLDataModel(request);
		dataModel.put("usersList", auth_user.getUsersList());
		return FTLParser.getParsedString(FTLConfiguration.getInstance(), dataModel, "webapp/admin/userslist.ftlh");
	}
}