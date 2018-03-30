package jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBCDatabaseStatus {

	private final static Logger logger = LoggerFactory.getLogger(JDBCDatabaseStatus.class);

	final static class RESOURCES {
		final static String appDir = "app";

		final static String tablesortFile = "tablesort.properties";
		final static String dataDir = "data";
		final static String ddlDir = "ddl";
	}

	private static void _checkColumnsStatus(JDBCCheckStatus status, JDBCTable statusTable, JDBCTable evalTable) {

		List<JDBCColumn> statusColumnsList	= statusTable.getColumns();
		List<JDBCColumn> evalColumnsList 	= evalTable.getColumns();

		for (int ist = 0; ist < statusColumnsList.size() && status.getFatalError() == null; ist++) {
			// Remove element from list to make further iterations faster
			JDBCColumn statusColumn = statusColumnsList.remove(ist);
			ist--; // Necessary when removing elements from the list

			JDBCColumn evalColumn = null;
			for (int ievl = 0; ievl < evalColumnsList.size() && evalColumn == null; ievl++) {
				if (evalColumnsList.get(ievl).getColumnName().equals(statusColumn.getColumnName())) {
					// Remove element from list to make further iterations faster
					evalColumn = evalColumnsList.remove(ievl);
				}
			}

			if (evalColumn == null) {
				status.addErrorMessage(String.format("Missing column %s.%s.", statusTable.getTableName(), statusColumn.getColumnName()));
			} else {
				if (statusColumn.getColumnType() != evalColumn.getColumnType())
					status.addErrorMessage(String.format("Column type mismatch for column %s.%s. Expected %d, while found %d.", statusTable.getTableName(), statusColumn.getColumnName(), statusColumn.getColumnType(), evalColumn.getColumnType()));
				if (statusColumn.getColumnSize() != evalColumn.getColumnSize())
					status.addErrorMessage(String.format("Column size mismatch for column %s.%s. Expected %d, while found %d.", statusTable.getTableName(), statusColumn.getColumnName(), statusColumn.getColumnSize(), evalColumn.getColumnSize()));
				if (statusColumn.getColumnDecimalPrecision() != evalColumn.getColumnDecimalPrecision())
					status.addErrorMessage(String.format("Column scale mismatch for column %s.%s. Expected %d, while found %d.", statusTable.getTableName(), statusColumn.getColumnName(), statusColumn.getColumnDecimalPrecision(), evalColumn.getColumnDecimalPrecision()));
				if (statusColumn.isColumnNullable() != evalColumn.isColumnNullable())
					status.addErrorMessage(String.format("Column nullability mismatch for column %s.%s. Expected %s, while found %s.", statusTable.getTableName(), statusColumn.getColumnName(), statusColumn.isColumnNullable(), evalColumn.isColumnNullable()));
			}
		}

		for (int ievl = 0; ievl < evalColumnsList.size() && status.getFatalError() == null; ievl++) {
			// Remove element from list to make further iterations faster
			JDBCColumn evalColumn = evalColumnsList.remove(ievl);
			ievl--; // Necessary when removing elements from the list

			JDBCColumn statusColumn = null;
			for (int ist = 0; ist < statusColumnsList.size() && statusColumn == null; ist++) {
				if (statusColumnsList.get(ist).getColumnName().equals(evalColumn.getColumnName())) {
					// Remove element from list to make further iterations faster
					statusColumn = statusColumnsList.remove(ist);
				}
			}

			if (statusColumn == null) {
				status.addWarningMessage(String.format("Column %s.%s is not defined in dictionary.", evalTable.getTableName(), evalColumn.getColumnName()));
			}
		}
	}

	private static void _checkTablesStatus(JDBCCheckStatus status, String databaseName)
			throws SQLException, ClassNotFoundException {
		JDBCConnection statusConnection = JDBCConnectionFactory.getAppDatabaseConnection("domeet_status");
		List<JDBCTable> statusTableList;
		try {
			statusTableList = statusConnection.getTableList(JDBCConnectionFactory.getAppScheme());
		} finally {
			statusConnection.close();
		}

		JDBCConnection appConnection = JDBCConnectionFactory.getAppDatabaseConnection(databaseName);
		List<JDBCTable> evalTableList;
		try {
			evalTableList = appConnection.getTableList(JDBCConnectionFactory.getAppScheme());
		} finally {
			appConnection.close();
		}

		for (int ist = 0; ist < statusTableList.size() && status.getFatalError() == null; ist++) {
			// Remove element from list to make further iterations faster
			JDBCTable statusTable = statusTableList.remove(ist);
			ist--; // Necessary when removing elements from the list

			JDBCTable evalTable = null;
			for (int ievl = 0; ievl < evalTableList.size() && evalTable == null; ievl++) {
				if (evalTableList.get(ievl).getTableName().equals(statusTable.getTableName())) {
					// Remove element from list to make further iterations faster
					evalTable = evalTableList.remove(ievl);
				}
			}

			if (evalTable == null) {
				status.addErrorMessage(String.format("Missing table %s.", statusTable.getTableName()));
			} else {
				_checkColumnsStatus(status, statusTable, evalTable);
			}
		}

		for (int ievl = 0; ievl < evalTableList.size() && status.getFatalError() == null; ievl++) {
			// Remove element from list to make further iterations faster
			JDBCTable evalTable = evalTableList.remove(ievl);
			ievl--; // Necessary when removing elements from the list

			JDBCTable statusTable = null;
			for (int ist = 0; ist < statusTableList.size() && statusTable == null; ist++) {
				if (statusTableList.get(ist).getTableName().equals(evalTable.getTableName())) {
					// Remove element from list to make further iterations faster
					statusTable = statusTableList.remove(ist);
				}
			}

			if (statusTable == null) {
				status.addWarningMessage(String.format("Table %s is not defined in dictionary.", evalTable.getTableName()));
			}
		}
	}

	/**
	 * Obtains the list of tables required for the received directory, defined by tablesort.properties file
	 *
	 * @param appDir Directory which holds the database structures and data
	 * @return a list of tables, sorted by creation order
	 * @throws IOException In case an occurs an error when accessing the table list file
	 */
	private static List<String> _getTableList(String appDir) throws IOException, JDBCException {
		InputStream tableSortInputStream = JDBCDatabaseStatus.class.getResourceAsStream(Paths.get(appDir, RESOURCES.tablesortFile).toString());

		if (tableSortInputStream == null)
			throw new JDBCException(String.format("Unable to find file containing tables list at %s", Paths.get(appDir, RESOURCES.tablesortFile).toString()));

		List<String> tableList = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader
				(tableSortInputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
			String line;
			while ((line = reader.readLine()) != null) {
				tableList.add(line);
			}
		}

		tableSortInputStream.close();

		return tableList;
	}

	/**
	 * Reads and input stream
	 *
	 * @return input stream content
	 * @throws IOException If an I/O error occurs
	 */
	private static String _inputStreamToString(InputStream inputStream) throws IOException {
		StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(new InputStreamReader
				(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
			int c;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}
		}

		inputStream.close();

		return textBuilder.toString();
	}

	public static void createApplicationDatabase(String databaseName, boolean dropIfExist)
			throws SQLException, ClassNotFoundException, JDBCException, IOException, URISyntaxException {

		JDBCConnection appConnection = JDBCConnectionFactory.getAppConnection();
		try {
			if (appConnection.getDatabasesList().contains(databaseName)) {
				logger.info("Database " + databaseName + " already exists");
				if (dropIfExist)
					appConnection.dropDatabase(databaseName);
				else
					throw new JDBCException("Database " + databaseName + " already exists");
			} else if (dropIfExist) {
				logger.info("Database " + databaseName + " not exists");
			}

			appConnection.createDatabase(databaseName);
		} finally {
			appConnection.close();
		}

		appConnection = JDBCConnectionFactory.getAppDatabaseConnection(databaseName);
		try {
			appConnection.createSchema(JDBCConnectionFactory.getAppScheme());
			for (String tableName : _getTableList(RESOURCES.appDir)) {
				// Execute the SQL statements necessary for creating the received table.
				appConnection.executeDDLStatement(
						_inputStreamToString(
								JDBCDatabaseStatus.class.getResourceAsStream(
										Paths.get(RESOURCES.appDir, RESOURCES.ddlDir, tableName + ".sql").toString(
										)
								)
						)
				);

				// Execute initially table load
				URL loadData = JDBCDatabaseStatus.class.getResource(Paths.get(RESOURCES.appDir, RESOURCES.dataDir, tableName + ".dat").toString());
				File loadDataFile = loadData != null ? new File(loadData.toURI()) : null;
				if (loadDataFile != null && loadDataFile.exists()) {
					appConnection.tableLoad(JDBCConnectionFactory.getAppTableName(tableName), loadDataFile, true);
				} else {
					InputStream loadSQL = JDBCDatabaseStatus.class.getResourceAsStream(Paths.get(RESOURCES.appDir, RESOURCES.dataDir, tableName + ".sql").toString());
					if (loadSQL != null) {
						appConnection.executeDDLStatement(_inputStreamToString(loadSQL));
					}
				}
			}
		} catch (Exception ex){
			appConnection.close();

			// If an error occur during database creation revert its creation
			JDBCConnection revertConnection = JDBCConnectionFactory.getAppConnection();
			try {
				revertConnection.dropDatabase(databaseName);
			} finally {
				revertConnection.close();
			}

			throw ex;
		}

		appConnection.close();
	}

	public static JDBCCheckStatus checkDatabaseStatus(String databaseName)
			throws SQLException, ClassNotFoundException, JDBCException, IOException, URISyntaxException {
		JDBCCheckStatus status = new JDBCCheckStatus();

		JDBCConnection generalConnection = JDBCConnectionFactory.getAppConnection();
		try {
			if (!generalConnection.getDatabasesList().contains(databaseName))
				status.setFatalError(String.format("Database [%s] not exists", databaseName));
		} finally {
			generalConnection.close();
		}

		if (status.getFatalError() == null) {
			createApplicationDatabase("domeet_status", true);
			_checkTablesStatus(status, databaseName);
		}

		return status;
	}
}
