package domain;

import java.util.HashMap;

import exceptions.UserNotFoundException;

public class GroupCatalog {

	private HashMap<String, Group> groupList = new HashMap<String, Group>();

	public void add(String userID, Group group) {
		if (!groupList.containsKey(userID)) {
			groupList.put(userID, group);
		}
	}
	
	public boolean contains(String key) {
		for(String x : groupList.keySet()) {
			if(x == key) {
				return true;
			}
		}
		return false;	
	}

	public Group getGroup(String userID) throws UserNotFoundException {
		Group group = groupList.get(userID);
		if (group == null) {
			throw new UserNotFoundException("Nao existe groupo com essa identidade");
		}
		return group;
	}

}