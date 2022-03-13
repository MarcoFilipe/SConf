package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import domain.BankAccountCatalog;
import exceptions.UserNotFoundException;

public class NetworkServer {

	private Skeleton<Object> skel = new Skeleton<Object>();
	private BankAccountCatalog catalog = new BankAccountCatalog();

	public void init(int port) {
		try {
			ServerSocket serverSoc = new ServerSocket(port);
			mainLoop(serverSoc);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	public void mainLoop(ServerSocket serverSoc) {
		System.out.println("Servidor conectado");
		while (true) {
			try {
				Socket inSoc = serverSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
				// serverSoc.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	private boolean authentication(String userID, String password) {
		AuthenticationHandler authHandler = new AuthenticationHandler(catalog);

		try {
			if (authHandler.validate(userID, password)) {
				return true;
			} else {
				return false;
			}
		} catch (UserNotFoundException e) {
			System.err.println(e.getMessage());
			authHandler.createNewUser(userID, password);
			return true;
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

				boolean authenticated = authentication(userID, password);

				if (authenticated) {
					System.out.println("Cliente conectado");
					out.writeObject(true);

					String message = null;

					while ((message = (String) in.readObject()) != null) {

						Object response = skel.invoke(userID, catalog, message);
						out.writeObject(response);

					}
				} else {
					System.out.print("Credenciais incorretas. ");
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
