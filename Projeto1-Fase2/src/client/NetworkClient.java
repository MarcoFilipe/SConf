package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class NetworkClient {

	private static final String SECURITY_FOLDER = "Projeto1-Fase2/security/";

	/**
	 * Metodo que conecta o cliente ao servidor. Caso a conexao seja bem sucedida,
	 * aparece uma mensagem indicando que o cliente foi autenticado, caso o
	 * contrario, aparece uma mensagem indicando que o cliente nao foi autenticado e
	 * a aplicacao termina.
	 * 
	 * @param ipHostName   O endereco ip ou hostname. Formato: <IP/hostname>
	 * @param port         A porta
	 * @param trustStore   A truststore
	 * @param keyStore     A keystore
	 * @param keyStorePass A palavra passe da keystore
	 * @param userID       o identificador do usuario
	 */
	public void connect(String ipHostName, int port, String trustStore, String keyStore, String keyStorePass,
			String userID) throws ClassNotFoundException {
		try {
			System.setProperty("javax.net.ssl.trustStore", SECURITY_FOLDER + trustStore);
			SocketFactory sf = SSLSocketFactory.getDefault();
			SSLSocket sslClientSocket = (SSLSocket) sf.createSocket(ipHostName, port);
			ObjectInputStream in = new ObjectInputStream(sslClientSocket.getInputStream());
			ObjectOutputStream out = new ObjectOutputStream(sslClientSocket.getOutputStream());

			// TODO
			if (authentication(sslClientSocket, out, in, userID, null)) {
				System.out.println("Autenticado");
				mainLoop(sslClientSocket, out, in);
			} else {
				System.err.println("Não autenticado");
				System.exit(0);
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private boolean authentication(Socket socket, ObjectOutputStream out, ObjectInputStream in, String userID,
			String password) throws ClassNotFoundException, IOException {

		networkSend(socket, out, userID);
		networkSend(socket, out, password);

		Object resp = in.readObject();

		if (resp instanceof String) {
			System.err.println(resp);
			return true;
		} else if (resp instanceof Boolean) {
			if ((Boolean) resp) {
				return true;
			}
		}
		return false;
	}

	private void networkSend(Socket socket, ObjectOutputStream out, Object message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void mainLoop(Socket clientSocket, ObjectOutputStream out, ObjectInputStream in)
			throws ClassNotFoundException, IOException {
		Scanner sc = new Scanner(System.in);
		String[] SplittedLine = null;
		String line = null;
		String strResp = null;
		Object resp = null;

		System.out.print("Comando: ");
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			SplittedLine = line.split(" ", 3);

			networkSend(clientSocket, out, line);
			try {
				switch (SplittedLine[0]) {
				case "balance":
				case "b":
					strResp = (String) in.readObject();
					System.out.println("Valor atual do saldo da sua conta: " + strResp + ".");
					break;
				case "makepayment":
				case "m":
					resp = in.readObject();
					outputMessage(resp, "Pagamento efetuado com sucesso.");
					break;
				case "requestpayment":
				case "r":
					resp = in.readObject();
					outputMessage(resp, "Envio do pedido de pagamento efetuado com sucesso.");
					break;
				case "viewrequests":
				case "v":
					resp = (String) in.readObject();
					System.out.print("Pedidos de pagamento pendentes: ");
					if (((String) resp).length() == 0) {
						System.out.println("Nao ha pagamentos pendentes.");
						break;
					}
					System.out.println();
					String[] splittedResp = ((String) resp).split(",");
					String[] splittedPaymentInf = null;
					for (String paymentInf : splittedResp) {
						splittedPaymentInf = (paymentInf).split(" ", 3);
						System.out.println("[ID do pedido]: " + splittedPaymentInf[0]);
						System.out.println("Valor: " + splittedPaymentInf[1]);
						System.out.println("Usuario que fez o pedido: " + splittedPaymentInf[2]);
					}
					break;
				case "payrequest":
				case "p":
					resp = in.readObject();
					outputMessage(resp, "Pagamento efetuado com sucesso.");
					break;
				case "obtainQRcode":
				case "o":
					resp = in.readObject();
					System.out.println("Codigo QR Code: " + (String) resp);
					break;
				case "confirmQRcode":
				case "c":
					resp = in.readObject();
					outputMessage(resp, "Pagamento QR code efetuado com sucesso.");
					break;
				case "newgroup":
				case "n":
					resp = in.readObject();
					outputMessage(resp, "Grupo criado com sucesso.");
					break;
				case "addu":
				case "a":
					resp = in.readObject();
					outputMessage(resp, "Utilizador adicionado com sucesso.");
					break;
				case "groups":
				case "g":
					resp = (String) in.readObject();
					System.out.println((String) resp);
					break;
				case "dividepayment":
				case "d":
					resp = in.readObject();
					outputMessage(resp, "Pagamento dividido com sucesso.");
					break;
				case "statuspayments":
				case "s":
					resp = in.readObject();
					System.out.println((String) resp);
					break;
				case "history":
				case "h":
					resp = in.readObject();
					System.out.println((String) resp);
					break;
				default:
					System.err.println((String) in.readObject());
					break;
				}
			} catch (NullPointerException | ClassCastException e) {
				System.err.println("Ocorreu um erro inesperado.");
			}
			System.out.print("Comando: ");
		}
		sc.close();
	}

	private void outputMessage(Object resp, String message) throws ClassNotFoundException, IOException {
		boolean boolResp = false;

		if (resp.getClass() == Boolean.class) {
			boolResp = (Boolean) resp;
			if (boolResp) {
				System.out.println(message);
			} else {
				System.err.println("Operacao nao concluida.");
			}
		} else if (resp.getClass() == String.class) {
			System.err.println((String) resp);
		}
	}

}
