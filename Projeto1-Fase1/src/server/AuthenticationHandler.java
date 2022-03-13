package server;

import data.UsersData;
import domain.BankAccount;
import domain.BankAccountCatalog;
import exceptions.UserNotFoundException;

public class AuthenticationHandler {
	
	protected enum Authentication {
		VALIDATED,
		NOT_VALIDATED,
		USER_NOT_FOUND
	}

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
	public Authentication validate(String userID, String password) {
		UsersData.User user = UsersData.getLine(userID);

		if (user == null) {
			return Authentication.USER_NOT_FOUND;
		}

		if (userID.equals(user.getUserID()) && password.equals(user.getPassword())) {
			return Authentication.VALIDATED;
		}

		return Authentication.NOT_VALIDATED;
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
