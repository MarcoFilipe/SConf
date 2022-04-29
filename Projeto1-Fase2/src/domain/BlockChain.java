package domain;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class BlockChain {

	private static final String SERVER_KEYSTORE_PATH = "Projeto1-Fase2/security/server.keystore";
	
	private PrivateKey privateKey = null;
	private long index = 1;
	private byte[] prevHash = new byte[32];
	private Block block = null;
	private List<Block> blockchain = new ArrayList<>();
	
	public BlockChain() {
		FileInputStream kfile;
		try {
			kfile = new FileInputStream(SERVER_KEYSTORE_PATH);
			KeyStore kstore = KeyStore.getInstance("JCEKS");
			kstore.load(kfile, "123456".toCharArray());

			privateKey = (PrivateKey) kstore.getKey("server", "123456".toCharArray());
			
			block = new Block(privateKey, index, prevHash);
			blockchain.add(block);
		} catch (IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException
				| CertificateException e) {
			e.printStackTrace();
		}
	}
	
	public void writeTransaction(byte[] transaction) {
		if (block.isClosed()) {
			index++;
			prevHash = block.getHash();
			block = new Block(privateKey, index, prevHash);
			blockchain.add(block);
		}
		block.writeTransaction(transaction);
	}
}
