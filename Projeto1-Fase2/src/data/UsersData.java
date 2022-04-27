package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe responsavel por ler e manipular os dados do ficheiro "users_inf.txt",
 * que possui as informacoes <userID>:<certificatePath> de cada um dos usuarios.
 * 
 * @author grupo 36.
 *
 */
public class UsersData {

	private static final String USERSINF_FILE_PATHNAME = "users.txt";
	private static File file = createOrGetUsersFile();

	public synchronized static User getLine(String cipherPass, String userID) {
		String currentUserID = null;
		String line = null;

		try (BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(file)))) {
			while ((line = br.readLine()) != null) {
				String[] lineSplitted = line.split(":", 2);
				currentUserID = lineSplitted[0];
				if (currentUserID.equals(userID)) {
					return new User(lineSplitted[0], lineSplitted[1]);
				}
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return null;
	}

	public synchronized static void addLine(String cipherPass, String userID, String certificatePath) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERSINF_FILE_PATHNAME, true))) {
			bw.write(userID + ":" + certificatePath);
			bw.flush();
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private synchronized static File createOrGetUsersFile() {
		File file = null;
		try {
			file = new File(USERSINF_FILE_PATHNAME);

			if (!file.exists()) {
				file.createNewFile();
				System.out.println("Ficheiro " + USERSINF_FILE_PATHNAME + " criado");
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		return file;
	}

	public static class User {
		private String userID;
		private String certificatePath;

		public User(String userID, String certificatePath) {
			this.userID = userID;
			this.certificatePath = certificatePath;
		}

		public String getUserID() {
			return userID;
		}

		public String getCertificatePath() {
			return certificatePath;
		}
	}
}
