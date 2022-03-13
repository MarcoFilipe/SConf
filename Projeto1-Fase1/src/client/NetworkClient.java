package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class NetworkClient {

	public void connect(String ipHostname, int port, String userID, String password) throws ClassNotFoundException {
		Socket clientSocket = null;
		ObjectInputStream in = null;
		ObjectOutputStream out = null;

		try {
			clientSocket = new Socket(ipHostname, port);
			in = new ObjectInputStream(clientSocket.getInputStream());
			out = new ObjectOutputStream(clientSocket.getOutputStream());

			if (authentication(clientSocket, out, in, userID, password)) {
				mainLoop(clientSocket, out, in);
				System.out.println("Autenticado");
			} else {
				System.out.println("Não autenticado");
				System.exit(0);
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private Boolean authentication(Socket socket, ObjectOutputStream out, ObjectInputStream in, String userID,
			String password) throws ClassNotFoundException, IOException {

		networkSend(socket, out, userID);
		networkSend(socket, out, password);

		Boolean response = (Boolean) networkReceive(socket, in);

		return response;
	}

	private void networkSend(Socket socket, ObjectOutputStream out, Object message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private Object networkReceive(Socket socket, ObjectInputStream in) throws ClassNotFoundException, IOException {
		Object response = in.readObject();
		return response;
	}

	public void mainLoop(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		Scanner sc = new Scanner(System.in);
		String[] SplittedLine = null;
		String line = null;
		String strResp = null;
		Boolean boolResp = null;

		System.out.print("Comando: ");
		System.out.flush();

		while (sc.hasNextLine()) {
			line = sc.nextLine();
			SplittedLine = line.split(" ", 3);

			networkSend(clientSocket, out, line);
			try {
				switch (SplittedLine[0]) {
				case "balance":
				case "b":
					strResp = (String) networkReceive(clientSocket, in);
					System.out.println("Valor atual do saldo da sua conta: " + strResp);
					break;
				case "makepayment":
				case "m":
					Object resp = networkReceive(clientSocket, in);

					if (resp.getClass() == Boolean.class) {
						boolResp = (Boolean) resp;
						if (boolResp) {
							System.out.println("Pagamento efetuado com sucesso");
						}
					} else if (resp.getClass() == String.class) {
						strResp = (String) resp;
					}

					break;
				// TODO
				default:
					/* Do nothing */
					break;
				}
			} catch (NullPointerException | ClassCastException e) {
				/* Do nothing */
			}
			System.out.print("Comando: ");
			System.out.flush();
		}
		sc.close();
	}

}
