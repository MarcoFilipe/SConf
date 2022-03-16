package domain;

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
	private static final List<Pair> IND_PENDING_PAYMENT = new ArrayList<Pair>();
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

	public synchronized void addIndPaymentRequest(String userID, String otherUserID, double amount) {
		IND_PENDING_PAYMENT.add(new Pair(userID, amount));
		IND_PENDING_PAYMENT_SINGLETON.addLine(userID, otherUserID, amount);
	}

	// TODO verificar
	public synchronized void removeIndPaymentRequest(String userID) throws UserNotFoundException {
		String currUser = null;
		for (int i = 0; i < IND_PENDING_PAYMENT.size(); i++) {
			currUser = IND_PENDING_PAYMENT.get(i).getUserID();
			if (currUser.equals(userID)) {
				IND_PENDING_PAYMENT.get(i);
			}
		}
		throw new UserNotFoundException("Nao existe usuario com essa identidade");
	}
	
	public class Pair {

		private String userID = null;
		private Double amount = null;

		public Pair(String userID, Double amount) {
			this.userID = userID;
			this.amount = amount;
		}

		public String getUserID() {
			return userID;
		}

		public Double getAmount() {
			return amount;
		}
	}

}