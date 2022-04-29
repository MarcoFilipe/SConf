package domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class BlockChain {

	private static final String SERVER_KEYSTORE_PATH = "Projeto1-Fase2/security/server.keystore";

	private PrivateKey privateKey = null;
	private long index;
	private byte[] prevHash;
	private Block block = null;

	public BlockChain(long index, byte[] hash, long numTransactions) {
		FileInputStream kfile;
		try {
			this.index = index;
			prevHash = hash;

			kfile = new FileInputStream(SERVER_KEYSTORE_PATH);
			KeyStore kstore = KeyStore.getInstance("JCEKS");
			kstore.load(kfile, "123456".toCharArray());

			privateKey = (PrivateKey) kstore.getKey("server", "123456".toCharArray());

			block = new Block(privateKey, index, prevHash);
			block.setNumTransactions(numTransactions);
		} catch (IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException
				| CertificateException e) {
			e.printStackTrace();
		}
	}

	public void writeTransaction(String strTransaction, byte[] signature) {
		byte[] transaction = strTransaction.getBytes();
		if (block.isClosed()) {
			index++;
			prevHash = block.getHash();
			block = new Block(privateKey, index, prevHash);
		}
		block.writeTransaction(transaction, signature);
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public void setHash(byte[] hash) {
		prevHash = hash;
	}
}
