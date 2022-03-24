package client;

import java.util.Scanner;

public class Trokos {

	private static int PORT = 45678;
	private static String IPHOSTNAME = null;
	private static String USERID = null;
	private static String PASSWORD = null;

	public static void main(String[] args) {

		if (!validateArgs(args)) {
			System.exit(-1);
		}

		NetworkClient network = new NetworkClient();

		try {
			network.connect(IPHOSTNAME, PORT, USERID, PASSWORD);
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
		if (args.length < 2) {
			System.err.println(
					"A execucao do cliente deve ser feita da seguinte forma: Trokos <serverAddress> <userID> [password]");
			return false;
		}

		String[] serverAddress = args[0].split(":", 2);
		IPHOSTNAME = serverAddress[0];
		USERID = args[1];

		switch (args.length) {
		case 2:
			Scanner sc = new Scanner(System.in);
			System.out.print("Insira a senha: ");
			System.out.flush();
			PASSWORD = sc.nextLine();
			sc.close();
			break;
		case 3:
			PASSWORD = args[2];
			break;
		default:
			System.err.println(
					"A execucao do cliente deve ser feita da seguinte forma: Trokos <serverAddress> <userID> [password]");
			return false;
		}

		switch (serverAddress.length) {
		case 1:
			break;
		case 2:
			try {
				PORT = Integer.parseInt(serverAddress[1]);
			} catch (NumberFormatException e) {
				System.err.println(e.toString());
				return false;
			}
			break;
		default:
			System.err.println("O serverAddress deve ter o seguinte formato: <IP/hostname>[:Port]");
			return false;
		}

		return true;
	}

}
