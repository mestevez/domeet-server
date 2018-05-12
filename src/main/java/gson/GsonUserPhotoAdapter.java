package gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import util.Image;

import java.io.IOException;
import java.lang.reflect.Type;

public class GsonUserPhotoAdapter implements JsonSerializer<byte[]> {

	@Override
	public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
		if (src != null) {
			try {
				return new JsonPrimitive(Image.getImageForURL(src, 128, 128));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
