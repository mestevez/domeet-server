package jdbc;

import java.sql.SQLException;

public class JDBCConnectionFactory {

	public static JDBCConnection getAppConnection() throws SQLException, ClassNotFoundException {
		return new JDBCPostgreSQLConnection("localhost", "5432", "", "UTF-8", "postgres", "12345");
	}

	public static JDBCConnection getAppDatabaseConnection() throws SQLException, ClassNotFoundException {
		return getAppDatabaseConnection("domeet");
	}

	public static JDBCConnection getAppDatabaseConnection(String databaseName) throws SQLException, ClassNotFoundException {
		return new JDBCPostgreSQLConnection("localhost", "5432", databaseName, "UTF-8", "postgres", "12345");
	}
}
