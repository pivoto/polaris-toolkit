package io.polaris.image.qrcode;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.polaris.core.io.IO;
import io.polaris.core.io.image.Images;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

class QrCodesTest {

	@Test
	public void generateTest() throws IOException {
		final BufferedImage image = QrCodes.generate("https://www.baidu.com/", 300, 300);
		Images.write(image, Paths.get("d:/data/test.png").toFile());
	}

	@Test
	public void generateCustomTest() throws IOException {
		final QrConfig config = new QrConfig();
		config.setMargin(0);
		config.setForeColor(Color.CYAN);
		// 背景色透明
		config.setBackColor(null);
		config.setErrorCorrection(ErrorCorrectionLevel.H);
		QrCodes.generate("https://www.baidu.com/", config, Paths.get("d:/data/testCustomTest.png").toFile());
	}

	@Test
	public void generateNoCustomColorTest() throws IOException {
		final QrConfig config = new QrConfig();
		config.setMargin(0);
		config.setForeColor(null);
		// 背景色透明
		config.setBackColor(null);
		config.setErrorCorrection(ErrorCorrectionLevel.H);
		QrCodes.generate("https://www.baidu.com/", config, Paths.get("d:/data/test-CustomTest.png").toFile());
	}

	@Test
	public void generateWithLogoTest() throws IOException {
		QrCodes.generate(//
			"https://www.baidu.com/", //
			QrConfig.create().setImg("d:/data/logo.png"), //
			Paths.get("d:/data/test-WithLogo.png").toFile());
	}

	@Test
	public void decodeTest() throws IOException {
		final String decode = QrCodes.decode(Paths.get("d:/data/test-WithLogo.png").toFile());
		System.out.println(decode);
	}

	@Test
	public void generateAsBase64Test() throws IOException {
		final String base64 = QrCodes.generateAsBase64("https://www.baidu.com/", new QrConfig(400, 400), "png");
		System.out.println(base64);
	}


	@Test
	public void generateAsBase64Test2() throws IOException {
		final String base64 = QrCodes.generateAsBase64("https://www.baidu.com/", new QrConfig(400, 400), "svg");
		System.out.println(base64);
	}

	@Test
	public void pdf417Test() throws IOException {
		final BufferedImage image = QrCodes.generate("content111", BarcodeFormat.PDF_417, QrConfig.create());
		Images.write(image, Paths.get("d:/data/test-pdf417Test.png").toFile());
	}

	@Test
	public void generateDataMatrixTest() throws IOException {
		final QrConfig qrConfig = QrConfig.create();
		qrConfig.setShapeHint(SymbolShapeHint.FORCE_RECTANGLE);
		final BufferedImage image = QrCodes.generate("content111", BarcodeFormat.DATA_MATRIX, qrConfig);
		Images.write(image, Paths.get("d:/data/test-DataMatrix1.png").toFile());

		final QrConfig config = QrConfig.create();
		config.setShapeHint(SymbolShapeHint.FORCE_SQUARE);
		final BufferedImage imageSquare = QrCodes.generate("content111", BarcodeFormat.DATA_MATRIX, qrConfig);
		Images.write(imageSquare, Paths.get("d:/data/test-DataMatrix2.png").toFile());
	}

	@Test
	public void generateSvgTest() throws IOException {
		final QrConfig qrConfig = QrConfig.create()
			.setImg("d:/data/logo.png")
			.setForeColor(Color.blue)
			.setBackColor(Color.pink)
			.setRatio(8)
			.setErrorCorrection(ErrorCorrectionLevel.M)
			.setMargin(1);
		final String svg = QrCodes.generateAsSvg("https://www.baidu.com/", qrConfig);
		IO.writeString(Paths.get("d:/data/test-Svg.svg").toFile(), StandardCharsets.UTF_8, svg);
	}

	@Test
	public void generateAsciiArtTest() throws IOException {
		final QrConfig qrConfig = QrConfig.create()
			.setForeColor(Color.BLUE)
			.setBackColor(Color.MAGENTA)
			.setWidth(0)
			.setHeight(0).setMargin(1);
		final String asciiArt = QrCodes.generateAsAsciiArt("https://www.baidu.com/", qrConfig);
		System.out.println(asciiArt);
	}

	@Test
	public void generateAsciiArtNoCustomColorTest() throws IOException {
		final QrConfig qrConfig = QrConfig.create()
			.setForeColor(null)
			.setBackColor(null)
			.setWidth(0)
			.setHeight(0).setMargin(1);
		final String asciiArt = QrCodes.generateAsAsciiArt("https://www.baidu.com/", qrConfig);
		System.out.println(asciiArt);
	}

	@Test
	public void generateToFileTest() throws IOException {
		final QrConfig qrConfig = QrConfig.create()
			.setForeColor(Color.BLUE)
			.setBackColor(new Color(0, 200, 255))
			.setWidth(0)
			.setHeight(0).setMargin(1);
		final File qrFile = QrCodes.generate("https://www.baidu.com/", qrConfig, Paths.get("d:/data/test-ascii_art_qr_code.txt").toFile());
	}
}
