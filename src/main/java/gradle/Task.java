package gradle;


import conf.database.MainDatabaseProps;
import jdbc.JDBCDatabaseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.StartUp;

import java.util.Arrays;

public class Task {
	private final static Logger logger = LoggerFactory.getLogger(StartUp.class);

	public static void main(String[] args) throws Exception {
		logger.info("Executing gradle task. args: " + Arrays.toString(args));

		if (args[0].equalsIgnoreCase("database")) {
			JDBCDatabaseStatus.createApplicationDatabase(MainDatabaseProps.getDatabaseProps(), false);
		} else if (args[0].equalsIgnoreCase("start")) {
			StartUp.init();
		}
	}
}
