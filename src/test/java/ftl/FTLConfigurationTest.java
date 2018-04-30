package ftl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import freemarker.template.TemplateException;
import gson.GSONConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import rest.util.PageCommons;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("freemaker")
class FTLConfigurationTest {

	@Test
	void executeTemplate() throws IOException, TemplateException {
		String templateOutput = FTLParser.getParsedString(FTLConfiguration.getTestInstance(), null, "simple.ftlh");
		assertEquals("<html>\n" +
				"<head>\n" +
				"\t<title>Welcome!</title>\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Welcome John Doe!</h1>\n" +
				"<p>Our latest product:\n" +
				"\t<a href=\"products/greenmouse.html\">green mouse</a>!\n" +
				"</body>\n" +
				"</html>", templateOutput.replaceAll("\\r", ""));
	}

	@Test
	void executeTemplateWithModel() throws IOException, TemplateException {
		Map<String, Object> root = new HashMap<>();
		root.put("user", "Big Joe");
		Product latest = new Product();
		latest.setUrl("products/greenmouse.html");
		latest.setName("green mouse");
		root.put("latestProduct", latest);

		String templateOutput = FTLParser.getParsedString(FTLConfiguration.getTestInstance(), root, "model.ftlh");
		assertEquals("<html>\n" +
				"<head>\n" +
				"\t<title>Welcome!</title>\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Welcome Big Joe!</h1>\n" +
				"<p>Our latest product:\n" +
				"\t<a href=\"products/greenmouse.html\">green mouse</a>!\n" +
				"</body>\n" +
				"</html>", templateOutput.replaceAll("\\r", ""));
	}

	@Test
	void dataTypeFormats() throws IOException, TemplateException, ParseException {
		DateFormat formatterUTC = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone;

		Map<String, Object> root = new HashMap<>();
		root.put("vartimestamp", 	formatterUTC.parse("2001-07-04T12:08:56.235Z"));

		String templateOutput = FTLParser.getParsedString(FTLConfiguration.getTestInstance(), root, "datatypes.ftlh");
		assertEquals("<html>\n" +
				"<head>\n" +
				"\t<title>Welcome!</title>\n" +
				"</head>\n" +
				"<body>\n" +
				"\t<ul>\n" +
				"\t\t<li>Date: 2001-07-04</li>\n" +
				"\t\t<li>Timestamp: 2001-07-04T12:08:56.235Z</li>\n" +
				"\t</ul>\n" +
				"</body>\n" +
				"</html>", templateOutput.replaceAll("\\r", ""));
	}

	@Test
	void objects2JSON() throws IOException, TemplateException, ParseException {
		DateFormat formatterUTC = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone;

		Map<String, Object> appData = new HashMap<>();
		appData.put("textattr", "Hello");
		appData.put("intattr", 15);
		appData.put("decattr", 15.8);
		appData.put("dateattr", formatterUTC.parse("2001-07-04T12:08:56.235Z"));
		appData.put("nullattr", null);

		Map<String, Object> user = new HashMap<>();
		user.put("first_name", "John");
		user.put("last_name", "Smith");
		user.put("email", "john.smith@uoc.edu");
		appData.put("user", user);

		List<Object> listValues = new ArrayList<>();
		listValues.add("Element1");
		listValues.add(20);
		listValues.add(null);
		appData.put("list", listValues);

		Map<String, Object> root = new HashMap<>();
		root.put("app", GSONConfiguration.getTestInstance().getConfiguration().toJson(appData));

		String templateOutput = FTLParser.getParsedString(FTLConfiguration.getTestInstance(), root, "objects2JSON.ftlh");
		assertEquals("<html>\n" +
				"<head>\n" +
				"\t<title>Welcome!</title>\n" +
				"</head>\n" +
				"<body>\n" +
				"<script>\n" +
				"var myvar = {\n" +
				"  \"intattr\": 15,\n" +
				"  \"decattr\": 15.8,\n" +
				"  \"list\": [\n    \"Element1\",\n    20,\n    null\n  ],\n" +
				"  \"textattr\": \"Hello\",\n" +
				"  \"user\": {\n" +
				"    \"last_name\": \"Smith\",\n" +
				"    \"first_name\": \"John\",\n" +
				"    \"email\": \"john.smith@uoc.edu\"\n" +
				"  },\n" +
				"  \"dateattr\": \"2001-07-04T12:08:56.235Z\"\n" +
				"};\n" +
				"</script>\n" +
				"</body>\n" +
				"</html>", templateOutput.replaceAll("\\r", ""));
	}

	/**
	 * Product bean for testing
	 */
	public class Product {

		private String url;
		private String name;

		// As per the JavaBeans spec., this defines the "url" bean property
		// It must be public!
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		// As per the JavaBean spec., this defines the "name" bean property
		// It must be public!
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}