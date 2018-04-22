package rest.login;

import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
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
public class LoginResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String login(@Context HttpServletRequest request) throws IOException, TemplateException {
		return FTLParser.getParsedString(
				FTLConfiguration.getInstance(),
				PageCommons.getFTLHeaderInfo(request, "ftl/webapp/login/login"),
				"webapp/login/login.ftlh");
	}

	@Path("failed")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String failed(@Context HttpServletRequest request) throws IOException, TemplateException {
		return FTLParser.getParsedString(
				FTLConfiguration.getInstance(),
				PageCommons.getFTLHeaderInfo(request, "ftl/webapp/login/login"),
				"webapp/login/login.ftlh");
	}
}