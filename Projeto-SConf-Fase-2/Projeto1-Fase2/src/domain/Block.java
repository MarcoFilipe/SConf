package domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;

import domain.utils.AppendingObjectOutputStream;

public class Block {

	private ObjectOutputStream file = null;
	private Signature signEng = null;
	private MessageDigest digestEng = null;
	private byte[] prevHash = null;
	private long numTransactions = 0;

	public Block(PrivateKey pk, long index, byte[] prevHash) {
		try {
			File f = new File("block_" + index + ".blk");
			if (f.exists()) {
				file = new AppendingObjectOutputStream(new FileOutputStream(f, true));
			} else {
				file = new ObjectOutputStream(new FileOutputStream(f, true));
				file.writeObject(prevHash);
				file.writeObject(String.valueOf(index).getBytes());
				file.writeObject(String.valueOf(5).getBytes());
			}
			signEng = Signature.getInstance("SHA256withRSA");
			signEng.initSign(pk);
			digestEng = MessageDigest.getInstance("SHA256");
			this.prevHash = prevHash;
		} catch (NoSuchAlgorithmException | InvalidKeyException | IOException e) {
			e.printStackTrace();
		}
	}

	public void writeTransaction(byte[] transaction, byte[] signature) {
		try {
			file.writeObject(transaction);
			digestEng.update(transaction);

			file.writeObject(signature);
			signEng.update(signature);
			digestEng.update(signature);

			numTransactions++;
			if (numTransactions == 5) {
				byte[] sign = signEng.sign();
				file.writeObject(sign);
				file.flush();
				file.close();
				digestEng.update(sign);
				prevHash = digestEng.digest();
			}
		} catch (IOException | SignatureException e) {
			e.printStackTrace();
		}
	}

	public void setNumTransactions(long numTransactions) {
		this.numTransactions = numTransactions;
	}

	public boolean isClosed() {
		return numTransactions == 5;
	}

	public byte[] getHash() {
		return prevHash;
	}

}
