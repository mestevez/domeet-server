package mail;

import org.junit.jupiter.api.Test;
import util.Path;

import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import java.io.IOException;

class TestMailConfiguration {

	@Test
	void sendTextEmail() throws MessagingException, IOException {
		MailConfiguration.getInstance().sendMail(new Mail(
			"mestevez85@gmail.com",
			"Test Email",
			"Test Email"
		));
	}

	@Test
	void sendHTMLEmail() throws MessagingException, IOException {
		MailConfiguration.getInstance().sendMail(new Mail(
			"mestevez85@gmail.com",
			"Test Email",
			"<h1>This is actual message embedded in HTML tags</h1>"
		).setHTML());
	}

	@Test
	void sendAttachmentEmail() throws MessagingException, IOException {

		MailConfiguration.getInstance().sendMail(new Mail(
			"mestevez85@gmail.com",
			"Test Email",
			"<h1>This is actual message embedded in HTML tags</h1>"
		).setHTML()
		.addFile(new FileDataSource(Path.getRootPath() + "/src/test/resources/mail/file.txt")));
	}
}