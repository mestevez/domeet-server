package rest.util;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PageCommons {

	public static Map<String, Object> getFTLHeaderInfo(HttpServletRequest request, String title) {
		Map<String, Object> dataModel = new HashMap<>();
		Locale locale = request.getLocale();
		dataModel.put("lang", locale.getLanguage());
		dataModel.put("charset", "UTF-8");
		dataModel.put("pagetitle", title);

		return dataModel;
	}

}
