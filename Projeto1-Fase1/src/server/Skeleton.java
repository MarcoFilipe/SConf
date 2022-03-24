package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.BankAccount;
import domain.BankAccount.IndPaymentRequestInformation;
import domain.BankAccountCatalog;
import domain.Group;
import domain.GroupCatalog;
import exceptions.InsufficientBalanceException;
import exceptions.InvalidIdentifierException;
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
		QRCodeGenerator QR = new QRCodeGenerator();
		Group group = new Group();
		GroupCatalog groupList = new GroupCatalog();

		try {
			userBA = catalog.getBankAccount(userID);
		} catch (UserNotFoundException e) {
			resp = (E) Boolean.FALSE;
		}

		switch (splittedMessage[0]) {
		case "balance":
		case "b":
//			if (splittedMessage.length != 1) {
//				resp = (E) Boolean.FALSE;
//				break;
//			}

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
			} catch (UserNotFoundException | NumberFormatException | InvalidOperation e) {
				resp = (E) e.getMessage();
			}
			break;
		case "viewrequests":
		case "v":
			List<String> pendingIndPaymentsList = userBA.getIndPaymtRequestList();
			if (pendingIndPaymentsList == null) {
				resp = (E) "";
				break;
			}

			StringBuilder sb = new StringBuilder();
			int listSize = pendingIndPaymentsList.size();
			for (int i = 0; i < listSize; i++) {
				sb.append(pendingIndPaymentsList.get(i));
				if (!(i == listSize - 1)) {
					sb.append(",");
				}
			}
			resp = (E) sb.toString();
			break;
		case "payrequest":
		case "p":
			if (splittedMessage.length != 2) {
				resp = (E) Boolean.FALSE;
				break;
			}

			try {
				String uniqueID = splittedMessage[1];
				IndPaymentRequestInformation ipri = userBA.getIndPaymentRequestInf(uniqueID);
				amount = ipri.getAmount();
				otherUserID = ipri.getUserWhoRequestedPayment();
				otherUserBA = catalog.getBankAccount(otherUserID);
				userBA.removeAmount(amount);
				otherUserBA.addAmount(amount);
				userBA.removeIndPaymentRequest(uniqueID, userID);
				resp = (E) Boolean.TRUE;
			} catch (InvalidIdentifierException | UserNotFoundException | InvalidOperation
					| InsufficientBalanceException e) {
				resp = (E) e.getMessage();
			}
			break;
		case "obtainQRcode":
		case "o":
			if (splittedMessage.length != 2) {
				resp = (E) Boolean.FALSE;
				break;
			}
			
			try {
				
				amount = (double) Double.valueOf(splittedMessage[1]);
				QR.generateQRCode(userID, amount);
				resp = (E) Boolean.TRUE;
			} catch (Exception e) {
				resp = (E) e.getMessage();
			}
			break;
		case "confirmQRcode":
		case "c":
			if (splittedMessage.length != 2) {
				resp = (E) Boolean.FALSE;
				break;
			}
			
			try {
				
				String info = QR.readQRCode(splittedMessage[1]);
				if(info.equals("fileNotExists")) {
					resp = (E) "QR code nao existe";
					break;
				}
				
				String [] parts = info.split(":");
				String otherUser = parts[0];
				String mont = parts[1];
				
				if (userID.equals(otherUser)) {
					resp = (E) Boolean.FALSE;
					break;
				}
				
				otherUserBA = catalog.getBankAccount(otherUser);
				amount = (double) Double.valueOf(mont);
				userBA.removeAmount(amount);
				otherUserBA.addAmount(amount);
				resp = (E) Boolean.TRUE;
				
			} catch ( NumberFormatException | InvalidOperation e) {
				resp = (E) e.getMessage();
			} catch (InsufficientBalanceException | UserNotFoundException e) {
				resp = (E) e.getMessage();
			}
			break;
		case "newgroup":
		case "n":
			if (splittedMessage.length != 2) {
				resp = (E) Boolean.FALSE;
				break;
			}
			
			try {
				
			if(groupList.contains(splittedMessage[1])) {
				resp = (E) Boolean.FALSE;
				break;
			}
			
			else {
				group.add(userID);
				groupList.add(splittedMessage[1], group);
				resp = (E) Boolean.TRUE;
			}
			
			} catch (Exception e) {
				resp = (E) e.getMessage();
			}
			break;
		case "addu":
		case "a":
			if (splittedMessage.length != 3) {
				resp = (E) Boolean.FALSE;
				break;
			}
			
			try {
			
			otherUserID = splittedMessage[1];
							
			if(!groupList.contains(splittedMessage[2])) {
				resp = (E) Boolean.FALSE;
				break;
			}
			
			Group list = groupList.getGroup(splittedMessage[2]);
			
			if(!list.isOwner(userID) || list.contains(otherUserID))	{
				resp = (E) Boolean.FALSE;
				break;
				
			}else {
				list.add(otherUserID);
				resp = (E) Boolean.TRUE;
			}
			
			} catch (Exception e) {
				resp = (E) e.getMessage();
			}
			break;
			
		case "groups":
		case "g":
			if (splittedMessage.length != 1) {
				resp = (E) Boolean.FALSE;
				break;
			}
			
			try {
				
			
			} catch (Exception e) {
				resp = (E) e.getMessage();
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
