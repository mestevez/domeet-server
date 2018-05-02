package rest.app;

import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import rest.util.PageCommons;
import rest.util.RestSessionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.*;

@Path("/meet")
public class MeetEntryResource {

	@Path("/entry")
	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response meetingEntryPage(@Context HttpServletRequest request) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			int meet_id = meeting.addMeeting(
					session,
					RestSessionUtils.getUserAuth(request, session).getUserID(),
					"[ Auto ]",
					null,
					new Short("30"),
					MeetingType.UNDETERMINED
			).getMeetId();

			return Response.seeOther(UriBuilder.fromPath("/app/meet/edit/{meet_id}").build(meet_id)).build();
		} finally {
			session.close();
		}
	}

	@Path("/edit/{meet_id}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response meetingEditPage(
		@Context HttpServletRequest request,
		@PathParam("meet_id") int meet_id
	) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			String bundlepath = "i18n/meetedit";
			Map<String, Object> appData = new HashMap<>();

			ResourceBundle bundle = ResourceBundle.getBundle(bundlepath, request.getLocale());
			List<Map<String, Object>> meetingTypes = new ArrayList<>();
			for (MeetingType meetingTypeValue : MeetingType.values()) {
				Map<String, Object> meetingType = new HashMap<>();
				meetingType.put("value", meetingTypeValue.ordinal());
				meetingType.put("text", bundle != null ? bundle.getString("meet_type." + meetingTypeValue.name()) : meetingTypeValue.name());
				meetingTypes.add(meetingType);
			}
			appData.put("meet_types", meetingTypes);

			appData.put("meet", session.get(meeting.class, meet_id));

			Map<String, String> navigation = new HashMap<>();
			navigation.put("meetedit", "/app/meet/edit/{meet_id}");
			navigation.put("meetcreate", "/app/meet/edit/{meet_id}");
			navigation.put("meetdelete", "/app/meet/edit/{meet_id}");
			navigation.put("back", "/");
			appData.put("navigation", navigation);

			return Response.accepted().entity(
					FTLParser.getParsedString(
							FTLConfiguration.getInstance(),
							PageCommons.getFTLHeaderInfo(
									request,
									session,
									bundlepath,
									"meetedit",
									"meetedit.js",
									"meetedit.css",
									appData),
							"webapp/vueapp.ftlh")
			).build();
		} finally {
			session.close();
		}
	}

	@Path("/edit/{meet_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingSave(@Context HttpServletRequest request, meeting meet) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.beginTransaction();

			session.merge(meet);
			session.merge(meet.getMeetDates().iterator().next());

			session.getTransaction().commit();

			return Response.ok().build();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		} finally {
			session.close();
		}
	}

	@Path("/create/{meet_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingCreate(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).setMeetState(session, MeetingState.READY);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("redirect", "/");
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/start/{meet_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingStart(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).startMeeting(session);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("redirect", "/");
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/cancel/{meet_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingCancel(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).setMeetState(session, MeetingState.CANCELLED);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("redirect", "/");
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/edit/{meet_id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingDelete(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			try {
				session.beginTransaction();
				session.remove(session.get(meeting.class, meet_id));
				session.getTransaction().commit();
			} catch (HibernateException ex){
				session.getTransaction().rollback();
				throw ex;
			}

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("redirect", "/");
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/subjects")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response subjectList(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);
			return Response.ok().entity(meet.getMeetSubjects()).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/subject/entry")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response subjectCreate(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			subject.addSubject(session, session.get(meeting.class, meet_id), 0, "[ Auto ]", new Short("10"), SubjectPriority.NORMAL);

			Map<String, Object> responseData = new HashMap<>();
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/subject/{subject_id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response subjectDelete(@Context HttpServletRequest request, @PathParam("subject_id") int subject_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(subject.class, subject_id).deleteSubject(session);

			Map<String, Object> responseData = new HashMap<>();
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/subject/{subject_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response subjectSave(@Context HttpServletRequest request, subject subject) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.beginTransaction();

			session.merge(subject);

			session.getTransaction().commit();

			return Response.ok().build();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		} finally {
			session.close();
		}
	}
}
