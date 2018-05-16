package conf.database;

import util.Path;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DatabaseProps {
	private static Properties databaseConfProperties;
	private String	hostname;
	private int 	port;
	private String	databasename;
	private String	driver;
	private String	user;
	private String 	userpassword;

	DatabaseProps(String p_hostname, int p_port, String p_databasename, String p_driver, String p_user, String p_userpassword) {
		hostname		= p_hostname;
		port			= p_port;
		databasename	= p_databasename;
		driver			= p_driver;
		user			= p_user;
		userpassword	= p_userpassword;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getDatabasename() {
		return databasename;
	}

	public String getDriver() {
		return driver;
	}

	public String getURL() {
		return String.format("jdbc:postgresql://%s:%d/%s", hostname, port, databasename);
	}

	public String getUser() {
		return user;
	}

	public String getUserpassword() {
		return userpassword;
	}

	private static Properties _getCommonProperties() {
		if (databaseConfProperties == null) {
			databaseConfProperties = new Properties();
			InputStream input = null;
			try {
				input = new FileInputStream(Path.getConfPath("security/jetty-realm-dbms.properties"));

				databaseConfProperties.load(input);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			String url = databaseConfProperties.get("url").toString();
			Matcher matcher = Pattern.compile("[^/]+//(\\w+):(\\d+)/(\\w+)").matcher(url);
			if (matcher.find()) {
				databaseConfProperties.setProperty("host", matcher.group(1));
				databaseConfProperties.setProperty("port", matcher.group(2));
				databaseConfProperties.setProperty("database", matcher.group(3));
			}
		}

		return databaseConfProperties;
	}

	public static String getCommonHost() {
		return _getCommonProperties().get("host").toString();
	}
	public static String getCommonDatabase() {
		return _getCommonProperties().get("database").toString();
	}
	public static Integer getCommonPort() {
		return Integer.parseInt(_getCommonProperties().get("port").toString());
	}
	public static String getCommonUserName() {
		return _getCommonProperties().get("username").toString();
	}
	public static String getCommonPassword() {
		return _getCommonProperties().get("password").toString();
	}
}
