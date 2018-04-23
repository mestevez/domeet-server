package rest.util;

import conf.database.MainDatabaseProps;
import hibernate.SessionFactoryProvider;
import model.auth_user;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Session {

	private static Pattern REGEX_GET_SESSION = Pattern.compile("DefaultUserIdentity\\('([^']+)'\\)");

	public static auth_user getUser(HttpServletRequest httpServletRequest) {
		auth_user user = null;

		Object sessUsedID = httpServletRequest.getSession().getAttribute("org.eclipse.jetty.security.UserIdentity");
		if (sessUsedID != null) {
			Matcher matcher = REGEX_GET_SESSION.matcher(sessUsedID.toString());
			if (matcher.find()) {
				String user_email = matcher.group(1);
				if (user_email != null)
					user = auth_user.getUser(SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()), user_email);
			}
		}

		return user;
	}
}
