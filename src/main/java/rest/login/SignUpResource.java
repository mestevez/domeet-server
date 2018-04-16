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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Path("/signup")
public class SignUpResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String login(@Context HttpServletRequest request) throws IOException, TemplateException {
		return FTLParser.getParsedString(FTLConfiguration.getInstance(), PageCommons.getFTLHeaderInfo(request, "Sign Up"), "webapp/login/signup.ftlh");
	}
}