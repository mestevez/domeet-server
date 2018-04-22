package rest.app;

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

@Path("/")
public class HomeResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String home(@Context HttpServletRequest request) throws IOException, TemplateException {
		return FTLParser.getParsedString(
				FTLConfiguration.getInstance(),
				PageCommons.getFTLHeaderInfo(request, "ftl/webapp/home/home"),
				"webapp/home/home.ftlh");
	}
}
