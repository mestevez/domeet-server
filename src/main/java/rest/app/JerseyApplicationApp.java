package rest.app;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

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

		// Enable multipart
		classes.add(MultiPartFeature.class);

		return classes;
	}
}
