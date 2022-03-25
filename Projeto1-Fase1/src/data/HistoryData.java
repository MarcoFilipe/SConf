package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HistoryData {
	private static final String HISTORY_FILE_PATHNAME = "history.txt";
	private static HistoryData history_instance = null;
	private static File file = null;

	protected HistoryData() {
		createFile();
	}

	public static HistoryData getInstance() {
		if (history_instance == null) {
			history_instance = new HistoryData();
		}
		return history_instance;
	}

	public synchronized void addHistoric(String groupID, List<String> uniquePaymentIDs) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(HISTORY_FILE_PATHNAME, true))) {
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

	private synchronized void createFile() {
		try {
			file = new File(HISTORY_FILE_PATHNAME);

			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Ficheiro " + HISTORY_FILE_PATHNAME + " criado");
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
