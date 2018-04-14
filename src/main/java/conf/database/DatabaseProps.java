package conf.database;

public abstract class DatabaseProps {
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
}
