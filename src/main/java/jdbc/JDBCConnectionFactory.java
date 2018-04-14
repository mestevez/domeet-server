package jdbc;

import conf.database.DatabaseProps;
import conf.database.MainDatabaseProps;

import java.sql.SQLException;

public class JDBCConnectionFactory {

	private final static String APP_SCHEME			= "app";

	/**
	 * Obtains a connection for an specific database from main application database server.
	 * This connection is useful for managing database, DML and DDL operations, or maintenance operations.
	 *
	 * @return Database connection
	 * @throws ClassNotFoundException    if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	static JDBCConnection getAppDatabaseConnection(DatabaseProps databaseProps) throws SQLException, ClassNotFoundException {
		return new JDBCPostgreSQLConnection(databaseProps.getHostname(), databaseProps.getPort(), databaseProps.getDatabasename(), "UTF-8", databaseProps.getUser(), databaseProps.getUserpassword());
	}

	/**
	 * Get application scheme
	 *
	 * @return scheme name
	 */
	static String getAppScheme() {
		return APP_SCHEME;
	}

	/**
	 * Get the full table name including table scheme
	 *
	 * @param tableName	Table name
	 * @return table name
	 */
	static String getAppTableName(String tableName) {
		return APP_SCHEME + "." + tableName;
	}

	/**
	 * Obtains a connection to the main application database server, without specifying any database.
	 * This connection is useful for managing database server, such us database or user creation, etc.
	 *
	 * @return Database server connection
	 * @throws ClassNotFoundException    if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	static JDBCConnection getServerConnection() throws SQLException, ClassNotFoundException {
		DatabaseProps mainDatabaseProps = MainDatabaseProps.getDatabaseProps();
		return new JDBCPostgreSQLConnection(mainDatabaseProps.getHostname(), mainDatabaseProps.getPort(),  "UTF-8", mainDatabaseProps.getUser(), mainDatabaseProps.getUserpassword());
	}
}
