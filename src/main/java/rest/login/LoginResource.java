package rest.login;

import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;

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
public class LoginResource {

	private static Map<String, Object> _getLoginFTLDataModel(HttpServletRequest request) {
		Map<String, Object> dataModel = new HashMap<>();
		Locale locale = request.getLocale();
		dataModel.put("lang", locale.getLanguage());
		dataModel.put("charset", "UTF-8");
		dataModel.put("pagetitle", "Login page");

		return dataModel;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String login(@Context HttpServletRequest request) throws IOException, TemplateException {
		return FTLParser.getParsedString(FTLConfiguration.getInstance(), _getLoginFTLDataModel(request), "login.ftlh");
	}

	@Path("failed")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String failed(@Context HttpServletRequest request) throws IOException, TemplateException {
		Map<String, Object> dataModel = _getLoginFTLDataModel(request);
		dataModel.put("failedmsg", "Login failed");

		return FTLParser.getParsedString(FTLConfiguration.getInstance(), dataModel, "login.ftlh");
	}
}