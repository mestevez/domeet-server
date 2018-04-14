package conf.database;

public class MainDatabaseProps extends DatabaseProps {

	public static DatabaseProps mainDatabase;

	MainDatabaseProps() {
		super("localhost", 5432, "domeet", "org.postgresql.Driver", "domeetadmin", "12345");
	}

	/**
	 * Obtains the configuration for the main application database
	 *
	 * As constructor is private through this function make MainDatabaseProps singleton.
	 *
	 * @return
	 */
	public static final DatabaseProps getDatabaseProps() {
		if (mainDatabase == null)
			mainDatabase = new MainDatabaseProps();

		return mainDatabase;
	}
}
