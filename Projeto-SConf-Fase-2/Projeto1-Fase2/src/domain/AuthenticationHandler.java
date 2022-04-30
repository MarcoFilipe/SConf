package domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import data.UsersData;
import data.UsersData.User;

/**
 * Classe responsavel pela autenticacao do cliente.
 * 
 * @author grupo 36.
 *
 */
public class AuthenticationHandler {

	private BankAccountCatalog catalog = null;
	private long nonce;

	public AuthenticationHandler(BankAccountCatalog catalog) {
		this.catalog = catalog;
		SecureRandom rand = new SecureRandom();
		nonce = rand.nextLong();
	}

	public long getNonce() {
		return nonce;
	}

	/**
	 * Verifies if the user is registered.
	 * 
	 * @param userID The user id
	 * @return The verification result
	 */
	public boolean isRegistered(String cipherPass, String userID) {
		return (UsersData.getLine(cipherPass, userID) != null);
	}

	public void registerNewUser(String cipherPass, String userID, byte[] certificateBytes, String certificatePath) {
		try (FileOutputStream fos = new FileOutputStream(certificatePath)) {
			fos.write(certificateBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

		catalog.add(userID, new BankAccount());
		UsersData.addLine(cipherPass, userID, certificatePath);
	}

	public boolean verifyNonce(long nonce, byte[] signedNonce, Certificate certificate) {
		if (this.nonce == nonce) {
			PublicKey publicKey = certificate.getPublicKey();
			try {
				Signature signature = Signature.getInstance("MD5withRSA");
				signature.initVerify(publicKey);
				signature.update(Long.valueOf(nonce).byteValue());
				if (signature.verify(signedNonce)) {
					return true;
				}
			} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean verifyNonce(String cipherPass, String userID, byte[] signedNonce) {
		User user = UsersData.getLine(cipherPass, userID);
		try {
			FileInputStream fis = new FileInputStream(user.getCertificatePath());
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			Certificate certificate = cf.generateCertificate(fis);
			PublicKey publicKey = certificate.getPublicKey();
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initVerify(publicKey);
			signature.update(Long.valueOf(nonce).byteValue());
			if (signature.verify(signedNonce)) {
				return true;
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | FileNotFoundException
				| CertificateException e) {
			e.printStackTrace();
		}
		return false;
	}

}
