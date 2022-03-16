package server;

import domain.BankAccount;
import domain.BankAccountCatalog;
import exceptions.InsufficientBalanceException;
import exceptions.InvalidOperation;
import exceptions.UserNotFoundException;

/**
 * 
 * Classe responsavel pela logica de todos os comandos.
 * 
 * @author grupo 36.
 *
 * @param <E> Objeto generico.
 */
@SuppressWarnings("unchecked")
public class Skeleton<E> {

	public E invoke(String userID, BankAccountCatalog catalog, String message) {
		E resp = null;
		String[] splittedMessage = message.split(" ", 3);

		BankAccount userBA = null;
		BankAccount otherUserBA = null;
		String otherUserID = null;
		double amount;

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
			if (splittedMessage.length != 3) {
				resp = (E) Boolean.FALSE;
				break;
			}

			otherUserID = splittedMessage[1];

			if (userID.equals(otherUserID)) {
				resp = (E) Boolean.FALSE;
				break;
			}

			try {
				otherUserBA = catalog.getBankAccount(otherUserID);
				amount = (double) Double.valueOf(splittedMessage[2]);
				userBA.removeAmount(amount);
				otherUserBA.addAmount(amount);
				resp = (E) Boolean.TRUE;
			} catch (InsufficientBalanceException | UserNotFoundException e) {
				resp = (E) e.getMessage();
			} catch (NumberFormatException | InvalidOperation e) {
				resp = (E) Boolean.FALSE;
			}
			break;
		case "requestpayment":
		case "r":
			if (splittedMessage.length != 3) {
				resp = (E) Boolean.FALSE;
				break;
			}

			otherUserID = splittedMessage[1];

			if (userID.equals(otherUserID)) {
				resp = (E) Boolean.FALSE;
				break;
			}

			try {
				otherUserBA = catalog.getBankAccount(otherUserID);
				amount = (double) Double.valueOf(splittedMessage[2]);
				otherUserBA.addIndPaymentRequest(otherUserID, userID, amount);
				resp = (E) Boolean.TRUE;
			} catch (UserNotFoundException e) {
				resp = (E) e.getMessage();
			} catch (NumberFormatException e) {
				resp = (E) Boolean.FALSE;
			}

			break;
		default:
			String err = "Comando nao existe";
			resp = (E) err;
			break;
		}

		return resp;
	}
}
