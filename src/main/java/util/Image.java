package util;

import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Base64;

public class Image {

	public static String getBase64URL(String contentType, String base64Data) {
		return "data:" + contentType + ";base64," + base64Data;
	}

	public static String getImageForURL(InputStream imagesrc) throws IOException {
		return getImageForURL(IOUtils.toByteArray(imagesrc));
	}

	public static String getImageForURL(byte[] imagesrc) throws IOException {
		String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(imagesrc));
		return  getBase64URL(contentType, Base64.getEncoder().encodeToString(imagesrc));
	}

	public static String getImageForURL(InputStream imagesrc, int width, int height) throws IOException {
		return getImageForURL(IOUtils.toByteArray(imagesrc), width, height);
	}

	public static String getImageForURL(byte[] imagesrc, int width, int height) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imagesrc);

		String contentType = URLConnection.guessContentTypeFromStream(byteArrayInputStream);
		String formatName = contentType.replace("image/", "");

		BufferedImage scaledBufferedImage = resizeImage(ImageIO.read(byteArrayInputStream), width, height);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( scaledBufferedImage, formatName, baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();

		return getBase64URL(contentType, Base64.getEncoder().encodeToString(imageInByte));
	}

	/**
	 * This function resize the image file and returns the BufferedImage object that can be saved to file system.
	 */
	private static BufferedImage resizeImage(final java.awt.Image image, int width, int height) {
		final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics2D = bufferedImage.createGraphics();
		graphics2D.setComposite(AlphaComposite.Src);
		//below three lines are for RenderingHints for better image quality at cost of higher processing time
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		graphics2D.dispose();
		return bufferedImage;
	}
}
