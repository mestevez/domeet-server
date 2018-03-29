package jdbc;

import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCPostgreSQLConnection extends JDBCConnection {

	private List<String> databasesList;

	/**
	 *
	 * @param host			Database server IP or host name
	 * @param port			Database server port
	 * @param databaseName	Database physical name
	 * @param encoding		Database character encoding
	 * @param user			Database connection user
	 * @param password		Connection user password
	 * @throws ClassNotFoundException	if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	JDBCPostgreSQLConnection(String host, int port, String databaseName, String encoding, String user, String password) throws ClassNotFoundException, SQLException {
		super("org.postgresql.Driver", "postgresql", host, port, databaseName, encoding, user, password);
	}

	@Override
	public void createDatabase(String databaseName) throws SQLException {
		// Set database list to null to reload it on next call
		databasesList = null;

		CallableStatement callableStatement = m_conn.prepareCall("CREATE DATABASE " + databaseName +";");
		callableStatement.execute();
	}

	@Override
	public void createSchema(String schemeName) throws SQLException {
		CallableStatement callableStatement = m_conn.prepareCall("CREATE SCHEMA IF NOT EXISTS " + schemeName +";");
		callableStatement.execute();
	}

	@Override
	public void dropDatabase(String databaseName) throws SQLException {
		// Set database list to null to reload it on next call
		databasesList = null;

		CallableStatement callableStatement = m_conn.prepareCall("DROP DATABASE " + databaseName +";");
		callableStatement.execute();
	}

	@Override
	public void executeDDLStatement(String sql) throws SQLException {
		Statement statement = m_conn.createStatement();
		try {
			statement.execute(sql);
		} finally {
			statement.close();
		}
	}

	/**
	 *
	 * @return List of database names available for the current <code>Connection</code>
	 * @throws SQLException if the database query error occurs
	 */
	@Override
	public List<String> getDatabasesList() throws SQLException {
		if (databasesList == null) {
			databasesList = new ArrayList<>();
			CallableStatement callableStatement = m_conn.prepareCall("SELECT datname FROM pg_database WHERE datistemplate = false;");
			callableStatement.execute();
			ResultSet resultSet = callableStatement.getResultSet();
			while (resultSet.next()) {
				databasesList.add(resultSet.getString(1));
			}
			resultSet.close();
		}
		return databasesList;
	}

//	/**
//	 *
//	 * @return List of database names available for the current <code>Connection</code>
//	 * @throws SQLException if the database query error occurs
//	 */
//	public List<String> getTables() throws SQLException {
//		if (databasesList == null) {
//			databasesList = new ArrayList<>();
//			CallableStatement callableStatement = m_conn.prepareCall("SELECT table_schema,table_name\n" +
//					"FROM information_schema.tables\n" +
//					"ORDER BY table_schema,table_name;;");
//			callableStatement.execute();
//			ResultSet resultSet = callableStatement.getResultSet();
//			while (resultSet.next()) {
//				databasesList.add(resultSet.getString(1));
//			}
//			resultSet.close();
//		}
//		return databasesList;
//	}

	/**
	 *
	 * @param tableName The name (optionally schema-qualified) of an existing table.
	 * @param loadFile File of the input
	 * @param header Specifies that the file contains a header line with the names of each column in the file.
	 *                  The first line is ignored. This option is allowed only when using CSV format.
	 * @throws SQLException if the occurs an error on database while executing SQL operation
	 * @throws IOException If an I/O error occurs
	 */
	@Override
	public void tableLoad(String tableName, File loadFile, boolean header) throws SQLException, IOException {
		CopyManager copyManager = new CopyManager(m_conn.unwrap(PgConnection.class));
		copyManager.copyIn("COPY " + tableName + " FROM STDIN WITH DELIMITER '|'" + (header ? " CSV HEADER" : ""), new FileReader(loadFile));
	}
}
