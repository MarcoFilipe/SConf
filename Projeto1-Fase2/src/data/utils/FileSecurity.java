package data.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class FileSecurity {

	private static final String TEMP = ".temp";

	private static byte[] params;

	public static void cipherFile(File file, File temp, String password) {
		SecretKey key = generateKey(password);
		encryption(file, temp, key);
	}

	public static File decipherFile(File file, String password) {
		SecretKey key = generateKey(password);
		return decryption(file, key);
	}

	private static SecretKey generateKey(String password) {
		byte[] salt = { (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99, (byte) 0x52, (byte) 0x3e, (byte) 0xea,
				(byte) 0xf2 };
		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 20); // pass, salt, iterations
		SecretKeyFactory kf;
		SecretKey key = null;
		try {
			kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
			key = kf.generateSecret(keySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return key;
	}

	private static void encryption(File file, File temp, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			FileInputStream fis = new FileInputStream(temp);
			FileOutputStream fos = new FileOutputStream(file);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);

			byte[] bytes = new byte[16];
			int i = 0;

			while ((i = fis.read(bytes)) != -1) {
				cos.write(bytes, 0, i);
			}

			params = cipher.getParameters().getEncoded();
			
			cos.close();
			fos.close();
			fis.close();
			
			temp.delete();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
			e.printStackTrace();
		}
	}

	private static File decryption(File file, SecretKey key) {
		File temp = null;
		try {
			AlgorithmParameters p = AlgorithmParameters.getInstance("PBEWithHmacSHA256AndAES_128");
			p.init(params);
			Cipher cipher = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
			cipher.init(Cipher.DECRYPT_MODE, key, p);
			
			temp = File.createTempFile("users", TEMP);
			
			FileInputStream fis = new FileInputStream(file);
			FileOutputStream fos = new FileOutputStream(temp);

			CipherInputStream cis = new CipherInputStream(fis, cipher);
			byte[] b = new byte[16];
			int i = cis.read(b);
			while (i != -1) {
				fos.write(b, 0, i);
				i = cis.read(b);
			}

			cis.close();
			fis.close();
			fos.close();
		} catch (NoSuchAlgorithmException | IOException | InvalidKeyException | InvalidAlgorithmParameterException
				| NoSuchPaddingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return temp;
	}

}
