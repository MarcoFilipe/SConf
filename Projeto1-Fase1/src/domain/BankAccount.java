package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import data.IndPendingPaymentData;
import exceptions.InsufficientBalanceException;
import exceptions.InvalidIdentifierException;
import exceptions.InvalidOperation;

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
	private List<String> paidPendingPayments = new ArrayList<String>();
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

	public synchronized void addIndPaymentRequest(String userID, String userWhoRequestedPayment, double amount)
			throws InvalidOperation {
		if (amount >= 0) {
			IndPaymentRequestInformation inf = new IndPaymentRequestInformation(amount, userID,
					userWhoRequestedPayment);
			indPendingPayment.add(inf);
			IND_PENDING_PAYMENT_SINGLETON.addLine(inf.getUniqueID(), amount, userWhoRequestedPayment);
		} else {
			throw new InvalidOperation();
		}
	}

	public synchronized void removeIndPaymentRequest(String uniqueID, String userID) {
		IndPaymentRequestInformation currInf = null;

		for (int i = 0; i < indPendingPayment.size(); i++) {
			currInf = indPendingPayment.get(i);
			if (currInf.getUniqueID().equals(uniqueID)) {
				if (currInf.getUserID().equals(userID)) {
					indPendingPayment.remove(i);
					paidPendingPayments.add(uniqueID);
				}
			}
		}
	}

	public IndPaymentRequestInformation getIndPaymentRequestInf(String uniqueID) throws InvalidIdentifierException {
		for (IndPaymentRequestInformation currInf : indPendingPayment) {
			if (currInf.getUniqueID().equals(uniqueID)) {
				return currInf;
			}
		}
		if (IND_PENDING_PAYMENT_SINGLETON.getLine(uniqueID) != null && !paidPendingPayments.contains(uniqueID)) {
			throw new InvalidIdentifierException("O identificador eh referente a um pagamento pedido a outro cliente");
		}
		throw new InvalidIdentifierException("O identificador nao existe");
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
			sb.append(ipri.getUserWhoRequestedPayment());
			pendingPayments.add(sb.toString());
			sb.delete(0, sb.length() - 1);
		}

		return pendingPayments;
	}

	public class IndPaymentRequestInformation {

		private String uniqueID = null;
		private Double amount = null;
		private String userID = null;
		private String userWhoRequestedPayment = null;

		public IndPaymentRequestInformation(Double amount, String userID, String userWhoRequestedPayment) {
			this.amount = amount;
			this.userID = userID;
			this.userWhoRequestedPayment = userWhoRequestedPayment;
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

		public String getUserWhoRequestedPayment() {
			return userWhoRequestedPayment;
		}

		private String generateUniqueID() {
			return UUID.randomUUID().toString();
		}

	}

}