package server;

import data.UsersData;
import domain.BankAccount;
import domain.BankAccountCatalog;
import exceptions.UserNotFoundException;

public class AuthenticationHandler {

	private BankAccountCatalog catalog = null;

	protected AuthenticationHandler(BankAccountCatalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * TODO
	 * 
	 * @param userID
	 * @param password
	 * @return
	 * @throws UserNotFoundException
	 */
	public Boolean validate(String userID, String password) throws UserNotFoundException {
		UsersData.User user = UsersData.getLine(userID);

		if (user == null) {
			throw new UserNotFoundException("Usuario nao existe, criando novo usuario...");
		}

		if (userID.equals(user.getUserID()) && password.equals(user.getPassword())) {
			return true;
		}

		return false;
	}

	/**
	 * TODO
	 * 
	 * @param userID   - identificacao do usuario.
	 * @param password - senha do usuario.
	 */
	public void createNewUser(String userID, String password) {
		catalog.add(userID, new BankAccount());
		UsersData.addLine(userID, password);
	}
}
