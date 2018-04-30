package gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class GSONConfiguration {
	private static GSONConfiguration GSONConfigurationMainInstance;
	private static GSONConfiguration GSONConfigurationTestInstance;

	private GsonBuilder gsonBuilder;

	private GSONConfiguration() {
		gsonBuilder = new GsonBuilder();

		// Locate config
		gsonBuilder.registerTypeAdapter(Date.class, new GsonUTCDateAdapter());

		gsonBuilder.setPrettyPrinting();
	}

	/**
	 *
	 * @return
	 */
	public Gson getConfiguration() {
		return gsonBuilder.create();
	}

	/**
	 * Use static getInstance to implement a singleton class.
	 *
	 * @return GSONConfiguration instance
	 */
	public static GSONConfiguration getInstance() {
		if (GSONConfigurationMainInstance == null)
			GSONConfigurationMainInstance = new GSONConfiguration();
		return GSONConfigurationMainInstance;
	}

	/**
	 * Use static getInstance to implement a singleton class.
	 *
	 * @return GSONConfiguration instance
	 */
	public static GSONConfiguration getTestInstance() {
		if (GSONConfigurationTestInstance == null) {
			GSONConfigurationTestInstance = new GSONConfiguration();
		}
		return GSONConfigurationTestInstance;
	}
}
