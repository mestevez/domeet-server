package conf.database;

public class StatusDatabaseProps extends DatabaseProps {

	public static DatabaseProps statusDatabase;

	StatusDatabaseProps() {
		super("localhost", 5432, "domeet_status", "org.postgresql.Driver", "domeetadmin", "12345");
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
