package rest.util;

import conf.database.MainDatabaseProps;
import hibernate.SessionFactoryProvider;
import model.auth_user;
import model.user;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class PageCommons {

	public static Map<String, Object> getFTLHeaderInfo(HttpServletRequest request, String bundlepath) {
		Locale locale = request.getLocale();
		Map<String, Object> dataModel = new HashMap<>();
		ResourceBundle bundle = ResourceBundle.getBundle(bundlepath, locale);
		ResourceBundle commonBundle = ResourceBundle.getBundle("ftl/webapp/common/common", locale);

		dataModel.put("lang", locale.getLanguage());
		dataModel.put("charset", "UTF-8");
		dataModel.put("pagetitle", bundle != null ? bundle.getString("pagetitle") : "Undefined");

		Map<String, String> sessUser = new HashMap<>();
		auth_user auth_user = Session.getUser(request);
		if (auth_user != null) {
			sessUser.put("user_email", auth_user.getUserMail());

			user user = model.user.getUser(SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()), auth_user.getUserID());
			if (user != null) {
				sessUser.put("user_first_name", user.getUserFirstname());
				sessUser.put("user_last_name", user.getUserLastname());
				sessUser.put("user_photo", null);
			}
		}
		dataModel.put("user", sessUser);

		Map<String, String> i18n = new HashMap<>();
		if (commonBundle != null) {
			for (String key : commonBundle.keySet()) {
				i18n.put(key, commonBundle.getString(key));
			}
		}
		if (bundle != null) {
			for (String key : bundle.keySet()) {
				i18n.put(key, bundle.getString(key));
			}
		}
		dataModel.put("i18n", i18n);

		return dataModel;
	}

}
