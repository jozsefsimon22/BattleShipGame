package battleShip;

public class User {
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
	
	

}
