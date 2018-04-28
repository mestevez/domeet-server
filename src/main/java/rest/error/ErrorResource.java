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

		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			Map<String, Object> dataModel = PageCommons.getFTLHeaderInfo(httpRequest, session, "ftl/webapp/error/error");
			dataModel.put("status_code", httpRequest.getAttribute("javax.servlet.error.status_code"));
			dataModel.put("error_message", httpRequest.getAttribute("javax.servlet.error.message"));
			dataModel.put("error_trace", "");

			return FTLParser.getParsedString(FTLConfiguration.getInstance(), dataModel, "webapp/error/error.ftlh");
		} finally {
			session.close();
		}
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
