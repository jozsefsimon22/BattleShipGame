package battleShip;

import java.io.Serializable;
import java.util.Comparator;

public class User implements Serializable {
	private static final long serialVersionUID = 1l;
	final private String name;
	private int highestScore;

	public User(String name) {
		this.name = name;
	}

	// Getters and setters
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
		if (this.highestScore < score) {
			setHighestScore(score);
		}
	}

	public static Comparator<User> sortByUserScoreDesc = new Comparator<User>() {
		public int compare(User user1, User user2) {
			int score1 = user1.getHighestScore();
			int score2 = user2.getHighestScore();

			return score2 - score1;
		}

	};

}
