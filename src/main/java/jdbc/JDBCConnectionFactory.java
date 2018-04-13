package jdbc;

import java.sql.SQLException;

public class JDBCConnectionFactory {

	private final static String APP_HOSTNAME 		= "localhost";
	private final static int 	APP_PORT 			= 5432;
	private final static String APP_DATABASENAME	= "domeet";
	private final static String APP_DRIVER			= "org.postgresql.Driver";
	private final static String APP_USER			= "domeetadmin";
	private final static String APP_USERPASS		= "12345";

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
		return new JDBCPostgreSQLConnection(APP_HOSTNAME, APP_PORT, "", "UTF-8", APP_USER, APP_USERPASS);
	}

	/**
	 * Get application main database JDBC connection User password
	 *
	 * @return database name
	 */
	public static String getAppConnectionPassword() {
		return APP_USERPASS;
	}

	/**
	 * Get application main database JDBC connection User
	 *
	 * @return database name
	 */
	public static String getAppConnectionUser() {
		return APP_USER;
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
		return new JDBCPostgreSQLConnection(APP_HOSTNAME, APP_PORT, databaseName, "UTF-8", APP_USER, APP_USERPASS);
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
	 * Get application main database JDBC Driver
	 *
	 * @return database name
	 */
	public static String getAppDriver() {
		return APP_DRIVER;
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

	/**
	 * Get application main database JDBC connection URL
	 *
	 * @return database name
	 */
	public static String getAppURL() {
		return String.format("jdbc:postgresql://%s:%d/%s", APP_HOSTNAME, APP_PORT, APP_DATABASENAME);
	}
}
