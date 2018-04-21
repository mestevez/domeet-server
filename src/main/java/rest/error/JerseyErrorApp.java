package rest.error;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/error")
public class JerseyErrorApp extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();
		classes.add(ErrorResource.class);
		classes.add(TestErrorResource.class);
		return classes;
	}
}
