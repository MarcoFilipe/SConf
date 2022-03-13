package server;

import domain.BankAccount;
import domain.BankAccountCatalog;
import exceptions.InsufficientBalanceException;
import exceptions.UserNotFoundException;

@SuppressWarnings("unchecked")
public class Skeleton<E> {

	public E invoke(String userID, BankAccountCatalog catalog, String message) {
		E resp = null;
		String[] splittedMessage = message.split(" ", 3);

		BankAccount userBA = null;
		try {
			userBA = catalog.getBankAccount(userID);
		} catch (UserNotFoundException e) {
			resp = (E) Boolean.FALSE;
		}

		switch (splittedMessage[0]) {
		case "balance":
		case "b":
			resp = (E) String.valueOf(userBA.balance());
			break;
		case "makepayment":
		case "m":
			if (splittedMessage.length == 3) {
				String otherUserID = splittedMessage[1];
				BankAccount otherUserBA = null;
				try {
					otherUserBA = catalog.getBankAccount(otherUserID);
					double amount = (double) Double.valueOf(splittedMessage[2]);
					userBA.removeAmount(amount);
					otherUserBA.addAmount(amount);
					resp = (E) Boolean.TRUE;
				} catch (InsufficientBalanceException | UserNotFoundException e) {
					resp = (E) e.getMessage();
				} catch (Exception e) {
					/* Do nothing */
				}
			} else {
				resp = (E) Boolean.FALSE;
			}
			break;
		// TODO
		default:
			String err = "Comando nao existe";
			resp = (E) err;
			break;
		}

		return resp;
	}

}
