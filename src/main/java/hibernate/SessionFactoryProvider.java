package hibernate;

import conf.database.DatabaseProps;
import model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;
import java.util.Map;

public class SessionFactoryProvider {

	private static Map<String, SessionFactory> sessionFactories = new HashMap<>();

	/**
	 * Obtain an unique instance of SessionFactory per database configuration
	 *
	 * @return instance of  SessionFactory
	 */
	public static SessionFactory getSessionFactory(DatabaseProps databaseProps) {
		SessionFactory sessionFactory = sessionFactories.get(databaseProps.getURL());
		if (sessionFactory == null) {
			Configuration configuration = new Configuration();

			configuration.setProperty("hibernate.connection.driver_class", 	databaseProps.getDriver());
			configuration.setProperty("hibernate.connection.url", 			databaseProps.getURL());
			configuration.setProperty("hibernate.connection.username", 		databaseProps.getUser());
			configuration.setProperty("hibernate.connection.password", 		databaseProps.getUserpassword());

			// JDBC connection pool (use the built-in)
			configuration.setProperty("hibernate.connection.pool_size", "1");

			// SQL dialect
			configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");

			// Echo all executed SQL to stdout
			configuration.setProperty("hibernate.show_sql", "true");

			// Drop and re-create the database schema on startup
			configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

			// Lucene Text Search
			configuration.setProperty("hibernate.search.default.directory_provider", "filesystem");
			configuration.setProperty("hibernate.search.default.indexBase", "build/tmp/lucene/indexes");

			// Class Mapping
			configuration.addAnnotatedClass(auth_user.class);
			configuration.addAnnotatedClass(auth_role.class);
			configuration.addAnnotatedClass(user.class);
			configuration.addAnnotatedClass(schedule.class);
			configuration.addAnnotatedClass(schedule_time.class);

			sessionFactory = configuration.buildSessionFactory();

			sessionFactories.put(databaseProps.getURL(), sessionFactory);
		}

		return sessionFactory;
	}
}
