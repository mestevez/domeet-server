package jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.sql.SQLException;

public class JDBCDatabaseStatus {

	private final static Logger logger = LoggerFactory.getLogger(JDBCDatabaseStatus.class);

	final static class RESOURCES {
		final static String appDir			= "app";

		final static String tablesortFile	= "tablesort.properties";
		final static String dataDir 		= "data";
		final static String ddlDir 			= "ddl";
	}

	public static void createApplicationDatabase(String databaseName, boolean dropIfExist) throws SQLException, ClassNotFoundException, JDBCException {
		JDBCConnection appConnection = JDBCConnectionFactory.getAppConnection();

		try {
			if (appConnection.getDatabasesList().contains(databaseName)) {
				logger.info("Database " + databaseName + " already exists");
				if (dropIfExist)
					appConnection.dropDatabase(databaseName);
				else
					throw new JDBCException("Database " + databaseName + " already exists");
			} else {
				logger.info("Database " + databaseName + " not exists");
			}

			appConnection.createDatabase(databaseName);
		} finally {
			appConnection.close();
		}

		appConnection = JDBCConnectionFactory.getAppDatabaseConnection(databaseName);
		try {
			appConnection.createSchema( "domeet");

			JDBCDatabaseStatus.class.getClassLoader().getResourceAsStream(Paths.get(RESOURCES.appDir, RESOURCES.tablesortFile).toString());
		} finally {
			appConnection.close();
		}

	}

	public static void checkApplicationStatus(JDBCConnection conn) {

	}
}
