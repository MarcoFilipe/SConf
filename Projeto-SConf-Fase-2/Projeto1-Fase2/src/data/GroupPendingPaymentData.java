package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import data.utils.FileSecurity;

public class GroupPendingPaymentData {
	private static final String GROUP_PAYMENT_REQUEST_FILE_PATHNAME = "group_payment_request.cif";
	private static GroupPendingPaymentData groupPaymentRequestData_instance = null;
	//private static File file = null;
	
	/*protected GroupPendingPaymentData() {
		createIndPaymentRequestFile();  //ALTERAR!!!!
	}*/

	public static GroupPendingPaymentData getInstance() {
		if (groupPaymentRequestData_instance == null) {
			groupPaymentRequestData_instance = new GroupPendingPaymentData();
		}
		return groupPaymentRequestData_instance;
	}

	public synchronized String getLine(String cipherPass, String uniqueID) {
		String currentUniqueID = null;
		String line = null;
		
		File file = createIndPaymentRequestFile(cipherPass);
		File temp = FileSecurity.decipherFile(file, cipherPass);

		try (BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(temp)))) {
			while ((line = br.readLine()) != null) {
				String[] lineSplitted = line.split(":", 2);
				currentUniqueID = lineSplitted[0];
				if (currentUniqueID.equals(uniqueID)) {
					temp.deleteOnExit();
					return line;
				}
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	public synchronized void addLine(String cipherPass, String uniqueID, double amount) {
		
		File file = createIndPaymentRequestFile(cipherPass);
		File temp = FileSecurity.decipherFile(file, cipherPass);
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp, true))) {
			bw.write(uniqueID + ":" + amount);
			bw.flush();
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		FileSecurity.cipherFile(file, temp, cipherPass);
	}

	private synchronized File createIndPaymentRequestFile(String cipherPass) {
		File file = null;
		try {
			file = new File(GROUP_PAYMENT_REQUEST_FILE_PATHNAME);

			if (!file.exists()) {
				File temp = File.createTempFile("users", ".temp");
				file.createNewFile();
				FileSecurity.cipherFile(file, temp, cipherPass);
				file = new File(GROUP_PAYMENT_REQUEST_FILE_PATHNAME);
				System.out.println("Ficheiro " + GROUP_PAYMENT_REQUEST_FILE_PATHNAME + " criado");
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		return file;
	}
}
