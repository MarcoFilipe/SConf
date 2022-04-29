package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import data.utils.FileSecurity;

public class HistoryData {
	private static final String HISTORY_FILE_PATHNAME = "history.cif";
	private static HistoryData history_instance = null;
	//private static File file = null;

	/*protected HistoryData() {
		createFile();  ////ALTERAR
	}*/

	public static HistoryData getInstance() {
		if (history_instance == null) {
			history_instance = new HistoryData();
		}
		return history_instance;
	}

	public synchronized void addHistoric(String cipherPass, String groupID, List<String> uniquePaymentIDs) {
		
		File file = createFile(cipherPass);
		File temp = FileSecurity.decipherFile(file, cipherPass);
		
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp, true))) {
			bw.write(groupID);
			bw.flush();
			for (String paymentID : uniquePaymentIDs) {
				bw.write(":" + paymentID);
				bw.flush();
			}
			bw.write(";");
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private synchronized File createFile(String cipherPass) {
		File file = null;
		try {
			file = new File(HISTORY_FILE_PATHNAME);

			if (!file.exists()) {
				File temp = File.createTempFile("users", ".temp");
				file.createNewFile();
				FileSecurity.cipherFile(file, temp, cipherPass);
				file = new File(HISTORY_FILE_PATHNAME);
				System.out.println("Ficheiro " + HISTORY_FILE_PATHNAME + " criado");
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return file;
	}
}
