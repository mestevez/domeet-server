package rest.util;

import model.auth_user;
import model.user;
import org.hibernate.Session;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestSessionUtils {

	private static Pattern REGEX_GET_SESSION = Pattern.compile("DefaultUserIdentity\\('([^']+)'\\)");

	public static auth_user getUserAuth(HttpServletRequest httpServletRequest, Session session) {
		auth_user user = null;

		Object sessUsedID = httpServletRequest.getSession().getAttribute("org.eclipse.jetty.security.UserIdentity");
		if (sessUsedID != null) {
			Matcher matcher = REGEX_GET_SESSION.matcher(sessUsedID.toString());
			if (matcher.find()) {
				String user_email = matcher.group(1);
				if (user_email != null)
					user = auth_user.getUser(session, user_email);
			}
		}

		return user;
	}

	public static user getUserApp(HttpServletRequest httpServletRequest, Session session) {
		auth_user auth_user = RestSessionUtils.getUserAuth(httpServletRequest, session);

		return auth_user == null ? null : model.user.getUser(session,auth_user.getUserID() );
	}
}
