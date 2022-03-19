package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe responsavel por manipular os dados do ficheiro
 * "ind_payment_request.txt", que possui as informacoes de todos os pedidos de
 * pagamento individuais no formato:
 * <uniqueID>:<amount>:<userWhoRequestedPayment>.
 * 
 * @author grupo 36.
 *
 */
public class IndPendingPaymentData {

	private static final String IND_PAYMENT_REQUEST_FILE_PATHNAME = "ind_payment_request.txt";
	private static IndPendingPaymentData indPaymentRequestData_instance = null;
	private static File file = null;

	protected IndPendingPaymentData() {
		createIndPaymentRequestFile();
	}

	public static IndPendingPaymentData getInstance() {
		if (indPaymentRequestData_instance == null) {
			indPaymentRequestData_instance = new IndPendingPaymentData();
		}
		return indPaymentRequestData_instance;
	}

	public synchronized String getLine(String uniqueID) {
		String currentUniqueID = null;
		String line = null;

		try (BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(file)))) {
			while ((line = br.readLine()) != null) {
				String[] lineSplitted = line.split(":", 3);
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

	public synchronized void addLine(String uniqueID, double amount, String userWhoRequestedPayment) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(IND_PAYMENT_REQUEST_FILE_PATHNAME, true))) {
			bw.write(uniqueID + ":" + amount + ":" + userWhoRequestedPayment);
			bw.flush();
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private synchronized void createIndPaymentRequestFile() {
		try {
			file = new File(IND_PAYMENT_REQUEST_FILE_PATHNAME);

			if (!file.exists()) {
				file.createNewFile();
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
