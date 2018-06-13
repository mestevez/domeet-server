package server;

import freemarker.template.TemplateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Path;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class StartUpTest {

	@Test
	void initConf() throws IOException, TemplateException {
		InputStream confPropertiesInput = getClass().getResourceAsStream("domeet.properties");
		Properties confProperties = new Properties();
		confProperties.load(confPropertiesInput);
		confPropertiesInput.close();

		StartUp.initConf(confProperties);

		InputStream jettyRealmPropertiesInput = new FileInputStream(Path.getConfPath("security/jetty-realm-dbms.properties"));
		Properties jettyRealmProperties = new Properties();
		jettyRealmProperties.load(jettyRealmPropertiesInput);
		jettyRealmPropertiesInput.close();

		Assertions.assertEquals("jdbc:postgresql://localhost:5432/domeet", jettyRealmProperties.getProperty("url"));
		Assertions.assertEquals("postgres", jettyRealmProperties.getProperty("username"));
		Assertions.assertEquals("12345", jettyRealmProperties.getProperty("password"));
	}
}