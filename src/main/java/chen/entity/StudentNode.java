package chen.entity;

import java.util.List;
import java.util.Objects;

public class StudentNode {

	private final String name;
	private int score;
	private List<String> answer;
	private List<Boolean> result;


	public void setResult(List<Boolean> result) {
		this.result = result;
	}

	public StudentNode(String name, int score) {
		super();
		this.name = name;
		this.score = score;
	}

	public StudentNode(String name, List<String> answer) {
		this.name = name;
		this.answer = answer;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public List<String> getAnswer() {
		return answer;
	}

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		StudentNode other = (StudentNode) obj;
//		if (name == null) {
//			if (other.name != null)
//				return false;
//		} else if (!name.equals(other.name))
//			return false;
//		return true;
//	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StudentNode that = (StudentNode) o;
		return Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "StudentNode [name=" + name + ", score=" + score + ", answer=" + answer + "]";
	}

//	@Override
//	public String toString() {
//		return "StudentNode [name=" + name + ", score=" + score + "]";
//	}
	
}
