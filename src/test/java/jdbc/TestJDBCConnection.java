package jdbc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@Tag("jdbc")
class TestJDBCConnection {

	@Test
	void createApplicationDatabase() throws SQLException, ClassNotFoundException, JDBCException, IOException, URISyntaxException {
		JDBCDatabaseStatus.createApplicationDatabase("domeet_junit", true);
	}

	@Test
	void checkAppStatus() throws SQLException, ClassNotFoundException, JDBCException, IOException, URISyntaxException {
		JDBCDatabaseStatus.checkDatabaseStatus("domeet_junit");
	}

	@Test
	void initAppConnection() throws SQLException, ClassNotFoundException {
		JDBCConnectionFactory.getAppDatabaseConnection("domeet_junit").close();
	}
}
