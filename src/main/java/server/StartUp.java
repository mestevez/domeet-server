package server;

import conf.database.DatabaseProps;
import conf.database.MainDatabaseProps;
import jdbc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class StartUp {
	private final static Logger logger = LoggerFactory.getLogger(StartUp.class);

	public static void init() throws SQLException, ClassNotFoundException, URISyntaxException, IOException, JDBCException, ServerException {

		logger.info("Application server starting up");

		DatabaseProps databaseProps = MainDatabaseProps.getDatabaseProps();

		if (!JDBCDatabaseStatus.doApplicationDatabaseExists(databaseProps)) {
			JDBCDatabaseStatus.createApplicationDatabase(databaseProps, false);
			logger.info("Application database CREATED!");
		} else {
			// Verify application database
			JDBCCheckStatus status = JDBCDatabaseStatus.checkDatabaseStatus(databaseProps);
			if (status.getFatalError() != null || status.getErrorsList().size() > 0)
				throw new ServerException("Main database has structural errors. Unable to start application server.");
			else
				logger.info("Application database OK");
		}
	}
}
