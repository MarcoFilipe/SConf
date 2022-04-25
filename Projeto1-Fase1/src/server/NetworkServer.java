package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import domain.BankAccountCatalog;
import domain.GroupCatalog;
import exceptions.UserNotFoundException;

/**
 * Classe responsavel pela interacao com os clientes.
 * 
 * @author grupo 36.
 *
 */
public class NetworkServer {

	private static final String SECURITY_FOLDER = "security/";
	
	private Skeleton<Object> skel = new Skeleton<Object>();
	private BankAccountCatalog bankCatalog = new BankAccountCatalog();
	private GroupCatalog groupCatalog = new GroupCatalog();

	// TODO cipherPass UNUSED
	public void init(int port, String cipherPass, String keyStore, String keyStorePass) {
		try {
			System.setProperty("javax.net.ssl.keyStore", SECURITY_FOLDER + keyStore);
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
			ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
			SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);
			mainLoop(sslServerSocket);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	public void mainLoop(ServerSocket sslServerSocket) {
		System.out.println("Servidor conectado");
		while (true) {
			try {
				Socket inSoc = sslServerSocket.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
				// serverSoc.close(); *PORQUE NAO EXISTE COMANDO QUIT - VERIFICAR COM PROFESSOR*
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	private boolean authentication(String userID, String password) throws UserNotFoundException {
		AuthenticationHandler authHandler = new AuthenticationHandler(bankCatalog);

		switch (authHandler.validate(userID, password)) {
		case VALIDATED:
			return true;
		case NOT_VALIDATED:
			return false;
		default:
			authHandler.createNewUser(userID, password);
			throw new UserNotFoundException("Usuario nao existe, criando novo usuario...");
		}
	}

	public class ServerThread extends Thread {
		private Socket socket;

		private ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("SERVIDOR: Nova conexao");
		}

		public void run() {
			try {
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

				String userID;
				String password;

				userID = (String) in.readObject();
				password = (String) in.readObject();

				boolean authenticated = false;
				boolean userNotFoundMessageSent = false;
				try {
					authenticated = authentication(userID, password);
				} catch (UserNotFoundException e) {
					authenticated = true;
					userNotFoundMessageSent = true;
					out.writeObject(e.getMessage());
				}

				if (authenticated) {
					if (!userNotFoundMessageSent) {
						out.writeObject(true);
					}

					String message = null;

					while ((message = (String) in.readObject()) != null) {
						Object response = skel.invoke(userID, bankCatalog, groupCatalog, message);
						out.writeObject(response);
					}
				} else {
					out.writeObject(false);
				}

				System.out.println("Conexao encerrada");

				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}
