package jdbc;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("jdbc")
class TestJDBCConnection {

	@Test
	void createApplicationDatabase() throws SQLException, ClassNotFoundException, JDBCException, IOException, URISyntaxException {
		JDBCDatabaseStatus.createApplicationDatabase("domeet_junit", true);
	}

	@Test
	void checkAppStatus() throws SQLException, ClassNotFoundException, JDBCException, IOException, URISyntaxException {
		JDBCCheckStatus status = JDBCDatabaseStatus.checkDatabaseStatus("domeet_junit");

		assertNull(status.getFatalError(), "FATAL ERROR");
		assertEquals(0, status.getErrorsList().size(), "UNEXPECTED ERRORS");
		assertEquals(0, status.getInfoList().size(), "UNEXPECTED INFO");
		assertEquals(0, status.getWarningsList().size(), "UNEXPECTED WARNINGS");
	}

	@Test
	void initAppConnection() throws SQLException, ClassNotFoundException {
		JDBCConnectionFactory.getAppDatabaseConnection("domeet_junit").close();
	}
}