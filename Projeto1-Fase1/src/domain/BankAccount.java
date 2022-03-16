package domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import data.IndPendingPaymentData;
import exceptions.InsufficientBalanceException;
import exceptions.InvalidOperation;
import exceptions.UserNotFoundException;

/**
 * Classe responsavel pela representacao da conta de um determinado cliente,
 * possuindo diversos metodos responsaveis pela gestao do saldo da conta.
 * 
 * @author grupo 36.
 *
 */
public class BankAccount {

	private static final IndPendingPaymentData IND_PENDING_PAYMENT_SINGLETON = IndPendingPaymentData.getInstance();
	private List<IndPaymentRequestInformation> indPendingPayment = new ArrayList<IndPaymentRequestInformation>();
	private double currentAmount = 100.0;

	public double balance() {
		return currentAmount;
	}

	public synchronized void addAmount(double amount) throws InvalidOperation {
		if (amount >= 0) {
			currentAmount += amount;
		} else {
			throw new InvalidOperation();
		}
	}

	public synchronized void removeAmount(double amount) throws InvalidOperation, InsufficientBalanceException {
		if (amount >= 0) {
			if (currentAmount - amount >= 0) {
				currentAmount -= amount;
			} else {
				throw new InsufficientBalanceException("Valor na conta insuficiente para proceder com operacao");
			}
		} else {
			throw new InvalidOperation();
		}
	}

	public synchronized void addIndPaymentRequest(String userID, String userWhoRequestedPayment, double amount) {
		IndPaymentRequestInformation inf = new IndPaymentRequestInformation(amount, userWhoRequestedPayment);
		indPendingPayment.add(inf);
		IND_PENDING_PAYMENT_SINGLETON.addLine(inf.getUniqueID(), amount, userWhoRequestedPayment);
	}

	// TODO verificar
	public synchronized void removeIndPaymentRequest(String userID) throws UserNotFoundException {
		String currUser = null;
		for (int i = 0; i < indPendingPayment.size(); i++) {
			currUser = indPendingPayment.get(i).getUserID();
			if (currUser.equals(userID)) {
				indPendingPayment.get(i);
			}
		}
		throw new UserNotFoundException("Nao existe usuario com essa identidade");
	}

	public List<String> getIndPaymtRequestList() {
		List<String> pendingPayments = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		if (indPendingPayment.size() == 0) {
			return null;
		}

		for (IndPaymentRequestInformation ipri : indPendingPayment) {
			sb.append(ipri.getUniqueID());
			sb.append(" ");
			sb.append(ipri.getAmount());
			sb.append(" ");
			sb.append(ipri.getUserID());
			pendingPayments.add(sb.toString());
			sb.delete(0, sb.length() - 1);
		}

		return pendingPayments;
	}

	public class IndPaymentRequestInformation {

		private String uniqueID = null;
		private Double amount = null;
		private String userID = null;

		public IndPaymentRequestInformation(Double amount, String userID) {
			this.amount = amount;
			this.userID = userID;
			this.uniqueID = generateUniqueID();
		}

		public String getUniqueID() {
			return uniqueID;
		}

		public Double getAmount() {
			return amount;
		}

		public String getUserID() {
			return userID;
		}

		private String generateUniqueID() {
			return Integer.toString(String
					.format("%s%s%s", userID, String.valueOf(amount), LocalDateTime.now(ZoneId.of("WET")).toString())
					.hashCode());
		}

	}

}