package ftl;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

public class FTLParser {

	/**
	 * Execute Freemaker template for the given configuration and model
	 *
	 * @param configuration	Freemaker configuration
	 * @param model			Data Model
	 * @param templatePath	Template name
	 * @return				Parsed template
	 * @throws IOException	In case templatePath is not a valid path
	 * @throws TemplateException In case template parsing emmit an error
	 */
	public static String getParsedString(FTLConfiguration configuration, Map<String, Object> model, String templatePath)
		throws IOException, TemplateException
	{
		Writer writer = new StringWriter();

		Template template = configuration.getConfiguration().getTemplate(templatePath);
		template.process(model, writer);

		return writer.toString();
	}
}
