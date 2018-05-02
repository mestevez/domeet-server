package gson;

import com.google.gson.Gson;
import model.MeetingType;
import org.junit.jupiter.api.Test;

class TestGSONConfiguration {

	@Test
	void testAdapter() {

		Gson gson = GSONConfiguration.getInstance().getConfiguration();

		System.out.println(gson.toJson(MeetingType.UNDETERMINED));

	}
}