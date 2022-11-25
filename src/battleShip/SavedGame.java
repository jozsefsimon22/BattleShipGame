package battleShip;
import java.io.Serializable;

public class SavedGame implements Serializable{
	private static final long serialVersionUID = 1l;
	Grid map;
	String mode; 
	User player; 
	String statusBar; 
	String bombedLocations; 
	String sunkShips;
	int score;
	int bombsRemaining; 
	int tries;
	
	
	public SavedGame(Grid map, String mode, User player, String statusBar, String bombedLocations, String snikedShips,
			int score, int bombsRemaining, int tries) {
		this.map = map;
		this.mode = mode;
		this.player = player;
		this.statusBar = statusBar;
		this.bombedLocations = bombedLocations;
		this.sunkShips = snikedShips;
		this.score = score;
		this.bombsRemaining = bombsRemaining;
		this.tries = tries;
	}
	

}
