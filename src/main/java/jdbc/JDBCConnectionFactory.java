package jdbc;

import java.sql.SQLException;

public class JDBCConnectionFactory {

	private final static String APP_HOSTNAME 		= "localhost";
	private final static String APP_DATABASENAME	= "domeet";

	private final static String APP_SCHEME			= "app";

	/**
	 * Obtains a connection to the main application database server, without specifying any database.
	 * This connection is useful for managing database server, such us database or user creation, etc.
	 *
	 * @return Database server connection
	 * @throws ClassNotFoundException    if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	public static JDBCConnection getAppConnection() throws SQLException, ClassNotFoundException {
		return new JDBCPostgreSQLConnection("localhost", 5432, "", "UTF-8", "postgres", "12345");
	}

	/**
	 * Obtains a connection to the main application database.
	 * This connection is useful for managing database, DML and DDL operations, or maintenance operations.
	 *
	 * @return Database connection
	 * @throws ClassNotFoundException    if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	public static JDBCConnection getAppDatabaseConnection() throws SQLException, ClassNotFoundException {
		return getAppDatabaseConnection(APP_DATABASENAME);
	}

	/**
	 * Obtains a connection for an specific database from main application database server.
	 * This connection is useful for managing database, DML and DDL operations, or maintenance operations.
	 *
	 * @return Database connection
	 * @throws ClassNotFoundException    if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	public static JDBCConnection getAppDatabaseConnection(String databaseName) throws SQLException, ClassNotFoundException {
		return new JDBCPostgreSQLConnection("localhost", 5432, databaseName, "UTF-8", "postgres", "12345");
	}

	/**
	 * Get application main database name
	 *
	 * @return database name
	 */
	public static String getAppDatabaseName() {
		return APP_DATABASENAME;
	}

	/**
	 * Get application scheme
	 *
	 * @return scheme name
	 */
	public static String getAppScheme() {
		return APP_SCHEME;
	}

	/**
	 * Get the full table name including table scheme
	 *
	 * @param tableName
	 * @return table name
	 */
	public static String getAppTableName(String tableName) {
		return APP_SCHEME + "." + tableName;
	}
}
