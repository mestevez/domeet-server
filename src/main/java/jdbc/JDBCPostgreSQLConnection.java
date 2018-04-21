package jdbc;

import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCPostgreSQLConnection extends JDBCConnection {

	private List<String> 	databasesList;
	private List<JDBCTable> tablesList;
	private List<String> 	sequenceList;

	private static Map<String, Integer> typeConvertion;
	static {
		typeConvertion = new HashMap<>();
		typeConvertion.put("bigint", JDBCDataType.TYPE_INTEGER);
		typeConvertion.put("bigserial", JDBCDataType.TYPE_INTEGER);
		typeConvertion.put("boolean", JDBCDataType.TYPE_BOOLEAN);
		typeConvertion.put("bytea", JDBCDataType.TYPE_BLOB);
		typeConvertion.put("character", JDBCDataType.TYPE_CHAR);
		typeConvertion.put("character varying", JDBCDataType.TYPE_VARCHAR);
		typeConvertion.put("date", JDBCDataType.TYPE_DATE);
		typeConvertion.put("double precision", JDBCDataType.TYPE_DOUBLE_PRECISION);
		typeConvertion.put("integer", JDBCDataType.TYPE_INTEGER);
		typeConvertion.put("numeric ", JDBCDataType.TYPE_NUMERIC);
		typeConvertion.put("real", JDBCDataType.TYPE_REAL);
		typeConvertion.put("smallint", JDBCDataType.TYPE_SMALLINT);
		typeConvertion.put("serial", JDBCDataType.TYPE_INTEGER);
		typeConvertion.put("text", JDBCDataType.TYPE_VARCHAR);
		typeConvertion.put("timestamp", JDBCDataType.TYPE_TIMESTAMP);
		typeConvertion.put("uuid", JDBCDataType.TYPE_UROWID);
	}

	/**
	 *
	 * Obtain database connection
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

	/**
	 *
	 * Obtain server connection, without specifying database
	 *
	 * @param host			Database server IP or host name
	 * @param port			Database server port
	 * @param encoding		Database character encoding
	 * @param user			Database connection user
	 * @param password		Connection user password
	 * @throws ClassNotFoundException	if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	JDBCPostgreSQLConnection(String host, int port, String encoding, String user, String password) throws ClassNotFoundException, SQLException {
		super("org.postgresql.Driver", "postgresql", host, port, "postgres", encoding, user, password);
	}

	@Override
	public void createDatabase(String databaseName) throws SQLException {
		// Set database list to null to reload it on next call
		databasesList = null;

		CallableStatement callableStatement = m_conn.prepareCall("CREATE DATABASE " + databaseName +";");
		try {
			callableStatement.execute();
		} finally {
			callableStatement.close();
		}
	}

	@Override
	public void createSchema(String schemeName) throws SQLException {
		CallableStatement callableStatement = m_conn.prepareCall("CREATE SCHEMA IF NOT EXISTS " + schemeName +";");
		try {
			callableStatement.execute();
		} finally {
			callableStatement.close();
		}
	}

	@Override
	public void dropDatabase(String databaseName) throws SQLException {
		// Set database list to null to reload it on next call
		databasesList = null;

		CallableStatement callableStatement = m_conn.prepareCall("DROP DATABASE " + databaseName +";");
		try {
			callableStatement.execute();
		} finally {
			callableStatement.close();
		}
	}

	@Override
	public void executeDDLStatement(String sql) throws SQLException {
		Statement statement = m_conn.createStatement();
		try {
			statement.execute(sql);

			// Due to DDL operations generally modify database structure, after this operations
			// structure caches must be empty in order to refresh it when they've been solicited again
			tablesList = null;
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
			try {
				callableStatement.execute();
				ResultSet resultSet = callableStatement.getResultSet();
				while (resultSet.next()) {
					databasesList.add(resultSet.getString(1));
				}
				resultSet.close();
			} finally {
				callableStatement.close();
			}
		}
		return databasesList;
	}

	/**
	 * Obtains the sequences for the current database.
	 *
	 * @return List of sequences available for the current <code>Connection</code>
	 * @throws SQLException if the database query error occurs
	 */
	@Override
	public String[] getSequenceList() throws SQLException {
		if (sequenceList == null) {
			CallableStatement sequenceStatement = m_conn.prepareCall(
				"SELECT relname FROM pg_class WHERE relkind = 'S';");

			try {
				if (sequenceStatement.execute()) {
					ResultSet rs = sequenceStatement.getResultSet();
					sequenceList = new ArrayList<>();
					while (rs.next()) {
						sequenceList.add(rs.getString(1));
					}
					rs.close();
				}
			} finally {
				sequenceStatement.close();
			}
		}
		return sequenceList.toArray(new String[sequenceList.size()]);
	}

	/**
	 * Obtains the tables for the current database.
	 *
	 * @return List of tables available for the current <code>Connection</code>
	 * @throws SQLException if the database query error occurs
	 */
	@Override
	public JDBCTable[] getTableList() throws SQLException {
		if (tablesList == null) {

			CallableStatement tablesStatement = m_conn.prepareCall(
					"SELECT table_schema,table_name\n" +
					"FROM information_schema.tables\n" +
					"ORDER BY table_schema,table_name;");

			PreparedStatement columnsStatement = m_conn.prepareStatement(
					"SELECT column_name, data_type, is_nullable, character_maximum_length, numeric_precision, numeric_scale\n" +
					" FROM information_schema.columns\n" +
					"WHERE table_schema = ?\n" +
					"  AND table_name = ?\n" +
					"ORDER BY ordinal_position;");

			try {
				if (tablesStatement.execute()) {
					ResultSet tablesResultSet = tablesStatement.getResultSet();

					tablesList = new ArrayList<>();
					while (tablesResultSet.next()) {
						JDBCTable jdbcTable = new JDBCTable(tablesResultSet.getString(1), tablesResultSet.getString(2));

						columnsStatement.setString(1, jdbcTable.getTableScheme());
						columnsStatement.setString(2, jdbcTable.getTableName());
						if (columnsStatement.execute()) {
							ResultSet columnsResultSet = columnsStatement.getResultSet();
							while (columnsResultSet.next()) {
								int sqlType =
										typeConvertion.containsKey(columnsResultSet.getString(2)) ?
										typeConvertion.get(columnsResultSet.getString(2)) :
										JDBCDataType.TYPE_UNKNOWN;

								jdbcTable.addColumn(new JDBCColumn(
									columnsResultSet.getString(1),
										sqlType,
										columnsResultSet.getString(3).equalsIgnoreCase("YES"),
										columnsResultSet.getInt(JDBCDataType.isNumeric(sqlType) ? 5: 4),
										columnsResultSet.getInt(6)

								));
							}
							columnsResultSet.close();
						}

						tablesList.add(jdbcTable);
					}
					tablesResultSet.close();
				}
			} finally {
				columnsStatement.close();
				tablesStatement.close();
			}
		}
		return tablesList.toArray(new JDBCTable[tablesList.size()]);
	}

	/**
	 * Obtains the tables for and specific scheme in the current database.
	 *
	 * @param scheme Table scheme
	 * @return List of tables available for the current <code>Connection</code>
	 * @throws SQLException if the database query error occurs
	 */
	@Override
	public List<JDBCTable> getTableList(String scheme) throws SQLException {
		List<JDBCTable> tablesInScheme = new ArrayList<>();
		for (JDBCTable jdbcTable : getTableList()) {
			if (jdbcTable.getTableScheme().equalsIgnoreCase(scheme))
				tablesInScheme.add(jdbcTable);
		}
		return tablesInScheme;
	}

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
