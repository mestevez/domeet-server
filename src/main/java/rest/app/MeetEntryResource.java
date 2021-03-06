package rest.app;

import conf.database.MainDatabaseProps;
import fop.FOPMeeting;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import hibernate.SessionFactoryProvider;
import model.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.xml.sax.SAXException;
import rest.util.PageCommons;
import rest.util.RestSessionUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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
		appData.put("meet_issues", meet.validate(session, request.getLocale()));

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
		appData.put("meet_issues", meet.validate(session, request.getLocale()));

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
		appData.put("meet_issues", meet.validate(session, request.getLocale()));

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

			if (meet == null)
				throw new NotFoundException("The requested meeting does not exists anymore");

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
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
	public Response meetingDispatcherJSON(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return Response.accepted().entity(session.get(meeting.class, meet_id)).build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/issues")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response meetingIssues(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			return Response.accepted().entity(session.get(meeting.class, meet_id).validate(session, request.getLocale())).build();
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
			meet.updateMeeting(session);

			return Response.ok().build();
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
			session.get(meeting.class, meet_id).deleteMeeting(session);

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
	public Response meetingCreate(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) throws TemplateException, IOException, MessagingException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).createMeeting(session);

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
	public Response meetingConclude(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id,
			@QueryParam("send_mail") @DefaultValue("false") boolean send_mail)
			throws TemplateException, IOException, MessagingException, TransformerException, SAXException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meeting.class, meet_id).concludeMeeting(session, send_mail);

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

	@Path("/{meet_id}/meet_doc")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response documentAdd(
		@Context HttpServletRequest request,
		@PathParam("meet_id") int meet_id,
		@FormDataParam("file") InputStream fileInputStream,
		@FormDataParam("file") FormDataContentDisposition contentDispositionHeader
	) throws IOException {
		String filepath = util.Path.getTempPathFile("upload", contentDispositionHeader.getFileName());
		java.nio.file.Path path = FileSystems.getDefault().getPath(filepath);

		Files.copy(fileInputStream, path);
		byte[] bytes = Files.readAllBytes(path);

		String mymetype = Files.probeContentType(path);

		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meet_doc.addDocument(
					session,
					session.get(meeting.class, meet_id),
					contentDispositionHeader.getFileName(),
					bytes,
					mymetype,
					bytes.length);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/meet_doc/{doc_id}")
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response documenGet(
		@Context HttpServletRequest request,
		@PathParam("meet_id") int meet_id,
		@PathParam("doc_id") int doc_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meet_doc doc = session.get(meet_doc.class, doc_id);
			InputStream is = new BufferedInputStream(new ByteArrayInputStream(doc.getDocumentData()));

			return Response
					.ok(is, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition","attachment; filename=\"" + doc.getDocumentName() + "\"")
					.build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/meet_doc/{doc_id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response documenDelete(
		@Context HttpServletRequest request,
		@PathParam("meet_id") int meet_id,
		@PathParam("doc_id") int doc_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			session.get(meet_doc.class, doc_id).deleteDocument(session);
			return Response.ok().build();
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
			note.updateNote(session);

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
			subject.updateSubject(session);

			return Response.ok().build();
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

	@Path("/{meet_id}/attend/confirm")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantConfirmCurrent(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);
			int user_id = RestSessionUtils.getUserAuth(request, session).getUserID();

			meet.getMeetAttendant(user_id).confirmAttendant(session);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/attend/{user_id}/confirm")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantConfirmUser(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id,
			@PathParam("user_id") int user_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);

			meet.getMeetAttendant(user_id).confirmAttendant(session);

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

	@Path("/{meet_id}/attend/join")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantJoin(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) throws TemplateException, IOException, MessagingException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);
			int user_id = RestSessionUtils.getUserAuth(request, session).getUserID();

			meet.getMeetAttendant(user_id).joinAttendant(session);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/attend/leave")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantLeave(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) throws TemplateException, IOException, MessagingException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);
			int user_id = RestSessionUtils.getUserAuth(request, session).getUserID();

			meet.getMeetAttendant(user_id).leaveAttendant(session);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/attend/reject")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantRejectCurrent(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);
			int user_id = RestSessionUtils.getUserAuth(request, session).getUserID();

			meet.getMeetAttendant(user_id).rejectAttendant(session);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/attend/{user_id}/reject")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response attendantRejectUser(
			@Context HttpServletRequest request,
			@PathParam("meet_id") int meet_id,
			@PathParam("user_id") int user_id
	) {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);

			meet.getMeetAttendant(user_id).rejectAttendant(session);

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
			att.updateAttendant(session);

			return Response.ok().build();
		} finally {
			session.close();
		}
	}

	@Path("/{meet_id}/minute")
	@GET
	@Produces({"application/pdf"})
	public Response getMinutesOfTheMeeting(@Context HttpServletRequest request, @PathParam("meet_id") int meet_id) throws TemplateException, TransformerException, SAXException, IOException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			meeting meet = session.get(meeting.class, meet_id);

			String fileName = meet.getMeetTitle().replaceAll("\\s+", "_").toLowerCase();
			StreamingOutput fileStream = new FOPMeeting(meet, request.getLocale()).getMinutesOfTheMeeting();

			return Response
					.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
					.header("Content-Disposition","attachment; filename=\"" + fileName + ".pdf\"")
					.build();
		} finally {
			session.close();
		}
	}
}
