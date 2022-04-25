package domain;

import java.util.HashMap;

import exceptions.UserNotFoundException;

/**
 * Classe responsavel pela gestao dos clientes e suas respectivas contas.
 * 
 * @author grupo 36.
 *
 */
public class BankAccountCatalog {

	private HashMap<String, BankAccount> hashMap = new HashMap<String, BankAccount>();

	public void add(String userID, BankAccount bankAccount) {
		if (!hashMap.containsKey(userID)) {
			hashMap.put(userID, bankAccount);
		}
	}

	public BankAccount getBankAccount(String userID) throws UserNotFoundException {
		BankAccount userBankAccount = hashMap.get(userID);
		if (userBankAccount == null) {
			throw new UserNotFoundException("Nao existe usuario com essa identidade");
		}
		return userBankAccount;
	}

}
