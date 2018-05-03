package rest.error;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import org.hibernate.Session;
import rest.util.PageCommons;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class ErrorResource {

	@Context
	private HttpServletRequest httpRequest;

	private static Map<String, Object> _getErrorData(HttpServletRequest httpRequest)  {
//		Enumeration<String> attributeNames = httpRequest.getAttributeNames();
//		System.out.println("!! ATTRIBUTES:");
//		while(attributeNames.hasMoreElements()) {
//			String attrname = attributeNames.nextElement();
//			System.out.println(String.format("!! . NAME=%s, VALUE=%s", attrname, httpRequest.getAttribute(attrname)));
//		}

		Map<String, Object> errData = new HashMap<>();
		errData.put("status_code", httpRequest.getAttribute("javax.servlet.error.status_code"));
		errData.put("error_message", httpRequest.getAttribute("javax.servlet.error.message"));
		errData.put("error_trace", "");

		return errData;
	}

	private static String _getErrorPage(HttpServletRequest httpRequest) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return FTLParser.getParsedStringFromFile(
					FTLConfiguration.getInstance(),
					PageCommons.getFTLHeaderInfo(
							httpRequest,
							session,
							"i18n/error",
							"errapp",
							"error.js",
							"error.css",
							_getErrorData(httpRequest)),
					"webapp/vueapp.ftlh");
		} finally {
			session.close();
		}
	}

	private static Map<String, Object> _getErrorMessage(HttpServletRequest httpRequest) throws IOException, TemplateException {
		Map<String, Object> result = new HashMap<>();
		result.put("srverr", _getErrorData(httpRequest));
		return result;
	}

	@GET
	public Response errorpageHTML() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response errorpageHTMLFromPost() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response errorpageJSONFromPost() throws IOException, TemplateException {
		return Response.serverError().entity(_getErrorMessage(httpRequest)).build();
	}

	@PUT
	public Response errorpageHTMLFromPut() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}

	@DELETE
	public Response errorpageHTMLFromDelete() throws IOException, TemplateException {
		return Response.accepted().entity(_getErrorPage(httpRequest)).build();
	}
}
