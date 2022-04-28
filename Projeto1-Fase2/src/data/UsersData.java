package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.utils.FileSecurity;

/**
 * Classe responsavel por ler e manipular os dados do ficheiro "users_inf.txt",
 * que possui as informacoes <userID>:<certificatePath> de cada um dos usuarios.
 * 
 * @author grupo 36.
 *
 */
public class UsersData {

	private static final String USERS_FILE_PATHNAME = "users.cif";

	public static List<String> getAllUsersIDs(String cipherPass) {
		File file = createOrGetUsersFile(cipherPass);
		File temp = FileSecurity.decipherFile(file, cipherPass);

		List<String> usersIDs = new ArrayList<String>();
		String line = null;

		try (BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(temp)))) {
			while ((line = br.readLine()) != null) {
				String[] lineSplitted = line.split(":", 2);
				usersIDs.add(lineSplitted[0]);
			}
			br.close();
		} catch (IOException e) {
			temp.deleteOnExit();
			System.err.println(e.getMessage());
		}
		temp.delete();
		return usersIDs;
	}

	public synchronized static User getLine(String cipherPass, String userID) {
		String currentUserID = null;
		String line = null;

		File file = createOrGetUsersFile(cipherPass);
		File temp = FileSecurity.decipherFile(file, cipherPass);

		try (BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(temp)))) {
			while ((line = br.readLine()) != null) {
				String[] lineSplitted = line.split(":", 2);
				currentUserID = lineSplitted[0];
				if (currentUserID.equals(userID)) {
					temp.deleteOnExit();
					return new User(lineSplitted[0], lineSplitted[1]);
				}
			}
			br.close();
		} catch (IOException e) {
			temp.deleteOnExit();
			System.err.println(e.getMessage());
		}
		temp.delete();
		return null;
	}

	public synchronized static void addLine(String cipherPass, String userID, String certificatePath) {

		File file = createOrGetUsersFile(cipherPass);
		File temp = FileSecurity.decipherFile(file, cipherPass);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp, true))) {
			bw.write(userID + ":" + certificatePath);
			bw.flush();
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		FileSecurity.cipherFile(file, temp, cipherPass);
	}

	private synchronized static File createOrGetUsersFile(String cipherPass) {
		File file = null;
		try {
			file = new File(USERS_FILE_PATHNAME);

			if (!file.exists()) {
				File temp = File.createTempFile("users", ".temp");
				file.createNewFile();
				FileSecurity.cipherFile(file, temp, cipherPass);
				file = new File(USERS_FILE_PATHNAME);
				System.out.println("Ficheiro " + USERS_FILE_PATHNAME + " criado.");
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
