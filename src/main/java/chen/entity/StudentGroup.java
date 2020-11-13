package chen.entity;

import java.util.ArrayList;

public class StudentGroup {

	private String groupName;
	private ArrayList<StudentNode> stuNodes;
	private int groupScore;
	
	public StudentGroup(String groupName) {
		this.groupName = groupName;
		stuNodes = new ArrayList<>();
	}
	
	public void setStuNodes(ArrayList<StudentNode> stuNodes) {
		this.stuNodes = stuNodes;
	}

	public void setGroupScore(int groupScore) {
		this.groupScore = groupScore;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupScore() {
		return groupScore;
	}

	public ArrayList<StudentNode> getStuNodes() {
		return stuNodes;
	}

	@Override
	public String toString() {
		return "StudentGroup [groupName=" + groupName + ", stuNodes=" + stuNodes + ", groupScore=" + groupScore + "]";
	}
	
}
