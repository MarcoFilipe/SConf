package domain;

import java.util.ArrayList;
import java.util.List;

public class Group {

	private List<String> groupMembers = new ArrayList<String>();

	public void add(String id) {
		groupMembers.add(id);
	}

	public boolean isOwner(String id) {
		if (groupMembers.get(0).equals(id)) {
			return true;
		}
		return false;
	}

	public boolean contains(String userID) {
		return groupMembers.contains(userID);
	}

	public List<String> getGroupMembers() {
		return groupMembers;
	}

	public void setGroup(List<String> group) {
		this.groupMembers = group;
	}

}
