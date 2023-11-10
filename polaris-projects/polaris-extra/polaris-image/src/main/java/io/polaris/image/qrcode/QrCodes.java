package io.polaris.image.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import io.polaris.core.codec.Base64;
import io.polaris.core.io.Filenames;
import io.polaris.core.io.IO;
import io.polaris.core.io.ansi.AnsiColors;
import io.polaris.core.io.ansi.AnsiElement;
import io.polaris.core.io.ansi.AnsiEncoder;
import io.polaris.core.io.ansi.ForeOrBack;
import io.polaris.core.io.image.ImageEditor;
import io.polaris.core.io.image.Images;
import io.polaris.core.string.Strings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于Zxing的二维码工具类，支持：
 * <ul>
 *     <li>二维码生成和识别，见{@link BarcodeFormat#QR_CODE}</li>
 *     <li>条形码生成和识别，见{@link BarcodeFormat#CODE_39}等很多标准格式</li>
 * </ul>
 *
 * @author Qt
 * @since 1.8
 */
public class QrCodes {

	public static final String QR_TYPE_SVG = "svg";// SVG矢量图格式
	public static final String QR_TYPE_TXT = "txt";// Ascii Art字符画文本
	private static final AnsiColors ansiColors = new AnsiColors(AnsiColors.BitDepth.EIGHT);

	/**
	 * 生成代 logo 图片的 Base64 编码格式的二维码，以 String 形式表示
	 *
	 * @param content    内容
	 * @param qrConfig   二维码配置，包括宽度、高度、边距、颜色等
	 * @param targetType 类型（图片扩展名），见{@link #QR_TYPE_SVG}、 {@link #QR_TYPE_TXT}、{@link Images}
	 * @param logoBase64 logo 图片的 base64 编码
	 * @return 图片 Base64 编码字符串
	 */
	public static String generateAsBase64(String content, QrConfig qrConfig, String targetType, String logoBase64) throws IOException {
		return generateAsBase64(content, qrConfig, targetType, Base64.decode(logoBase64));
	}

	/**
	 * 生成代 logo 图片的 Base64 编码格式的二维码，以 String 形式表示
	 *
	 * @param content    内容
	 * @param qrConfig   二维码配置，包括宽度、高度、边距、颜色等
	 * @param targetType 类型（图片扩展名），见{@link #QR_TYPE_SVG}、 {@link #QR_TYPE_TXT}、{@link Images}
	 * @param logo       logo 图片的byte[]
	 * @return 图片 Base64 编码字符串
	 */
	public static String generateAsBase64(String content, QrConfig qrConfig, String targetType, byte[] logo) throws IOException {
		return generateAsBase64(content, qrConfig, targetType, Images.toImage(logo));
	}

	/**
	 * 生成代 logo 图片的 Base64 编码格式的二维码，以 String 形式表示
	 *
	 * @param content    内容
	 * @param qrConfig   二维码配置，包括宽度、高度、边距、颜色等
	 * @param targetType 类型（图片扩展名），见{@link #QR_TYPE_SVG}、 {@link #QR_TYPE_TXT}、{@link Images}
	 * @param logo       logo 图片的byte[]
	 * @return 图片 Base64 编码字符串
	 */
	public static String generateAsBase64(String content, QrConfig qrConfig, String targetType, Image logo) throws IOException {
		qrConfig.setImg(logo);
		return generateAsBase64(content, qrConfig, targetType);
	}

	/**
	 * 生成 Base64 编码格式的二维码，以 String 形式表示
	 *
	 * <p>
	 * 输出格式为: data:image/[type];base64,[data]
	 * </p>
	 *
	 * @param content    内容
	 * @param qrConfig   二维码配置，包括宽度、高度、边距、颜色等
	 * @param targetType 类型（图片扩展名），见{@link #QR_TYPE_SVG}、 {@link #QR_TYPE_TXT}、{@link Images}
	 * @return 图片 Base64 编码字符串
	 */
	public static String generateAsBase64(String content, QrConfig qrConfig, String targetType) throws IOException {
		String result;
		switch (targetType) {
			case QR_TYPE_SVG:
				String svg = generateAsSvg(content, qrConfig);
				result = svgToBase64(svg);
				break;
			case QR_TYPE_TXT:
				String txt = generateAsAsciiArt(content, qrConfig);
				result = txtToBase64(txt);
				break;
			default:
				final BufferedImage img = generate(content, qrConfig);
				result = Images.toBase64DataUri(img, targetType);
				break;
		}


		return result;
	}

	private static String txtToBase64(String txt) {
		return Images.toBase64DataUri("text/plain", Base64.encodeToString(txt.getBytes(StandardCharsets.UTF_8)));
	}

	private static String svgToBase64(String svg) {
		return Images.toBase64DataUri("image/svg+xml", Base64.encodeToString(svg.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * @param content  内容
	 * @param qrConfig 二维码配置，包括宽度、高度、边距、颜色等
	 * @return SVG矢量图（字符串）
	 * @since 5.8.6
	 */
	public static String generateAsSvg(String content, QrConfig qrConfig) throws IOException {
		final BitMatrix bitMatrix = encode(content, qrConfig);
		return toSVG(bitMatrix, qrConfig);
	}

	/**
	 * 生成ASCII Art字符画形式的二维码
	 *
	 * @param content 内容
	 * @return ASCII Art字符画形式的二维码字符串
	 * @since 5.8.6
	 */
	public static String generateAsAsciiArt(String content) throws IOException {
		return generateAsAsciiArt(content, 0, 0, 1);
	}

	/**
	 * 生成ASCII Art字符画形式的二维码
	 *
	 * @param content  内容
	 * @param qrConfig 二维码配置，仅宽度、高度、边距配置有效
	 * @return ASCII Art字符画形式的二维码
	 * @since 5.8.6
	 */
	public static String generateAsAsciiArt(String content, QrConfig qrConfig) throws IOException {
		final BitMatrix bitMatrix = encode(content, qrConfig);
		return toAsciiArt(bitMatrix, qrConfig);
	}

	/**
	 * @param content 内容
	 * @param width   宽度（单位：字符▄的大小）
	 * @param height  高度（单位：字符▄的大小）
	 * @param margin  边距大小（1~4）
	 * @return ASCII Art字符画形式的二维码
	 * @since 5.8.6
	 */
	public static String generateAsAsciiArt(String content, int width, int height, int margin) throws IOException {
		QrConfig qrConfig = new QrConfig(width, height).setMargin(margin);
		return generateAsAsciiArt(content, qrConfig);
	}


	/**
	 * 生成PNG格式的二维码图片，以byte[]形式表示
	 *
	 * @param content 内容
	 * @param width   宽度（单位：像素）
	 * @param height  高度（单位：像素）
	 * @return 图片的byte[]
	 * @since 4.0.10
	 */
	public static byte[] generatePng(String content, int width, int height) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		generate(content, width, height, Images.IMAGE_TYPE_PNG, out);
		return out.toByteArray();
	}

	/**
	 * 生成PNG格式的二维码图片，以byte[]形式表示
	 *
	 * @param content 内容
	 * @param config  二维码配置，包括宽度、高度、边距、颜色等
	 * @return 图片的byte[]
	 * @since 4.1.2
	 */
	public static byte[] generatePng(String content, QrConfig config) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		generate(content, config, Images.IMAGE_TYPE_PNG, out);
		return out.toByteArray();
	}

	/**
	 * 生成二维码到文件，二维码图片格式取决于文件的扩展名
	 *
	 * @param content    文本内容
	 * @param width      宽度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @param height     高度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @param targetFile 目标文件，扩展名决定输出格式
	 * @return 目标文件
	 */
	public static File generate(String content, int width, int height, File targetFile) throws IOException {
		String extName = Filenames.extName(targetFile);
		switch (extName) {
			case QR_TYPE_SVG:
				String svg = generateAsSvg(content, new QrConfig(width, height));
				IO.writeString(targetFile, StandardCharsets.UTF_8, svg);
				break;
			case QR_TYPE_TXT:
				String txt = generateAsAsciiArt(content, new QrConfig(width, height));
				IO.writeString(targetFile, StandardCharsets.UTF_8, txt);
				break;
			default:
				final BufferedImage image = generate(content, width, height);
				Images.write(image, targetFile);
				break;
		}

		return targetFile;
	}

	/**
	 * 生成二维码到文件，二维码图片格式取决于文件的扩展名
	 *
	 * @param content    文本内容
	 * @param config     二维码配置，包括宽度、高度、边距、颜色等
	 * @param targetFile 目标文件，扩展名决定输出格式
	 * @return 目标文件
	 * @since 4.1.2
	 */
	public static File generate(String content, QrConfig config, File targetFile) throws IOException {
		String extName = Filenames.extName(targetFile);
		switch (extName) {
			case QR_TYPE_SVG:
				final String svg = generateAsSvg(content, config);
				IO.writeString(targetFile, StandardCharsets.UTF_8, svg);
				break;
			case QR_TYPE_TXT:
				final String txt = generateAsAsciiArt(content, config);
				IO.writeString(targetFile, StandardCharsets.UTF_8, txt);
				break;
			default:
				final BufferedImage image = generate(content, config);
				Images.write(image, targetFile);
				break;
		}
		return targetFile;
	}

	/**
	 * 生成二维码到输出流
	 *
	 * @param content    文本内容
	 * @param width      宽度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @param height     高度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @param targetType 类型（图片扩展名），见{@link #QR_TYPE_SVG}、 {@link #QR_TYPE_TXT}、{@link Images}
	 * @param out        目标流
	 */
	public static void generate(String content, int width, int height, String targetType, OutputStream out) throws IOException {
		switch (targetType) {
			case QR_TYPE_SVG:
				final String svg = generateAsSvg(content, new QrConfig(width, height));
				IO.writeString(out, StandardCharsets.UTF_8, svg);
				break;
			case QR_TYPE_TXT:
				final String txt = generateAsAsciiArt(content, new QrConfig(width, height));
				IO.writeString(out, StandardCharsets.UTF_8, txt);
				break;
			default:
				final BufferedImage image = generate(content, width, height);
				Images.write(image, targetType, out);
				break;
		}
	}

	/**
	 * 生成二维码到输出流
	 *
	 * @param content    文本内容
	 * @param config     二维码配置，包括宽度、高度、边距、颜色等
	 * @param targetType 类型（图片扩展名），见{@link #QR_TYPE_SVG}、 {@link #QR_TYPE_TXT}、{@link Images}
	 * @param out        目标流
	 * @since 4.1.2
	 */
	public static void generate(String content, QrConfig config, String targetType, OutputStream out) throws IOException {
		switch (targetType) {
			case QR_TYPE_SVG:
				final String svg = generateAsSvg(content, config);
				IO.writeString(out, StandardCharsets.UTF_8, svg);
				break;
			case QR_TYPE_TXT:
				final String txt = generateAsAsciiArt(content, config);
				IO.writeString(out, StandardCharsets.UTF_8, txt);
				break;
			default:
				final BufferedImage image = generate(content, config);
				Images.write(image, targetType, out);
				break;
		}
	}

	/**
	 * 生成二维码图片
	 *
	 * @param content 文本内容
	 * @param width   宽度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @param height  高度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @return 二维码图片（黑白）
	 */
	public static BufferedImage generate(String content, int width, int height) throws IOException {
		return generate(content, new QrConfig(width, height));
	}

	/**
	 * 生成二维码或条形码图片
	 *
	 * @param content 文本内容
	 * @param format  格式，可选二维码或者条形码
	 * @param width   宽度（单位：像素）
	 * @param height  高度（单位：像素）
	 * @return 二维码图片（黑白）
	 */
	public static BufferedImage generate(String content, BarcodeFormat format, int width, int height) throws IOException {
		return generate(content, format, new QrConfig(width, height));
	}

	/**
	 * 生成二维码图片
	 *
	 * @param content 文本内容
	 * @param config  二维码配置，包括宽度、高度、边距、颜色等
	 * @return 二维码图片（黑白）
	 * @since 4.1.2
	 */
	public static BufferedImage generate(String content, QrConfig config) throws IOException {
		return generate(content, BarcodeFormat.QR_CODE, config);
	}

	/**
	 * 生成二维码或条形码图片<br>
	 * 只有二维码时QrConfig中的图片才有效
	 *
	 * @param content 文本内容
	 * @param format  格式，可选二维码、条形码等
	 * @param config  二维码配置，包括宽度、高度、边距、颜色等
	 * @return 二维码图片（黑白）
	 * @since 4.1.14
	 */
	public static BufferedImage generate(String content, BarcodeFormat format, QrConfig config) throws IOException {
		final BitMatrix bitMatrix = encode(content, format, config);
		final BufferedImage image = toImage(bitMatrix, config.foreColor != null ? config.foreColor : Color.BLACK.getRGB(), config.backColor);
		final Image logoImg = config.img;
		if (null != logoImg && BarcodeFormat.QR_CODE == format) {
			// 只有二维码可以贴图
			final int qrWidth = image.getWidth();
			final int qrHeight = image.getHeight();
			int width;
			int height;
			// 按照最短的边做比例缩放
			if (qrWidth < qrHeight) {
				width = qrWidth / config.ratio;
				height = logoImg.getHeight(null) * width / logoImg.getWidth(null);
			} else {
				height = qrHeight / config.ratio;
				width = logoImg.getWidth(null) * height / logoImg.getHeight(null);
			}

			ImageEditor.from(image).pressImage(//
				ImageEditor.from(logoImg).round(0.3).getImg(), // 圆角
				new Rectangle(width, height), //
				1//
			);
		}
		return image;
	}

	// ------------------------------------------------------------------------------------------------------------------- encode

	/**
	 * 将文本内容编码为二维码
	 *
	 * @param content 文本内容
	 * @param width   宽度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @param height  高度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @return {@link BitMatrix}
	 */
	public static BitMatrix encode(String content, int width, int height) throws IOException {
		return encode(content, BarcodeFormat.QR_CODE, width, height);
	}

	/**
	 * 将文本内容编码为二维码
	 *
	 * @param content 文本内容
	 * @param config  二维码配置，包括宽度、高度、边距、颜色等
	 * @return {@link BitMatrix}
	 * @since 4.1.2
	 */
	public static BitMatrix encode(String content, QrConfig config) throws IOException {
		return encode(content, BarcodeFormat.QR_CODE, config);
	}

	/**
	 * 将文本内容编码为条形码或二维码
	 *
	 * @param content 文本内容
	 * @param format  格式枚举
	 * @param width   宽度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @param height  高度（单位：类型为一般图片或SVG时，单位是像素，类型为 Ascii Art 字符画时，单位是字符▄或▀的大小）
	 * @return {@link BitMatrix}
	 */
	public static BitMatrix encode(String content, BarcodeFormat format, int width, int height) throws IOException {
		return encode(content, format, new QrConfig(width, height));
	}

	/**
	 * 将文本内容编码为条形码或二维码
	 *
	 * @param content 文本内容
	 * @param format  格式枚举
	 * @param config  二维码配置，包括宽度、高度、边距、颜色等
	 * @return {@link BitMatrix}
	 * @since 4.1.2
	 */
	public static BitMatrix encode(String content, BarcodeFormat format, QrConfig config) throws IOException {
		final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		if (null == config) {
			// 默认配置
			config = new QrConfig();
		}

		BitMatrix bitMatrix;
		try {
			bitMatrix = multiFormatWriter.encode(content, format, config.width, config.height, config.toHints(format));
		} catch (WriterException e) {
			throw new IOException(e);
		}

		return bitMatrix;
	}

	// ------------------------------------------------------------------------------------------------------------------- decode

	/**
	 * 解码二维码或条形码图片为文本
	 *
	 * @param qrCodeInputStream 二维码输入流
	 * @return 解码文本
	 */
	public static String decode(InputStream qrCodeInputStream) throws IOException {
		return decode(Images.read(qrCodeInputStream));
	}

	/**
	 * 解码二维码或条形码图片为文本
	 *
	 * @param qrCodeFile 二维码文件
	 * @return 解码文本
	 */
	public static String decode(File qrCodeFile) throws IOException {
		return decode(Images.read(qrCodeFile));
	}

	/**
	 * 将二维码或条形码图片解码为文本
	 *
	 * @param image {@link Image} 二维码图片
	 * @return 解码后的文本
	 */
	public static String decode(Image image) {
		return decode(image, true, false);
	}

	/**
	 * 将二维码或条形码图片解码为文本<br>
	 * 此方法会尝试使用{@link HybridBinarizer}和{@link GlobalHistogramBinarizer}两种模式解析<br>
	 * 需要注意部分二维码如果不带logo，使用PureBarcode模式会解析失败，此时须设置此选项为false。
	 *
	 * @param image         {@link Image} 二维码图片
	 * @param isTryHarder   是否优化精度
	 * @param isPureBarcode 是否使用复杂模式，扫描带logo的二维码设为true
	 * @return 解码后的文本
	 * @since 4.3.1
	 */
	public static String decode(Image image, boolean isTryHarder, boolean isPureBarcode) {
		return decode(image, buildHints(isTryHarder, isPureBarcode));
	}

	/**
	 * 将二维码或条形码图片解码为文本<br>
	 * 此方法会尝试使用{@link HybridBinarizer}和{@link GlobalHistogramBinarizer}两种模式解析<br>
	 * 需要注意部分二维码如果不带logo，使用PureBarcode模式会解析失败，此时须设置此选项为false。
	 *
	 * @param image {@link Image} 二维码图片
	 * @param hints 自定义扫码配置，包括算法、编码、复杂模式等
	 * @return 解码后的文本
	 * @since 5.7.12
	 */
	public static String decode(Image image, Map<DecodeHintType, Object> hints) {
		final MultiFormatReader formatReader = new MultiFormatReader();
		formatReader.setHints(hints);

		final LuminanceSource source = new BufferedImageLuminanceSource(Images.toBufferedImage(image));

		Result result = _decode(formatReader, new HybridBinarizer(source));
		if (null == result) {
			result = _decode(formatReader, new GlobalHistogramBinarizer(source));
		}

		return null != result ? result.getText() : null;
	}

	/**
	 * BitMatrix转BufferedImage
	 *
	 * @param matrix    BitMatrix
	 * @param foreColor 前景色
	 * @param backColor 背景色(null表示透明背景)
	 * @return BufferedImage
	 * @since 4.1.2
	 */
	public static BufferedImage toImage(BitMatrix matrix, int foreColor, Integer backColor) {
		final int width = matrix.getWidth();
		final int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, null == backColor ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (matrix.get(x, y)) {
					image.setRGB(x, y, foreColor);
				} else if (null != backColor) {
					image.setRGB(x, y, backColor);
				}
			}
		}
		return image;
	}

	/**
	 * BitMatrix转SVG(字符串)
	 *
	 * @param matrix   BitMatrix
	 * @param qrConfig 二维码配置，包括宽度、高度、边距、颜色等
	 * @return SVG矢量图（字符串）
	 * @since 5.8.6
	 */
	public static String toSVG(BitMatrix matrix, QrConfig qrConfig) throws IOException {
		return toSVG(matrix, qrConfig.foreColor, qrConfig.backColor, qrConfig.img, qrConfig.getRatio());
	}

	/**
	 * BitMatrix转SVG(字符串)
	 *
	 * @param matrix    BitMatrix
	 * @param foreColor 前景色
	 * @param backColor 背景色(null表示透明背景)
	 * @param logoImg   LOGO图片
	 * @param ratio     二维码中的Logo缩放的比例系数，如5表示长宽最小值的1/5
	 * @return SVG矢量图（字符串）
	 * @since 5.8.6
	 */
	public static String toSVG(BitMatrix matrix, Integer foreColor, Integer backColor, Image logoImg, int ratio) throws IOException {
		StringBuilder sb = new StringBuilder();
		int qrWidth = matrix.getWidth();
		int qrHeight = matrix.getHeight();
		int moduleHeight = (qrHeight == 1) ? qrWidth / 2 : 1;
		for (int y = 0; y < qrHeight; y++) {
			for (int x = 0; x < qrWidth; x++) {
				if (matrix.get(x, y)) {
					sb.append(" M").append(x).append(",").append(y).append("h1v").append(moduleHeight).append("h-1z");
				}
			}
		}
		qrHeight *= moduleHeight;
		String logoBase64 = "";
		int logoWidth = 0;
		int logoHeight = 0;
		int logoX = 0;
		int logoY = 0;
		if (logoImg != null) {
			logoBase64 = Images.toBase64DataUri(logoImg, "png");
			// 按照最短的边做比例缩放
			if (qrWidth < qrHeight) {
				logoWidth = qrWidth / ratio;
				logoHeight = logoImg.getHeight(null) * logoWidth / logoImg.getWidth(null);
			} else {
				logoHeight = qrHeight / ratio;
				logoWidth = logoImg.getWidth(null) * logoHeight / logoImg.getHeight(null);
			}
			logoX = (qrWidth - logoWidth) / 2;
			logoY = (qrHeight - logoHeight) / 2;

		}

		StringBuilder result = new StringBuilder();
		result.append("<svg width=\"").append(qrWidth).append("\" height=\"").append(qrHeight).append("\" \n");
		if (backColor != null) {
			Color back = new Color(backColor, true);
			result.append("style=\"background-color:rgba(").append(back.getRed()).append(",").append(back.getGreen()).append(",").append(back.getBlue()).append(",").append(back.getAlpha()).append(")\"\n");
		}
		result.append("viewBox=\"0 0 ").append(qrWidth).append(" ").append(qrHeight).append("\" \n");
		result.append("xmlns=\"http://www.w3.org/2000/svg\" \n");
		result.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\" >\n");
		result.append("<path d=\"").append(sb).append("\" ");
		if (foreColor != null) {
			Color fore = new Color(foreColor, true);
			result.append("stroke=\"rgba(").append(fore.getRed()).append(",").append(fore.getGreen()).append(",").append(fore.getBlue()).append(",").append(fore.getAlpha()).append(")\"");
		}
		result.append(" /> \n");
		if (Strings.isNotBlank(logoBase64)) {
			result.append("<image xlink:href=\"").append(logoBase64).append("\" height=\"").append(logoHeight).append("\" width=\"").append(logoWidth).append("\" y=\"").append(logoY).append("\" x=\"").append(logoX).append("\" />\n");
		}
		result.append("</svg>");
		return result.toString();
	}

	/**
	 * BitMatrix转ASCII Art字符画形式的二维码
	 *
	 * @param bitMatrix BitMatrix
	 * @param qrConfig  QR设置
	 * @return ASCII Art字符画形式的二维码
	 * @since 5.8.6
	 */
	public static String toAsciiArt(BitMatrix bitMatrix, QrConfig qrConfig) {
		final int width = bitMatrix.getWidth();
		final int height = bitMatrix.getHeight();


		final AnsiElement foreground = qrConfig.foreColor == null ? null : rgbToAnsi8BitElement(qrConfig.foreColor, ForeOrBack.FORE);
		final AnsiElement background = qrConfig.backColor == null ? null : rgbToAnsi8BitElement(qrConfig.backColor, ForeOrBack.BACK);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i <= height; i += 2) {
			StringBuilder rowBuilder = new StringBuilder();
			for (int j = 0; j < width; j++) {
				boolean tp = bitMatrix.get(i, j);
				boolean bt = i + 1 >= height || bitMatrix.get(i + 1, j);
				if (tp && bt) {
					rowBuilder.append(' ');//'\u0020'
				} else if (tp) {
					rowBuilder.append('▄');//'\u2584'
				} else if (bt) {
					rowBuilder.append('▀');//'\u2580'
				} else {
					rowBuilder.append('█');//'\u2588'
				}
			}
			builder.append(AnsiEncoder.encode(foreground, background, rowBuilder)).append('\n');
		}
		return builder.toString();
	}

	/*	*/

	/**
	 * rgb转AnsiElement
	 *
	 * @param rgb        rgb颜色值
	 * @param foreOrBack 前景or背景
	 * @return AnsiElement
	 * @since 5.8.6
	 */
	private static AnsiElement rgbToAnsi8BitElement(int rgb, ForeOrBack foreOrBack) {
		return ansiColors.findClosest(new Color(rgb)).toAnsiElement(foreOrBack);
	}


	/**
	 * 创建解码选项
	 *
	 * @param isTryHarder   是否优化精度
	 * @param isPureBarcode 是否使用复杂模式，扫描带logo的二维码设为true
	 * @return 选项Map
	 */
	private static Map<DecodeHintType, Object> buildHints(boolean isTryHarder, boolean isPureBarcode) {
		final HashMap<DecodeHintType, Object> hints = new HashMap<>();
		hints.put(DecodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);

		// 优化精度
		if (isTryHarder) {
			hints.put(DecodeHintType.TRY_HARDER, true);
		}
		// 复杂模式，开启PURE_BARCODE模式
		if (isPureBarcode) {
			hints.put(DecodeHintType.PURE_BARCODE, true);
		}
		return hints;
	}

	/**
	 * 解码多种类型的码，包括二维码和条形码
	 *
	 * @param formatReader {@link MultiFormatReader}
	 * @param binarizer    {@link Binarizer}
	 * @return {@link Result}
	 */
	private static Result _decode(MultiFormatReader formatReader, Binarizer binarizer) {
		try {
			return formatReader.decodeWithState(new BinaryBitmap(binarizer));
		} catch (NotFoundException e) {
			return null;
		}
	}
}
