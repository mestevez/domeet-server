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
		final static String appDir			= "app";

		final static String tablesortFile	= "tablesort.properties";
		final static String dataDir 		= "data";
		final static String ddlDir 			= "ddl";
	}

	/**
	 * Obtains the list of tables required for the received directory, defined by tablesort.properties file
	 *
	 * @param appDir	Directory which holds the database structures and data
	 * @return a list of tables, sorted by creation order
	 * @throws IOException In case an occurs an error when accessing the table list file
	 */
	private static List<String> _getTableList(String appDir) throws IOException {
		InputStream		tableSortInputStream	= JDBCDatabaseStatus.class.getResourceAsStream(Paths.get(appDir, RESOURCES.tablesortFile).toString());
		List<String>	tableList 				= new ArrayList<>();

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
		StringBuilder	textBuilder		= new StringBuilder();
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
			} else {
				logger.info("Database " + databaseName + " not exists");
			}

			appConnection.createDatabase(databaseName);
		} finally {
			appConnection.close();
		}

		appConnection = JDBCConnectionFactory.getAppDatabaseConnection(databaseName);
		try {
			appConnection.createSchema( "domeet");

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
					appConnection.tableLoad("domeet." + tableName, loadDataFile, true);
				} else {
					InputStream loadSQL = JDBCDatabaseStatus.class.getResourceAsStream(Paths.get(RESOURCES.appDir, RESOURCES.dataDir, tableName + ".sql").toString());
					if (loadSQL != null) {
						appConnection.executeDDLStatement(_inputStreamToString(loadSQL));
					}
				}
			}
		} finally {
			appConnection.close();
		}
	}

	public static JDBCCheckStatus checkDatabaseStatus(String databaseName)
			throws SQLException, ClassNotFoundException, JDBCException, IOException, URISyntaxException
	{
		JDBCCheckStatus status = new JDBCCheckStatus();

		JDBCConnection generalConnection = JDBCConnectionFactory.getAppConnection();
		try {
			if (!generalConnection.getDatabasesList().contains(databaseName))
				status.setFatalError(String.format("Database [$s] not exists", databaseName));
		} finally {
			generalConnection.close();
		}

		if (status.getFatalError() == null) {
			createApplicationDatabase("domeet_status", true);
			JDBCConnection appConnection = JDBCConnectionFactory.getAppDatabaseConnection(databaseName);
			try {

			} finally {
				appConnection.close();
			}
		}

		return status;
	}
}
