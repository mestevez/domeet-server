package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public abstract class JDBCConnection implements AutoCloseable {

	protected Connection	m_conn;

	/**
	 *
	 * @param forNameClass	the fully qualified name of the desired class.
	 * @param engineName	Database engine name (mysql|postgres|oracle, etc.)
	 * @param host			Database server IP or host name
	 * @param port			Database server port
	 * @param databaseName	Database physical name
	 * @param encoding		Database character encoding
	 * @param user			Database connection user
	 * @param password		Connection user password
	 * @throws ClassNotFoundException	if the class for forNameClass cannot be located
	 * @throws SQLException if a database access error occurs or the url is
	 */
	JDBCConnection(String forNameClass, String engineName, String host, String port, String databaseName, String encoding, String user, String password)
		throws ClassNotFoundException, SQLException
	{
		this(
			forNameClass,
			String.format("jdbc:%s://%s:%s/%s?useUnicode=true&characterEncoding=%s&user=%s&password=%s", engineName, host, port, databaseName, encoding, user, password)
		);
	}

	/**
	 *
	 * @param forNameClass	the fully qualified name of the desired class.
	 * @param url a database url of the form
	 *  <code> jdbc:<em>subprotocol</em>:<em>subname</em></code>
	 * @throws ClassNotFoundException	if the class for forNameClass cannot be located
	 * @exception SQLException if a database access error occurs or the url is
	 * {@code null}
	 */
	private JDBCConnection(String forNameClass, String url)
		throws ClassNotFoundException, SQLException
	{
		Class.forName(forNameClass);
		m_conn = DriverManager.getConnection(url);
	}

	/**
	 * Releases this <code>Connection</code> object's database and JDBC resources
	 * immediately instead of waiting for them to be automatically released.
	 * <P>
	 * Calling the method <code>close</code> on a <code>Connection</code>
	 * object that is already closed is a no-op.
	 * <P>
	 * It is <b>strongly recommended</b> that an application explicitly
	 * commits or rolls back an active transaction prior to calling the
	 * <code>close</code> method.  If the <code>close</code> method is called
	 * and there is an active transaction, the results are implementation-defined.
	 * <P>
	 *
	 * @exception SQLException SQLException if a database access error occurs
	 */
	public void close() throws SQLException {
		m_conn.close();
	}

	/**
	 *
	 * @param databaseName Database name
	 * @throws SQLException if the occurs an error on database creation
	 */
	public abstract void createDatabase(String databaseName) throws SQLException;

	/**
	 *
	 * @param schemeName Scheme name
	 * @throws SQLException if the occurs an error on database creation
	 */
	public abstract void createSchema(String schemeName) throws SQLException;

	/**
	 *
	 * @param databaseName Database name
	 * @throws SQLException if the occurs an error on database creation
	 */
	public abstract void dropDatabase(String databaseName) throws SQLException;

	/**
	 *
	 * @return List of database names available for the current <code>Connection</code>
	 * @throws SQLException if the occurs an error on database while obtaining database names list
	 */
	public abstract List<String> getDatabasesList() throws SQLException;
}
