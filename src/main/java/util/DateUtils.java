package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {

	public static String getDateFormatFromLocale(Locale locale) {
		return ((SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, locale))
				.toPattern()
				.replaceAll("d+", "DD")
				.replaceAll("M+", "MM")
				.replaceAll("y+", "YYYY")
				.replaceAll("/", "-");
	}

	public static String getDateTimeFormatFromLocale(Locale locale) {
		return  ((SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale))
				.toPattern()
				.replaceAll("d+", "DD")
				.replaceAll("M+", "MM")
				.replaceAll("y+", "YYYY")
				.replaceAll("[hH]+", "HH")
				.replaceAll("\\s+a", "")
				.replace("/", "-");
	}
}
