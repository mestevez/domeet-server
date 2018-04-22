package rest.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class PageCommons {

	public static Map<String, Object> getFTLHeaderInfo(HttpServletRequest request, String bundlepath) {
		Locale locale = request.getLocale();
		Map<String, Object> dataModel = new HashMap<>();
		ResourceBundle bundle = ResourceBundle.getBundle(bundlepath, locale);

		dataModel.put("lang", locale.getLanguage());
		dataModel.put("charset", "UTF-8");
		dataModel.put("pagetitle", bundle != null ? bundle.getString("pagetitle") : "Undefined");

		Map<String, String> i18n = new HashMap<>();
		if (bundle != null) {
			for (String key : bundle.keySet()) {
				i18n.put(key, bundle.getString(key));
			}
		}
		dataModel.put("i18n", i18n);

		return dataModel;
	}

}
