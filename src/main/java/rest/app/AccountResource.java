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
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/account")
public class AccountResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response userAccount(@Context HttpServletRequest request) throws IOException, TemplateException {
		return Response.accepted().entity(
				FTLParser.getParsedString(
						FTLConfiguration.getInstance(),
						PageCommons.getFTLHeaderInfo(request, "ftl/webapp/account/account"),
						"webapp/account/account.ftlh")
		).build();
	}
}
