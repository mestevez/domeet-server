package rest.conf.providers;

import gson.GSONConfiguration;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class MapMessageBodyWriter implements MessageBodyWriter<Map<String, Object>> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(HashMap.class);
	}

	@Override
	public long getSize(Map<String, Object> stringObjectMap, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return stringObjectMap.size();
	}

	@Override
	public void writeTo(Map<String, Object> stringObjectMap, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		Writer writer = new OutputStreamWriter(entityStream, "UTF-8");
		writer.write(GSONConfiguration.getInstance().getConfiguration().toJson(stringObjectMap));
		writer.flush();
		writer.close();
	}
}
