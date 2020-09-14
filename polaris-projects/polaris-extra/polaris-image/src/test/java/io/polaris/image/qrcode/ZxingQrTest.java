package io.polaris.image.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.polaris.core.codec.Base64;
import io.polaris.core.io.image.Images;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Qt
 * @since 1.8
 */
public class ZxingQrTest {

	@Test
	void test01() throws IOException, WriterException {
		String data = Randoms.randomString(1024);
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 350, 350);
		ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
		byte[] byteArray = pngOutputStream.toByteArray();
		System.out.println(Images.toBase64ImageDataUri("png", Base64.encodeToString(byteArray)));
	}
}
