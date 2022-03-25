package domain;

import java.util.ArrayList;
import java.util.List;

public class Group {

	private List<String> group = new ArrayList<String>();

	public void add(String id) {
		group.add(id);
	}

	public boolean isOwner(String id) {
		if (group.get(0).equals(id)) {
			return true;
		}
		return false;
	}
	

	public boolean contains(String userID) {
		return group.contains(userID);
	}

	public List<String> getGroup() {
		return group;
	}

	public void setGroup(List<String> group) {
		this.group = group;
	}
	
	public void print() {
		for(int i = 0; i < group.size(); i++) {
            System.out.println(group.get(i));
        }
	}

}
