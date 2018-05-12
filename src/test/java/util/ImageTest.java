package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

class ImageTest {

	@Test
	void getImageForURL() throws IOException {
		InputStream imageAsStream = this.getClass().getResourceAsStream("convert.jpg");
		Assertions.assertEquals(10617035, Image.getImageForURL(imageAsStream).length());
	}

	@Test
	void getImageForURLScaled() throws IOException {
		InputStream imageAsStream = this.getClass().getResourceAsStream("convert.jpg");
		Assertions.assertEquals(3375, Image.getImageForURL(imageAsStream, 64, 64).length());
	}
}