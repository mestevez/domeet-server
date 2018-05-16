package conf.database;

public class StatusDatabaseProps extends DatabaseProps {

	public static DatabaseProps statusDatabase;

	StatusDatabaseProps() {
		super(DatabaseProps.getCommonHost(), DatabaseProps.getCommonPort(), "domeet_status", "org.postgresql.Driver", DatabaseProps.getCommonUserName(), DatabaseProps.getCommonPassword());
	}

	/**
	 * Obtains the configuration for the main application database
	 *
	 * As constructor is private through this function make MainDatabaseProps singleton.
	 *
	 * @return
	 */
	public static final DatabaseProps getDatabaseProps() {
		if (statusDatabase == null)
			statusDatabase = new StatusDatabaseProps();

		return statusDatabase;
	}
}
