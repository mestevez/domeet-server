package util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

	public static DateFormat ISOTimestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static {
		//This is the key line which converts the date to UTC which cannot be accessed with the default serializer
		ISOTimestampFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

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

	public static Timestamp parseISOTimestampString2Java(String datestr) throws ParseException {
		return new Timestamp(ISOTimestampFormat.parse(datestr).getTime());
	}
}
