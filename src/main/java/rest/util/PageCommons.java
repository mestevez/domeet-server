package rest.util;

import gson.GSONConfiguration;
import model.user;
import org.hibernate.Session;
import util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class PageCommons {

	public static Map<String, Object> getFTLHeaderInfo(
		HttpServletRequest request,
		Session session,
		String bundlepath,
		String appid,
		String appjs,
		String appcss,
		Map<String, Object> custData
	) {
		Locale locale = request.getLocale();
		Map<String, Object> dataModel = new HashMap<>();
		ResourceBundle bundle = ResourceBundle.getBundle(bundlepath, locale);
		ResourceBundle commonBundle = ResourceBundle.getBundle("i18n/common", locale);

		dataModel.put("lang", locale.getLanguage());
		dataModel.put("charset", "UTF-8");
		dataModel.put("pagetitle", bundle != null ? bundle.getString("pagetitle") : "Undefined");

		Map<String, Object> appData = new HashMap<>();
		appData.put("app", custData);

		Map<String, Object> sessUser = new HashMap<>();
		user user = RestSessionUtils.getUserApp(request, session);
		if (user != null) {
			sessUser.put("user_email", user.getUserAuth().getUserMail());
			sessUser.put("user_first_name", user.getUserFirstname());
			sessUser.put("user_last_name", user.getUserLastname());
			sessUser.put("user_photo", user.getUserPhoto() != null ? "/app/account/photo" : null);

			Map<String, Object> userLocale = new HashMap<>();
			userLocale.put("code", String.format("%s-%s", locale.getLanguage(), locale.getCountry()));
			userLocale.put("dateformat", DateUtils.getDateFormatFromLocale(locale));
			userLocale.put("firstdayofweek", Calendar.getInstance(locale).getFirstDayOfWeek());
			userLocale.put("timestampformat", DateUtils.getDateTimeFormatFromLocale(locale));
			sessUser.put("locale", userLocale);
		}
		appData.put("user", sessUser);

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
		appData.put("i18n", i18n);

		dataModel.put("appData", GSONConfiguration.getTestInstance().getConfiguration().toJson(appData));
		dataModel.put("appcss", appcss);
		dataModel.put("appid", appid);
		dataModel.put("appjs", appjs);

		return dataModel;
	}
}
