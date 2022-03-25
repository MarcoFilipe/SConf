package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import data.GroupPendingPaymentData;
import data.HistoryData;
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
	private static final GroupPendingPaymentData GROUP_PENDING_PAYMENT_SINGLETON = GroupPendingPaymentData
			.getInstance();
	private static final HistoryData HISTORY_SINGLETON = HistoryData.getInstance();
	private List<IndPaymentRequestInformation> indPendingPayment = new ArrayList<IndPaymentRequestInformation>();
	private HashMap<String, List<GroupPaymentReqInformation>> groupsPaymentReqInfo = new HashMap<String, List<GroupPaymentReqInformation>>();
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

	public synchronized IndPaymentRequestInformation addIndPaymentRequest(String userID, String userWhoRequestedPayment,
			double amount) throws InvalidOperation {
		IndPaymentRequestInformation inf = null;
		if (amount >= 0) {
			inf = new IndPaymentRequestInformation(amount, userID, userWhoRequestedPayment);
			indPendingPayment.add(inf);
			IND_PENDING_PAYMENT_SINGLETON.addLine(inf.getUniqueID(), amount, userWhoRequestedPayment);
		} else {
			throw new InvalidOperation();
		}
		return inf;
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
			sb.delete(0, sb.length());
		}

		return pendingPayments;
	}

	public List<String> getPaidPendingPayments() {
		return paidPendingPayments;
	}

	public synchronized GroupPaymentReqInformation addGroupPaymentRequest(String groupID, double amount,
			List<String> pendMembers, List<String> pendPayments) {
		GroupPaymentReqInformation inf = new GroupPaymentReqInformation(groupID, amount, pendMembers, pendPayments);
		List<GroupPaymentReqInformation> gpriList = groupsPaymentReqInfo.get(groupID);
		if (gpriList == null) {
			gpriList = new ArrayList<GroupPaymentReqInformation>();
		}
		gpriList.add(inf);
		groupsPaymentReqInfo.put(groupID, gpriList);
		GROUP_PENDING_PAYMENT_SINGLETON.addLine(groupID, amount);
		return inf;
	}

	public List<GroupPaymentReqInformation> getGroupPaymentReqInformation(String groupID) {
		return groupsPaymentReqInfo.get(groupID);
	}

	public synchronized String statusPayments(String groupID) {
		StringBuilder sb = new StringBuilder();
		List<GroupPaymentReqInformation> gpriList = groupsPaymentReqInfo.get(groupID);
		if (gpriList.size() == 0) {
			sb.append("vazio");
			sb.append(System.getProperty("line.separator"));
		} else {
			for (GroupPaymentReqInformation gpri : gpriList) {
				sb.append("ID do Pedido: " + gpri.getUniqueID());
				sb.append(System.getProperty("line.separator"));
				if (gpri.getPendMembersID().size() == 0) {
					sb.append("vazio");
					sb.append(System.getProperty("line.separator"));
				} else {
					for (String pendMembers : gpri.getPendMembersID()) {
						sb.append(pendMembers);
						sb.append(System.getProperty("line.separator"));
					}
				}
			}
		}
		return sb.toString();
	}

	public List<String> getHistory(String groupID) throws InvalidOperation {
		List<GroupPaymentReqInformation> gpriList = groupsPaymentReqInfo.get(groupID);
		List<String> completed = new ArrayList<String>();
		for (GroupPaymentReqInformation gpri : gpriList) {
			if (gpri.isCompleted()) {
				completed.add(gpri.uniqueID);
			}
		}

		if (completed != null) {
			HISTORY_SINGLETON.addHistoric(groupID, completed);
			// update? TODO
		}

		return completed;
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

	public class GroupPaymentReqInformation {

		private String groupID = null;
		private Double amount = null;
		private List<String> pendPayments = null;
		private List<String> pendMembersID = null;
		private List<String> usersWhoPaid = new ArrayList<String>();
		private String uniqueID = null;
		private boolean completed = false;

		public GroupPaymentReqInformation(String groupID, double amount, List<String> pendMembersID,
				List<String> pendPayments) {
			this.groupID = groupID;
			this.amount = amount;
			this.pendPayments = pendPayments;
			this.pendMembersID = pendMembersID;
			this.uniqueID = generateUniqueID();
		}

		public String getGroupID() {
			return groupID;
		}

		public Double getAmount() {
			return amount;
		}

		public List<String> getPendMembersID() {
			return pendMembersID;
		}

		public List<String> getPendPayments() {
			return pendPayments;
		}

		public synchronized void updatePendPaymtsList(String uniqueID, String userID) {
			pendPayments.remove(uniqueID);
			pendMembersID.remove(userID);
			usersWhoPaid.add(userID);
		}

		public List<String> getUsersIDWhoPaid() {
			return usersWhoPaid;
		}

		public String getUniqueID() {
			return uniqueID;
		}

		private String generateUniqueID() {
			return UUID.randomUUID().toString();
		}

		private boolean isCompleted() {
			return pendPayments.size() == 0;
		}
	}

}