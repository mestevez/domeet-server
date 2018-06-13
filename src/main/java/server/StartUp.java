package server;

import conf.database.DatabaseProps;
import conf.database.MainDatabaseProps;
import freemarker.template.TemplateException;
import ftl.FTLConfiguration;
import ftl.FTLParser;
import jdbc.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Path;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StartUp {
	private final static Logger logger = LoggerFactory.getLogger(StartUp.class);

	public static void initConf(Properties confProperties) throws IOException, TemplateException {
		Map<String, Object> mapProperties = new HashMap<>();
		confProperties.keySet().forEach((key) -> mapProperties.put(key.toString(), confProperties.getProperty(key.toString())));

		String [][] configFiles = {
			{ Path.getConfPath("security/jetty-realm-dbms.template.properties"), Path.getConfPath("security/jetty-realm-dbms.properties") },
			{ Path.getConfPath("mail-config.template.xml"), Path.getConfPath("mail-config.xml") },
			{ Path.getConfPath("jetty/jetty.template.xml"), Path.getConfPath("jetty/jetty.xml") }
		};

		for (String[] configFile : configFiles) {
			String templateFile = configFile[0];
			String endFile 		= configFile[1];

			InputStream jettyRealmPropertiesTemplateInput = new FileInputStream(templateFile);
			String parsedStringFromFile = FTLParser.getParsedStringFromString(FTLConfiguration.getInstance(), mapProperties, IOUtils.toString(jettyRealmPropertiesTemplateInput));
			jettyRealmPropertiesTemplateInput.close();

			File jettyRealmDbmsPropertiesFile = new File(endFile);
			if (!jettyRealmDbmsPropertiesFile.exists())
				jettyRealmDbmsPropertiesFile.createNewFile();

			FileOutputStream jettyRealmDbmsOutputStream = new FileOutputStream(jettyRealmDbmsPropertiesFile);
			jettyRealmDbmsOutputStream.write(parsedStringFromFile.getBytes());
			jettyRealmDbmsOutputStream.close();
		}
	}

	public static void init(Properties confProperties) throws SQLException, ClassNotFoundException, URISyntaxException, IOException, JDBCException, ServerException, TemplateException {

		logger.info("Application server starting up");

		logger.info("Updating config files");

		StartUp.initConf(confProperties);

		logger.info("Config files UP TO DATE");

		DatabaseProps databaseProps = MainDatabaseProps.getDatabaseProps();

		if (!JDBCDatabaseStatus.doApplicationDatabaseExists(databaseProps)) {
			JDBCDatabaseStatus.createApplicationDatabase(databaseProps, false, true);
			logger.info("Application database CREATED!");
		} else {
			if (!JDBCDatabaseStatus.doApplicationSchemeExists(databaseProps))
				JDBCDatabaseStatus.createApplicationSchema(databaseProps);

			// Verify application database
			JDBCCheckStatus status = JDBCDatabaseStatus.checkDatabaseStatus(databaseProps);
			if (status.getFatalError() != null || status.getErrorsList().size() > 0)
				throw new ServerException("Main database has structural errors. Unable to start application server.");
			else
				logger.info("Application database OK");
		}
	}
}
