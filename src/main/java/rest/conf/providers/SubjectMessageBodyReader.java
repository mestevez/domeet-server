package rest.conf.providers;

import com.google.gson.reflect.TypeToken;
import conf.database.MainDatabaseProps;
import gson.GSONConfiguration;
import hibernate.SessionFactoryProvider;
import model.subject;
import org.hibernate.Session;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class SubjectMessageBodyReader implements MessageBodyReader<subject> {
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(subject.class);
	}

	@Override
	public subject readFrom(Class<subject> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws WebApplicationException {
		Session session = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			Type gsonType = new TypeToken<Map<String, Object>>(){}.getType();
			return subject.fromMap(
					session,
					GSONConfiguration.getInstance().getConfiguration()
							.fromJson(new InputStreamReader(entityStream, "UTF-8"), gsonType)
			);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return null;
	}
}
