package rest.app;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.schedule;
import model.user;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.Session;
import rest.util.PageCommons;
import rest.util.RestSessionUtils;
import sun.misc.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/account")
public class AccountResource {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response userAccount(@Context HttpServletRequest request) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			Map<String, Object> appData = new HashMap<>();
			user app_user = RestSessionUtils.getUserApp(request, session);

			List<Map<String, String>> workingHours = new ArrayList<>();
			for (schedule schedule : schedule.getSchedulesList(session)) {
				Map<String, String> scheduleValues = new HashMap<>();
				scheduleValues.put("value", schedule.getScheduleCode());
				scheduleValues.put("text", schedule.getScheduleDescription());
				workingHours.add(scheduleValues);
			}

			appData.put("user_email", app_user.getUserAuth().getUserMail());
			appData.put("user_firstname", app_user.getUserFirstname());
			appData.put("user_lastname", app_user.getUserLastname());
			appData.put("user_company", app_user.getUserCompany());
			appData.put("user_phone", app_user.getUserPhone());
			appData.put("user_photo", app_user.getUserPhoto() != null && app_user.getUserPhoto().length > 0 ? "/app/account/photo" : null);
			appData.put("user_schedule", app_user.getUserSchedule() != null ? app_user.getUserSchedule().getScheduleCode() : null);
			appData.put("working_hours", workingHours);

			Map<String, String> navigation = new HashMap<>();
			navigation.put("back", "/");
			appData.put("navigation", navigation);

			return Response.accepted().entity(
					FTLParser.getParsedStringFromFile(
							FTLConfiguration.getInstance(),
							PageCommons.getFTLHeaderInfo(
									request,
									session,
									"i18n/account",
									"account",
									"account.js",
									"account.css",
									appData),
							"webapp/vueapp.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

	@Path("/photo")
	@GET
	@Produces("image/*")
	public Response userPhoto(@Context HttpServletRequest request) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			user app_user = RestSessionUtils.getUserApp(request, session);

			InputStream is = new BufferedInputStream(new ByteArrayInputStream(app_user.getUserPhoto()));

			return Response.accepted().entity(is).build();
		} finally {
			session.close();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updateAccount(
		@Context HttpServletRequest request,
		@FormParam("user_firstname") String user_firstname,
		@FormParam("user_lastname") String user_lastname,
		@FormParam("user_company") String user_company,
		@FormParam("user_phone") String user_phone,
		@FormParam("user_schedule") String user_schedule
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			user app_user = RestSessionUtils.getUserApp(request, session);

			app_user.updateUser(session,user_firstname, user_lastname, user_company, user_phone, user_schedule);

			return Response.seeOther(URI.create("/")).build();
		} finally {
			session.close();
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response updateAccountWithPhoto(
		@Context HttpServletRequest request,
		@FormDataParam("user_firstname") String user_firstname,
		@FormDataParam("user_lastname") String user_lastname,
		@FormDataParam("user_company") String user_company,
		@FormDataParam("user_phone") String user_phone,
		@FormDataParam("user_photo") InputStream user_photo,
		@FormDataParam("user_schedule") String user_schedule
	) throws IOException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			user app_user = RestSessionUtils.getUserApp(request, session);

			app_user.updateUser(session,user_firstname, user_lastname, user_company, user_phone, IOUtils.readFully(user_photo, -1, false), user_schedule);

			return Response.seeOther(URI.create("/")).build();
		} finally {
			session.close();
		}
	}
}
