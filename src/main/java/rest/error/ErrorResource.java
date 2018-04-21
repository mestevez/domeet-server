package rest.error;

import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import org.glassfish.jersey.message.internal.TracingLogger;
import rest.util.PageCommons;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class ErrorResource {

	@Context
	private HttpServletRequest httpRequest;

	private static String _getErrorPage(HttpServletRequest httpRequest) throws IOException, TemplateException {
//		Enumeration<String> attributeNames = httpRequest.getAttributeNames();
//		while(attributeNames.hasMoreElements()) {
//			String attrname = attributeNames.nextElement();
//			System.out.println(String.format("!! NAME=%s, VALUE=%s", attrname, httpRequest.getAttribute(attrname)));
//		}

		Map<String, Object> dataModel = PageCommons.getFTLHeaderInfo(httpRequest, "Error page");
		dataModel.put("status_code", httpRequest.getAttribute("javax.servlet.error.status_code"));
		dataModel.put("error_message", httpRequest.getAttribute("javax.servlet.error.message"));
		dataModel.put("error_trace", "");

		return FTLParser.getParsedString(FTLConfiguration.getInstance(), dataModel, "webapp/error/error.ftlh");
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response errorpage() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response errorpageFromPost() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}

	@PUT
	@Produces(MediaType.TEXT_HTML)
	public Response errorpageFromPut() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}

	@DELETE
	@Produces(MediaType.TEXT_HTML)
	public Response errorpageFromDelete() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}
}
