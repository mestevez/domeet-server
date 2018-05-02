package gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import util.SerializableOrdinalEnumeration;

import java.util.Date;

public class GSONConfiguration {
	private static GSONConfiguration GSONConfigurationMainInstance;

	private GsonBuilder gsonBuilder;

	private GSONConfiguration() {
		gsonBuilder = new GsonBuilder()
			.registerTypeAdapter(Date.class, new GsonUTCDateAdapter())
			.registerTypeAdapterFactory(GsonHibernateProxyTypeAdapter.FACTORY)
			.registerTypeHierarchyAdapter(SerializableOrdinalEnumeration.class, new GsonOrdinalEnumAdapter())
			.excludeFieldsWithoutExposeAnnotation()
			.serializeNulls()
			.setPrettyPrinting();
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
}
