package ftl;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

/**
 * A Configuration instance is the central place to store the application level settings of FreeMarker.
 * Also, it deals with the creation and caching of pre-parsed templates.
 *
 * !ATTENTION:
 * Do not needlessly re-create Configuration instances; it's expensive, among others because you lose the template cache.
 * Configuration instances meant to be application-level singletons.
 *
 */
public class FTLConfiguration {
	private static FTLConfiguration FTLConfigurationMainInstance;
	private static FTLConfiguration FTLConfigurationTestInstance;
	private static String 			FTLTemplateMainDirectory = "src/main/resources/ftl/";
//	private static String 			FTLTemplateTestDirectory = "src/test/resources/ftl/";
	private static String 			FTLTemplateTestDirectory = "D:\\workspace\\UOC\\Projecte final\\domeet.project\\src\\test\\resources\\ftl";

	private Configuration configuration;

	private FTLConfiguration(String templateDirectory) throws IOException {
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.27) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		configuration = new Configuration(Configuration.VERSION_2_3_27);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		configuration.setDirectoryForTemplateLoading(new File(templateDirectory));

		// Set the preferred charset template files are stored in:
		configuration.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		configuration.setLogTemplateExceptions(false);

		// Locate config
		configuration.setDateFormat("yyyy-MM-dd");
		configuration.setDateTimeFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		configuration.setTimeZone(TimeZone.getTimeZone("UTC"));

		// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
		configuration.setWrapUncheckedExceptions(true);
	}

	/**
	 *
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Use static getInstance to implement a singleton class.
	 *
	 * @return FTLConfiguration instance
	 */
	public static FTLConfiguration getInstance() throws IOException {
		if (FTLConfigurationMainInstance == null)
			FTLConfigurationMainInstance = new FTLConfiguration(FTLTemplateMainDirectory);
		return FTLConfigurationMainInstance;
	}

	/**
	 * Use static getInstance to implement a singleton class.
	 *
	 * @return FTLConfiguration instance
	 */
	public static FTLConfiguration getTestInstance() throws IOException {
		if (FTLConfigurationTestInstance == null)
			FTLConfigurationTestInstance = new FTLConfiguration(FTLTemplateTestDirectory);
		return FTLConfigurationTestInstance;
	}
}
