package server;

import jdbc.JDBCConnection;
import jdbc.JDBCConnectionFactory;

import java.sql.SQLException;

public class StartUp {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		System.out.println("Starting server verification");

		// Verify application database
		JDBCConnection appConnection = JDBCConnectionFactory.getAppDatabaseConnection();
		try {

		} finally {
			appConnection.close();
		}
	}
}
