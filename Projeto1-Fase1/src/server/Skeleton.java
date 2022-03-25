package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import domain.BankAccount;
import domain.BankAccount.GroupPaymentReqInformation;
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

	public E invoke(String userID, BankAccountCatalog bankCatalog, GroupCatalog groupCatalog, String message) {
		E resp = null;
		String[] splittedMessage = message.split(" ", 3);

		BankAccount userBA = null;
		BankAccount otherUserBA = null;
		String otherUserID = null;
		double amount;
		QRCodeGenerator QR = new QRCodeGenerator();
		Group group = new Group();

		try {
			userBA = bankCatalog.getBankAccount(userID);
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
				otherUserBA = bankCatalog.getBankAccount(otherUserID);
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
				otherUserBA = bankCatalog.getBankAccount(otherUserID);
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
				otherUserBA = bankCatalog.getBankAccount(otherUserID);
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
				resp = (E) QR.generateQRCode(userID, amount);
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
				if (info.equals("fileNotExists")) {
					resp = (E) "QR code nao existe";
					break;
				}

				String[] parts = info.split("_");
				String otherUser = parts[0];
				String mont = parts[1];

				if (userID.equals(otherUser)) {
					resp = (E) Boolean.FALSE;
					break;
				}

				otherUserBA = bankCatalog.getBankAccount(otherUser);
				amount = (double) Double.valueOf(mont);
				userBA.removeAmount(amount);
				otherUserBA.addAmount(amount);
				resp = (E) Boolean.TRUE;

			} catch (NumberFormatException | InvalidOperation e) {
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

				bankCatalog.getBankAccount(userID);

				if (groupCatalog.contains(splittedMessage[1])) {
					resp = (E) Boolean.FALSE;
					break;
				}

				else {
					group.add(userID);
					groupCatalog.add(splittedMessage[1], group);
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
				bankCatalog.getBankAccount(otherUserID);

				if (!groupCatalog.contains(splittedMessage[2])) {
					resp = (E) Boolean.FALSE;
					break;
				}

				Group list = groupCatalog.getGroup(splittedMessage[2]);

				if (!list.isOwner(userID) || list.contains(otherUserID)) {
					resp = (E) Boolean.FALSE;
					break;

				} else {
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
				String str1 = "";
				String str2 = "";
				HashMap<String, Group> map = groupCatalog.getGroupList();
				for (String key : map.keySet()) {
					if (map.get(key).isOwner(userID)) {
						str1 = str1 + System.lineSeparator() + key;
					}
				}

				for (String key : map.keySet()) {
					if (map.get(key).contains(userID) && !map.get(key).isOwner(userID)) {
						str2 = str2 + System.lineSeparator() + key;
					}
				}
				str1 = "Grupos (Dono): " + str1 + System.lineSeparator();
				str2 = System.lineSeparator() + "Grupos (Participante): " + str2 + System.lineSeparator();
				String str = str1 + str2;
				resp = (E) str;
			} catch (Exception e) {
				resp = (E) e.getMessage();
			}
			break;

		case "dividepayment":
		case "d":
			if (splittedMessage.length != 3) {
				resp = (E) Boolean.FALSE;
				break;
			}
			try {
				String groupID = splittedMessage[1];
				amount = (double) Double.valueOf(splittedMessage[2]);

				if (!groupCatalog.contains(groupID)) {
					resp = (E) Boolean.FALSE;
					break;
				}

				group = groupCatalog.getGroup(groupID);

				if (!group.isOwner(userID)) {
					resp = (E) Boolean.FALSE;
					break;
				}

				List<String> groupMembers = group.getGroupMembers();
				List<String> pendPayments = new ArrayList<String>();
				double amountPerID = amount / (groupMembers.size());
				for (int i = 0; i < groupMembers.size(); i++) {
					otherUserID = groupMembers.get(i);
					otherUserBA = bankCatalog.getBankAccount(otherUserID);
					IndPaymentRequestInformation paymentInf = otherUserBA.addIndPaymentRequest(otherUserID, userID,
							amountPerID);
					if (paymentInf != null)
						pendPayments.add(paymentInf.getUniqueID());
				}
				userBA.addGroupPaymentRequest(groupID, amount, new ArrayList<String>(groupMembers), pendPayments);

				resp = (E) Boolean.TRUE;
			} catch (UserNotFoundException | NumberFormatException | InvalidOperation e) {
				resp = (E) e.getMessage();
			}
			break;
		case "statuspayments":
		case "s":
			if (splittedMessage.length != 2) {
				resp = (E) Boolean.FALSE;
				break;
			}

			try {
				String groupID = splittedMessage[1];

				if (!groupCatalog.contains(groupID)) {
					resp = (E) Boolean.FALSE;
					break;
				}

				group = groupCatalog.getGroup(groupID);

				if (!group.isOwner(userID)) {
					resp = (E) Boolean.FALSE;
					break;
				}

				userBA = bankCatalog.getBankAccount(userID);

				List<GroupPaymentReqInformation> gpriList = userBA.getGroupPaymentReqInformation(groupID);

				for (String currUserID : group.getGroupMembers()) {
					for (GroupPaymentReqInformation gpri : gpriList) {
						BankAccount currUserBA = bankCatalog.getBankAccount(currUserID);
						List<String> paidPendPayments = currUserBA.getPaidPendingPayments();
						List<String> groupPendPayments = gpri.getPendPayments();
						for (String paidPayment : paidPendPayments) {
							for (String groupPendPayment : groupPendPayments) {
								if (paidPayment.equals(groupPendPayment)) {
									gpri.updatePendPaymtsList(paidPayment, currUserID);
									break;
								}
							}
						}
					}
				}
				resp = (E) userBA.statusPayments(groupID);
			} catch (UserNotFoundException e) {
				resp = (E) e.getMessage();
			}
			break;
		case "history":
		case "h":
			if (splittedMessage.length != 2) {
				resp = (E) Boolean.FALSE;
				break;
			}

			String groupID = splittedMessage[1];

			try {
				group = groupCatalog.getGroup(groupID);

				if (!groupCatalog.contains(groupID)) {
					resp = (E) Boolean.FALSE;
					break;
				}

				if (!group.isOwner(userID)) {
					resp = (E) Boolean.FALSE;
					break;
				}

				userBA = bankCatalog.getBankAccount(userID);
				List<String> paymentIds = userBA.getHistory(groupID);

				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("ID do grupo: " + groupID);
				stringBuilder.append(System.getProperty("line.separator"));
				stringBuilder.append("Lista de pagamentos realizados do grupo " + groupID + ": ");
				stringBuilder.append(System.getProperty("line.separator"));
				for (String id : paymentIds) {
					stringBuilder.append(id);
					stringBuilder.append(System.getProperty("line.separator"));
				}

				resp = (E) stringBuilder.toString();

			} catch (UserNotFoundException | InvalidOperation e) {
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
