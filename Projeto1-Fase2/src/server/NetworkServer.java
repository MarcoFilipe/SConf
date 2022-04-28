package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import data.UsersData;
import domain.AuthenticationHandler;
import domain.BankAccount;
import domain.BankAccountCatalog;
import domain.GroupCatalog;

/**
 * Classe responsavel pela interacao com os clientes.
 * 
 * @author grupo 36.
 *
 */
public class NetworkServer {

	private static final String SECURITY_FOLDER = "Projeto1-Fase2/security/";
	private static final String CER = ".cer";

	private Skeleton<Object> skel = new Skeleton<Object>();
	private BankAccountCatalog bankCatalog = new BankAccountCatalog();
	private GroupCatalog groupCatalog = new GroupCatalog();
	private String cipherPass;

	public void init(int port, String cipherPass, String keyStore, String keyStorePass) {
		try {
			System.setProperty("javax.net.ssl.keyStore", SECURITY_FOLDER + keyStore);
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
			ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
			SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);
			this.cipherPass = cipherPass;
			recoverBankAccountCatalogToMemory();
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

				String userID = (String) in.readObject();

				if (authentication(in, out, userID)) {
					out.writeObject(true);

					String message = null;

					while ((message = (String) in.readObject()) != null) {
						Object response = skel.invoke(userID, bankCatalog, groupCatalog, message, in, out);
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

	private boolean authentication(ObjectInputStream in, ObjectOutputStream out, String userID)
			throws IOException, ClassNotFoundException {
		AuthenticationHandler authHandler = new AuthenticationHandler(bankCatalog);
		int flag = 0;
		if (authHandler.isRegistered(cipherPass, userID)) {
			flag = 1;
		}

		out.writeObject(Long.valueOf(authHandler.getNonce()));
		out.writeObject(Integer.valueOf(flag));

		if (flag == 0) {
			register(in, out, authHandler, userID);
			return true;
		} else if (flag == 1) {
			byte[] signedNonce = (byte[]) in.readObject();
			return authHandler.verifyNonce(cipherPass, userID, signedNonce);
		}
		return false;
	}

	private void register(ObjectInputStream in, ObjectOutputStream out, AuthenticationHandler authHandler,
			String userID) throws ClassNotFoundException, IOException {
		long nonce = (Long) in.readObject();
		byte[] signedNonce = (byte[]) in.readObject();
		Certificate certificate = (Certificate) in.readObject();

		if (authHandler.verifyNonce(nonce, signedNonce, certificate)) {
			try {
				byte[] certBytes = certificate.getEncoded();
				authHandler.registerNewUser(cipherPass, userID, certBytes, SECURITY_FOLDER + userID + CER);
			} catch (CertificateEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	// ainda nao recupera o balance
	private BankAccountCatalog recoverBankAccountCatalogToMemory() {
		for (String userID : UsersData.getAllUsersIDs(cipherPass)) {
			bankCatalog.add(userID, new BankAccount());
		}
		return bankCatalog;
	}

}
