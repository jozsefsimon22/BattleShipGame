package battleShip;
import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1l;
	final private String name;
	private int highestScore;

	public User(String name) {
		this.name = name;
	}

	//Getters and setters
	public int getHighestScore() {
		return highestScore;
	}

	public void setHighestScore(int highestScore) {
		this.highestScore = highestScore;
	}

	public String getName() {
		return name;
	}
	
	public void checkHighestScore(int score) {
		if(this.highestScore < score) {
			setHighestScore(score);
		}
	}

}
