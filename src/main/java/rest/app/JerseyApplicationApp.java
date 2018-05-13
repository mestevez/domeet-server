package rest.app;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import rest.conf.providers.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/app")
public class JerseyApplicationApp extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();
		classes.add(HomeResource.class);
		classes.add(AccountResource.class);
		classes.add(MeetEntryResource.class);

		// Enable multipart
		classes.add(MultiPartFeature.class);

		// Jersey Readers & Writters
		classes.add(MeetingMessageBodyReader.class);
		classes.add(MeetingMessageBodyWriter.class);
		classes.add(SubjectMessageBodyReader.class);
		classes.add(SubjectNoteMessageBodyReader.class);
		classes.add(AttendMessageBodyReader.class);
		classes.add(MapMessageBodyWriter.class);

		return classes;
	}
}
