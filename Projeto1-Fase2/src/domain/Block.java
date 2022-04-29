package domain;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

public class Block {

	private ObjectOutputStream file = null;
	private Signature signEng = null;
	private MessageDigest digestEng = null;
	private byte[] prevHash = null;
	private long numTransactions = 0;
	private boolean isClosed = false;

	public Block(PrivateKey pk, long index, byte[] prevHash) {
		try {
			FileOutputStream fileStream = new FileOutputStream("block_" + index + ".blk");
			file = new ObjectOutputStream(fileStream);
			signEng = Signature.getInstance("SHA256withRSA");
			signEng.initSign(pk);
			digestEng = MessageDigest.getInstance("SHA256");
			this.prevHash = prevHash;
			file.writeObject(prevHash);
			file.writeObject(String.valueOf(index).getBytes());
			file.writeObject(String.valueOf(5).getBytes());
		} catch (NoSuchAlgorithmException | InvalidKeyException | IOException e) {
			e.printStackTrace();
		}
	}

	public void writeTransaction(byte[] transaction) {
		try {
			file.writeObject(transaction);
			signEng.update(transaction);
			digestEng.update(transaction);
			numTransactions++;
			if (numTransactions == 5) {
				byte[] sign = signEng.sign();
				file.writeObject(sign);
				file.flush();
				file.close();
				isClosed = true;
				digestEng.update(sign);
				prevHash = digestEng.digest();
			}
		} catch (IOException | SignatureException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isClosed() {
		return isClosed;
	}

	public byte[] getHash() {
		return prevHash;
	}

}
