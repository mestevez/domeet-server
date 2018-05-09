package mail;

import javax.activation.DataSource;
import java.util.ArrayList;
import java.util.List;

public class Mail {
	private List<String> to;
	private String from;
	private String subject;
	private String text;
	private boolean isHTML;
	private List<DataSource> files;

	public Mail (List<String> p_to, String p_from, String p_subject, String p_text) {
		to = p_to;
		from = p_from;
		subject = p_subject;
		text = p_text;
		files = new ArrayList<>();
	}

	public Mail (String p_to, String p_from, String p_subject, String p_text) {
		this(new ArrayList<>(), p_from, p_subject, p_text);
		to.add(p_to);
	}

	public Mail (String p_to, String p_subject, String p_text) {
		this(p_to, null, p_subject, p_text);
	}

	public Mail addFile(DataSource file) {
		files.add(file);
		return this;
	}

	public List<DataSource> getFiles() {
		return files;
	}

	public String getFrom() {
		return from;
	}

	public List<String> getTo() {
		return to;
	}
	public String getSubject() {
		return subject;
	}
	public String getText() {
		return text;
	}

	public boolean hasFiles() {
		return files.size() > 0;
	}

	public boolean isHTML() {
		return isHTML;
	}

	public Mail setHTML() {
		isHTML = true;
		return this;
	}
}
