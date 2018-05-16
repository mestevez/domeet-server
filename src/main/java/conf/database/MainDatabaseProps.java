package conf.database;

public class MainDatabaseProps extends DatabaseProps {

	public static DatabaseProps mainDatabase;

	MainDatabaseProps() {
		super(DatabaseProps.getCommonHost(), DatabaseProps.getCommonPort(), DatabaseProps.getCommonDatabase(), "org.postgresql.Driver", DatabaseProps.getCommonUserName(), DatabaseProps.getCommonPassword());
	}

	/**
	 * Obtains the configuration for the main application database
	 *
	 * As constructor is private through this function make MainDatabaseProps singleton.
	 *
	 * @return
	 */
	public static final DatabaseProps getDatabaseProps() {
		if (mainDatabase == null) {
			mainDatabase = new MainDatabaseProps();
		}

		return mainDatabase;
	}
}
