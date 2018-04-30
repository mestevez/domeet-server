package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class TestDateUtils {

	@Test
	void getDateFormatFromLocale() {
		Assertions.assertEquals("MM-dd-yyyy", DateUtils.getDateFormatFromLocale(Locale.US));
		Assertions.assertEquals("dd-MM-yyyy", DateUtils.getDateFormatFromLocale(Locale.UK));
	}
	@Test
	void getDateTimeFormatFromLocale() {
		Assertions.assertEquals("MM-dd-yyyy HH:mm:ss", DateUtils.getDateTimeFormatFromLocale(Locale.US));
		Assertions.assertEquals("dd-MM-yyyy HH:mm:ss", DateUtils.getDateTimeFormatFromLocale(Locale.UK));
	}
}