package ftl;

import freemarker.template.TemplateException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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