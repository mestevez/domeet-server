package mail;

import util.Path;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class MailConfiguration {

	private static MailConfiguration MAILCONFIG;

	private Session session;
	private String user;

	public MailConfiguration() throws IOException {
		// Get system properties
		Properties properties = System.getProperties();

		Properties confProperties = new Properties();
		confProperties.loadFromXML(new FileInputStream(Path.getConfPath("mail-config.xml")));

		String password = null;
		Enumeration<Object> keys = confProperties.keys();
		while (keys.hasMoreElements()) {
			String prop = keys.nextElement().toString();
			if (prop.equalsIgnoreCase("user"))
				user = confProperties.get(prop).toString();
			else if (prop.equalsIgnoreCase("password"))
				password = confProperties.get(prop).toString();
			else
				properties.setProperty(prop, confProperties.get(prop).toString());
		}

		if (user == null)
			throw new IOException("Undefined user property on " + Path.getConfPath("mail-config.xml"));
		if (password == null)
			throw new IOException("Undefined password property on " + Path.getConfPath("mail-config.xml"));

		// Get the default Session object.
		String finalUser = user;
		String finalPassword = password;
		session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(finalUser, finalPassword);
			}
		});
	}

	public void sendMail(Mail mail) throws MessagingException {
		// Create a default MimeMessage object.
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		if (mail.getFrom() != null)
			message.setFrom(new InternetAddress(mail.getFrom()));
		else
			message.setFrom(new InternetAddress(user));

		// Set To: header field of the header.
		InternetAddress[] internetAddresses = new InternetAddress[ mail.getTo().size() ];
		List<String> to1 = mail.getTo();
		for (int i = 0; i < to1.size(); i++) {
			internetAddresses[i] = new InternetAddress(to1.get(i));
		}
		message.setRecipients(Message.RecipientType.TO, internetAddresses);

		// Set Subject: header field
		message.setSubject(mail.getSubject());

		if (mail.hasFiles()) {
			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
			if (!mail.isHTML()) {
				messageBodyPart.setText(mail.getText());
			} else {
				messageBodyPart.setContent(mail.getText(), "text/html");
			}

			// Create a multipart message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			for (DataSource source : mail.getFiles()) {
				messageBodyPart = new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(source.getName());
				multipart.addBodyPart(messageBodyPart);
			}

			// Send the complete message parts
			message.setContent(multipart);
		} else if (!mail.isHTML()) {
			message.setText(mail.getText());
		} else {
			message.setContent(mail.getText(), "text/html");
		}

		// Send message
		Transport.send(message);
	}

	public static MailConfiguration getInstance() throws IOException {
		if (MAILCONFIG == null)
			MAILCONFIG = new MailConfiguration();
		return MAILCONFIG;
	}

}
