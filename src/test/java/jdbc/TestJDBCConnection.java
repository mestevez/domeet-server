package jdbc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Tag("jdbc")
class TestJDBCConnection {

	@Test
	void initAppConnection() throws SQLException, ClassNotFoundException {
		JDBCConnectionFactory.getAppDatabaseConnection().close();
	}

	@Test
	void createApplicationDatabase() throws SQLException, ClassNotFoundException, JDBCException {
		JDBCDatabaseStatus.createApplicationDatabase("junit_domeet", true);
	}

	@Test
	void checkAppStatus() throws SQLException, ClassNotFoundException {
		JDBCConnection appConnection = JDBCConnectionFactory.getAppDatabaseConnection();
		try {
			JDBCDatabaseStatus.checkApplicationStatus(appConnection);
		} finally {
			appConnection.close();
		}
	}
}
