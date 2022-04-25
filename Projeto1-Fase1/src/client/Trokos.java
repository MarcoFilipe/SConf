package client;

public class Trokos {

	private static int port = 45678;
	private static String ipHostName = null;
	private static String trustStore = null;
	private static String keyStore = null;
	private static String keyStorePass = null;
	private static String userID = null;

	public static void main(String[] args) {

		if (!validateArgs(args)) {
			System.exit(-1);
		}

		NetworkClient network = new NetworkClient();

		try {
			network.connect(ipHostName, port, trustStore, keyStore, keyStorePass, userID);
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}

	}

	/**
	 * Metodo que valida os argumentos recebidos como parametro pela funcao main.
	 * 
	 * @param args - argumentos.
	 * @return - true: caso os argumentos sejam validos, false: caso os argumentos
	 *         sejam invalidos.
	 */
	private static boolean validateArgs(String[] args) {

		if (args.length == 5) {
			try {
				String splittedStr[] = args[0].split(":", 2);
				if (splittedStr.length == 2) {
					port = Integer.parseInt(splittedStr[1]);
				}
				assignValues(splittedStr[0], args[1], args[2], args[3], args[4]);
				return true;
			} catch (NumberFormatException e) {
				System.err.println(e.toString());
			}
		}

		System.err.println(
				"A execução do cliente deve ser feita da seguinte forma: Trokos <serverAddress> <truststore> <keystore> <password-keystore> <userID>");
		return false;
	}

	private static void assignValues(String iphn, String ts, String ks, String ksp, String uID) {
		ipHostName = iphn;
		trustStore = ts;
		keyStore = ks;
		keyStorePass = ksp;
		userID = uID;
	}

}
