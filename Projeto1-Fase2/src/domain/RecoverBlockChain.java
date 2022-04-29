package domain;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class RecoverBlockChain {

	private static long index = 1;
	private static byte[] hash = new byte[32];
	private static byte[] prevHash = hash.clone();

	public static List<String> recoverAllBlocks() {
		List<String> transactions = new ArrayList<String>();
		List<String> blockTransactions = null;

		while ((blockTransactions = readBlock()) != null) {
			transactions.addAll(blockTransactions);
			index++;
		}
		if (index > 1) {
			index--;
		}
		return transactions;
	}

	public static long getIndex() {
		return index;
	}

	public static byte[] getHash() {
		return prevHash;
	}

	private static List<String> readBlock() {
		List<String> transactions = new ArrayList<String>();
		FileInputStream fis;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream("block_" + index + ".blk");
			ois = new ObjectInputStream(fis);

			hash = (byte[]) ois.readObject();
			ois.readObject();
			ois.readObject();

			try {
				for (;;) {
					String transaction = new String((byte[]) ois.readObject());
					byte[] signature = (byte[]) ois.readObject();
					// TODO verify signature
					transactions.add(transaction);
					if (transactions.size() == 5) {
						prevHash = hash.clone();
					}
				}
			} catch (EOFException e) {
				return transactions;
			}

		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException | NullPointerException e) {
				return null;
			}
		}
		return null;
	}

}
