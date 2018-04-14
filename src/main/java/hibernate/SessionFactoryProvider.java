package hibernate;

import conf.database.DatabaseProps;
import conf.database.MainDatabaseProps;
import jdbc.JDBCConnection;
import jdbc.JDBCConnectionFactory;
import model.auth_user;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryProvider {

	private static SessionFactory sessionFactory;

	/**
	 * Obtain an unique instance of SessionFactory
	 *
	 * @return instance of  SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			Configuration configuration = new Configuration();

			DatabaseProps mainDatabaseProps = MainDatabaseProps.getDatabaseProps();

			configuration.setProperty("hibernate.connection.driver_class", 	mainDatabaseProps.getDriver());
			configuration.setProperty("hibernate.connection.url", 			mainDatabaseProps.getURL());
			configuration.setProperty("hibernate.connection.username", 		mainDatabaseProps.getUser());
			configuration.setProperty("hibernate.connection.password", 		mainDatabaseProps.getUserpassword());

			// JDBC connection pool (use the built-in)
			configuration.setProperty("hibernate.connection.pool_size", "1");

			// SQL dialect
			configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");

			// Echo all executed SQL to stdout
			configuration.setProperty("hibernate.show_sql", "true");

			// Drop and re-create the database schema on startup
			configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

			// Class Mapping
			configuration.addAnnotatedClass(auth_user.class);

			sessionFactory = configuration.buildSessionFactory();
		}

		return sessionFactory;
	}
}
