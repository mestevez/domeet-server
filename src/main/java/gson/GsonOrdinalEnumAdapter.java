package gson;

import com.google.gson.*;
import util.SerializableOrdinalEnumeration;

import java.lang.reflect.Type;

public class GsonOrdinalEnumAdapter implements JsonSerializer<SerializableOrdinalEnumeration> {

	@Override
	public synchronized JsonElement serialize(SerializableOrdinalEnumeration e, Type type, JsonSerializationContext jsonSerializationContext) {
		return new JsonPrimitive(e.getKey());
	}
}
