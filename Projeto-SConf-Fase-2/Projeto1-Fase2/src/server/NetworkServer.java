package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import data.IndPendingPaymentData;
import data.UsersData;
import domain.AuthenticationHandler;
import domain.BankAccount;
import domain.BankAccountCatalog;
import domain.BlockChain;
import domain.GroupCatalog;
import domain.RecoverBlockChain;
import exceptions.InvalidOperation;
import exceptions.UserNotFoundException;

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
	private BlockChain blockChain = null;

	public void init(int port, String cipherPass, String keyStore, String keyStorePass) {
		try {
			System.setProperty("javax.net.ssl.keyStore", SECURITY_FOLDER + keyStore);
			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
			ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
			SSLServerSocket sslServerSocket = (SSLServerSocket) ssf.createServerSocket(port);
			this.cipherPass = cipherPass;
			recoverDataToMemory();
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
						Object response = skel.invoke(userID, bankCatalog, groupCatalog, message, in, out, blockChain, cipherPass);
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

	private BankAccountCatalog recoverDataToMemory() {
		
		for (String userID : UsersData.getAllUsersIDs(cipherPass)) {
			bankCatalog.add(userID, new BankAccount());
		}
		
		List<String> individualPayments = IndPendingPaymentData.getAllIndividualPaymentIDs(cipherPass);
		List<String> transactions = recoverTransactions();
		List<String> ipTransactions = new ArrayList<String>();
		
		String[] splittedLine = null;
		
		for (String transaction : transactions) {
			splittedLine = transaction.split(" ");
			if (splittedLine[0].equals("p")) {
				ipTransactions.add(transaction);
			}
		}
		
		for (String t : ipTransactions) {
			splittedLine = t.split(" ", 3);
			String uniqueID = splittedLine[1];
			String otherUserID = splittedLine[2];
			
			for (String ip : individualPayments) {
				splittedLine = ip.split(":", 3);
				String uID = splittedLine[0];
				if (uID.equals(uniqueID)) {
					String amount = splittedLine[1];
					String userID = splittedLine[2];
					try {
						BankAccount ba = bankCatalog.getBankAccount(otherUserID);
						ba.recoverAddIndPaymentRequest(Double.valueOf(amount), otherUserID, userID, uniqueID);
					} catch (UserNotFoundException e) {}
				}
			}
		}
		
		skel.rerunOperations(bankCatalog, transactions, cipherPass);
		
		return bankCatalog;
	}

	private List<String> recoverTransactions() {
		List<String> transactions = RecoverBlockChain.recoverAllBlocks();
		long index = RecoverBlockChain.getIndex();
		long numTransactions = (long) (transactions.size() % 5);
		if (numTransactions == 0 && transactions.size() != 0) {
			index++;
		}
		blockChain = new BlockChain(index, RecoverBlockChain.getHash(), (long) (transactions.size() % 5));
		return transactions;
	}

}
