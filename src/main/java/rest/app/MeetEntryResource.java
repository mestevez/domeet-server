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

	private static String _getMeetEditPage(HttpServletRequest request, Session session, meeting meet) throws IOException, TemplateException {
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

		appData.put("meet", meet);

		Map<String, String> navigation = new HashMap<>();
		navigation.put("meetedit", "/app/meet/{meet_id}");
		navigation.put("back", "/");
		appData.put("navigation", navigation);

		return FTLParser.getParsedStringFromFile(
				FTLConfiguration.getInstance(),
				PageCommons.getFTLHeaderInfo(
						request,
						session,
						bundlepath,
						"meetedit",
						"meetedit.js",
						"meetedit.css",
						appData),
				"webapp/vueapp.ftlh");
	}

	private static String _getMeetExecutionPage(HttpServletRequest request, Session session, meeting meet) throws IOException, TemplateException {
		String bundlepath = "i18n/meetexec";
		Map<String, Object> appData = new HashMap<>();

		appData.put("meet", meet);

		Map<String, String> navigation = new HashMap<>();
		navigation.put("back", "/");
		appData.put("navigation", navigation);

		return FTLParser.getParsedStringFromFile(
				FTLConfiguration.getInstance(),
				PageCommons.getFTLHeaderInfo(
						request,
						session,
						bundlepath,
						"meetexec",
						"meetexec.js",
						"meetexec.css",
						appData),
				"webapp/vueapp.ftlh");
	}

	private static String _getMeetConcludingPage(HttpServletRequest request, Session session, meeting meet) throws IOException, TemplateException {
		String bundlepath = "i18n/meetconclude";
		Map<String, Object> appData = new HashMap<>();

		appData.put("meet", meet);

		Map<String, String> navigation = new HashMap<>();
		navigation.put("back", "/");
		appData.put("navigation", navigation);

		return FTLParser.getParsedStringFromFile(
				FTLConfiguration.getInstance(),
				PageCommons.getFTLHeaderInfo(
						request,
						session,
						bundlepath,
						"meetconclude",
						"meetconclude.js",
						"meetconclude.css",
						appData),
				"webapp/vueapp.ftlh");
	}

	private static String _getMeetInfoPage(HttpServletRequest request, Session session, meeting meet) throws IOException, TemplateException {
		String bundlepath = "i18n/meetinfo";
		Map<String, Object> appData = new HashMap<>();

		appData.put("meet", meet);

		Map<String, String> navigation = new HashMap<>();
		navigation.put("back", "/");
		appData.put("navigation", navigation);

		return FTLParser.getParsedStringFromFile(
				FTLConfiguration.getInstance(),
				PageCommons.getFTLHeaderInfo(
						request,
						session,
						bundlepath,
						"meetinfo",
						"meetinfo.js",
						"meetinfo.css",
						appData),
				"webapp/vueapp.ftlh");
	}

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

			return Response.seeOther(UriBuilder.fromPath("/app/meet/{meet_id}").build(meet_id)).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response meetingDispatcher(
		@Context HttpServletRequest request,
		@PathParam("meet_id") int meet_id
	) throws IOException, TemplateException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			String ftl;
			meeting meet = session.get(meeting.class, meet_id);

			if (meet.getMeetState() == MeetingState.STARTED)
				ftl = _getMeetExecutionPage(request, session, meet);
			else if (meet.getMeetState() == MeetingState.ENDED)
				ftl = _getMeetConcludingPage(request, session, meet);
			else if (meet.getMeetState() == MeetingState.CONCLUDED || meet.getMeetState() == MeetingState.MAIL_SENT)
				ftl = _getMeetInfoPage(request, session, meet);
			else
				ftl = _getMeetEditPage(request, session, meet);

			return Response.accepted().entity(ftl).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}")
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

	@Path("/{meet_id}")
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

	@Path("/{meet_id}/create")
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

	@Path("/{meet_id}/start")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingStart(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).startMeeting(session);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("redirect", UriBuilder.fromPath("/app/meet/{meet_id}").build(meet_id));
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/end")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingEnd(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).endMeeting(session);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("redirect", UriBuilder.fromPath("/app/meet/{meet_id}").build(meet_id));
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/conclude")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response meetingConclude(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).setMeetState(session, MeetingState.CONCLUDED);

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("redirect", UriBuilder.fromPath("/app/meet/{meet_id}").build(meet_id));
			return Response.ok().entity(responseData).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/cancel")
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

	@Path("/subject/{subject_id}/note")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response noteCreate(
			@Context HttpServletRequest request,
			@PathParam("subject_id") int subject_id,
			@QueryParam("note_type") String note_type
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			subject sbjobj = session.get(subject.class, subject_id);
			SubjectNoteType type = SubjectNoteType.fromKey(Short.parseShort(note_type));

			int nextOrder = sbjobj.getNotesMaxOrder(type) < 0 ? 0 : sbjobj.getNotesMaxOrder(type) + 5;
			subject_note.addNote(session, sbjobj, nextOrder, "[ Auto ]", type);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/note/{note_id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response noteDelete(
		@Context HttpServletRequest request,
		@PathParam("note_id") int note_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(subject_note.class, note_id).deleteNote(session);
			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/note/{note_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response noteSave(
		@Context HttpServletRequest request,
		subject_note note
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.beginTransaction();

			session.merge(note);

			session.getTransaction().commit();

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/searchAttendants")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response searchAttendants(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id,
			@QueryParam("q") String query
	) throws InterruptedException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);
			return Response.ok().entity(meet.searchPendingAttendants(session, query)).build();
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
			meeting meet = session.get(meeting.class, meet_id);

			subject.addSubject(session, meet, meet.getSubjectMaxOrder() < 0 ? 0 : meet.getSubjectMaxOrder() + 5, "[ Auto ]", new Short("10"), SubjectPriority.NORMAL);

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

	@Path("/subject/{subject_id}/start")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response subjectStart(@Context HttpServletRequest request, @PathParam("subject_id") int subject_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(subject.class, subject_id).startSubject(session);

			return Response.ok().build();
		} catch (HibernateException ex){
			session.getTransaction().rollback();
			throw ex;
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/attend/{user_id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantAdd(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id, @PathParam("user_id") int user_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);
			user user = session.get(user.class, user_id);

			attend.addAttendant(session, meet, user);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/attend/{user_id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantDelete(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id, @PathParam("user_id") int user_id) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);

			meet.getMeetAttendant(user_id).deleteAttendant(session);
			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/attend/{user_id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantSave(@Context HttpServletRequest request, attend att) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.beginTransaction();

			session.merge(att);

			session.getTransaction().commit();

			return Response.ok().build();
		} finally {
			session.close();
		}
	}
}
