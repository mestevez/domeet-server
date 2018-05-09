package fop;

import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import model.meeting;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class FOPMeeting {

	private meeting meet;
	private Locale locale;
	private DateFormat dateFormat;
	private DateFormat timeFormat;

	public FOPMeeting(meeting meet, Locale locale) {
		this.meet = meet;
		this.locale = locale;
		this.dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		this.timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
	}

	public String getMeetTitle() {
		return this.meet.getMeetTitle();
	}

	public String getMeetDate() {
		return dateFormat.format(meet.getMeetDates().iterator().next().getMeetDate());
	}

	public String getMeetTimeStart() {
		return timeFormat.format(meet.getMeetTimeStart());
	}

	public String getMeetTimeEnd() {
		return timeFormat.format(meet.getMeetTimeEnd());
	}

	public StreamingOutput getMinutesOfTheMeeting() throws IOException, TemplateException, SAXException, TransformerException {
		ResourceBundle bundle = ResourceBundle.getBundle("i18n/minutes", locale);

		Map<String, Object> data =  new HashMap<>();
		data.put("meet", this);
		data.put("i18n", bundle);

		String templateOutput = FTLParser.getParsedStringFromFile(FTLConfiguration.getInstance(), data, "minute.ftl");

		FOPProducer fopProducer = new FOPProducer(
			FOPConfiguration.getInstance(),
			templateOutput
		);

		fopProducer.transform();

		byte[] filedata = FileUtils.readFileToByteArray(fopProducer.getPDF());

		fopProducer.clean();

		return output -> {
			output.write(filedata);
			output.flush();
		};
	}
}
