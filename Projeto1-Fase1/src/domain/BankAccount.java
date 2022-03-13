package domain;

import exceptions.InsufficientBalanceException;
import exceptions.UserNotFoundException;

public class BankAccount {

	private double currentAmount;

	public BankAccount() {
		currentAmount = 100.0;
	}

	public double balance() {
		return currentAmount;
	}

	public synchronized void addAmount(double amount) {
		if (amount >= 0) {
			currentAmount += amount;
		}
	}

	public synchronized void removeAmount(double amount) throws InsufficientBalanceException, UserNotFoundException {
		if (amount >= 0 && currentAmount - amount >= 0) {
			currentAmount -= amount;
		} else if (currentAmount - amount < 0) {
			throw new InsufficientBalanceException("Valor na conta insuficiente para proceder com operacao");
		}
	}
	
}