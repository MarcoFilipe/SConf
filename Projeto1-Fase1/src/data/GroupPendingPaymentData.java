package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GroupPendingPaymentData {
	private static final String GROUP_PAYMENT_REQUEST_FILE_PATHNAME = "group_payment_request.txt";
	private static GroupPendingPaymentData groupPaymentRequestData_instance = null;
	private static File file = null;

	protected GroupPendingPaymentData() {
		createIndPaymentRequestFile();
	}

	public static GroupPendingPaymentData getInstance() {
		if (groupPaymentRequestData_instance == null) {
			groupPaymentRequestData_instance = new GroupPendingPaymentData();
		}
		return groupPaymentRequestData_instance;
	}

	public synchronized String getLine(String uniqueID) {
		String currentUniqueID = null;
		String line = null;

		try (BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(file)))) {
			while ((line = br.readLine()) != null) {
				String[] lineSplitted = line.split(":", 2);
				currentUniqueID = lineSplitted[0];
				if (currentUniqueID.equals(uniqueID)) {
					return line;
				}
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	public synchronized void addLine(String uniqueID, double amount) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(GROUP_PAYMENT_REQUEST_FILE_PATHNAME, true))) {
			bw.write(uniqueID + ":" + amount);
			bw.flush();
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private synchronized void createIndPaymentRequestFile() {
		try {
			file = new File(GROUP_PAYMENT_REQUEST_FILE_PATHNAME);

			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Ficheiro " + GROUP_PAYMENT_REQUEST_FILE_PATHNAME + " criado");
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
