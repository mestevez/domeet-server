package conf.database;

public class JUnitDatabaseProps extends DatabaseProps {

	public static DatabaseProps junitDatabase;

	JUnitDatabaseProps() {
		super("localhost", 5432, "domeet_junit", "org.postgresql.Driver", "domeetadmin", "12345");
	}

	/**
	 * Obtains the configuration for the main application database
	 *
	 * As constructor is private through this function make MainDatabaseProps singleton.
	 *
	 * @return
	 */
	public static final DatabaseProps getDatabaseProps() {
		if (junitDatabase == null)
			junitDatabase = new JUnitDatabaseProps();

		return junitDatabase;
	}
}
