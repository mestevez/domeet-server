package gradle;


import conf.database.MainDatabaseProps;
import jdbc.JDBCCheckStatus;
import jdbc.JDBCDatabaseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.StartUp;
import util.Path;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class Task {
	private final static Logger logger = LoggerFactory.getLogger(StartUp.class);

	public static void main(String[] args) throws Exception {
		logger.info("Executing gradle task. args: " + Arrays.toString(args));

		if (args[0].equalsIgnoreCase("database")) {
			if (args.length <= 1) {
				logger.info("Missing arguments at position 1 for task database");
			} else if (args[1].equalsIgnoreCase("create")) {
				JDBCDatabaseStatus.createApplicationDatabase(MainDatabaseProps.getDatabaseProps(), false, true);
			} else if (args[1].equalsIgnoreCase("drop")) {
				JDBCDatabaseStatus.dropApplicationDatabase(MainDatabaseProps.getDatabaseProps());
			} else if (args[1].equalsIgnoreCase("load")) {
				JDBCDatabaseStatus.loadApplicationDatabaseData(MainDatabaseProps.getDatabaseProps());
			} else if (args[1].equalsIgnoreCase("status")) {
				JDBCCheckStatus status = JDBCDatabaseStatus.checkDatabaseStatus(MainDatabaseProps.getDatabaseProps());
				if (status.getFatalError() != null)
					logger.error("STATUS FAILED: " + status.getFatalError());
				else if (status.getErrorsList().size() == 0)
					logger.info("Application database OK");
				else {
					logger.warn("Differences found (" + status.getErrorsList().size() + "):");
					for (String diff : status.getErrorsList()) {
						logger.warn(". " + diff);
					}
				}
			} else {
				logger.info("Unhandled argument [" + args[1] + "] for task database");
			}
		} else if (args[0].equalsIgnoreCase("loadconf")) {
			String configFile = args.length > 1 ? args[1] : "domeet.template.properties";
			StartUp.initConf(configFile);
		} else if (args[0].equalsIgnoreCase("start")) {
			String configFile = args.length > 1 ? args[1] : "domeet.template.properties";
			StartUp.init(configFile);
		}
	}
}
