package rest.conf.providers;

import gson.GSONConfiguration;
import model.meeting;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class MeetingMessageBodyWriter implements MessageBodyWriter<meeting> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(meeting.class);
	}

	@Override
	public long getSize(meeting source, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(meeting source, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		Writer writer = new OutputStreamWriter(entityStream, "UTF-8");
		writer.write(GSONConfiguration.getInstance().getConfiguration().toJson(source));
		writer.flush();
		writer.close();


	}
}
