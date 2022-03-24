package server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

public class QRCodeGenerator {

	public void generateQRCode(String user, double amount) throws Exception {

		String s = String.valueOf(amount);
		String data = user + ":" + s;
		String path = ".\\QR Codes\\" + user + ".jpg";

		BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500);

		MatrixToImageWriter.writeToPath(matrix, "jpg", Paths.get(path));
	}

	public String readQRCode(String QRCode) {
		try {
			String path = ".\\QR Codes\\" + QRCode + ".jpg";

			FileInputStream fi = new FileInputStream(path);
			BufferedImage bf = ImageIO.read(fi);

			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bf)));

			Result result = new MultiFormatReader().decode(bitmap);
			String s = result.getText();
			fi.close();
			File myObj = new File(path);

			if (!myObj.exists()) {

				return "fileNotExists";
			}
			myObj.delete();

			return s;

		} catch (Exception e) {

		}
		return null;
	}

}
