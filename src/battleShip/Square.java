package battleShip;

import java.io.Serializable;

public class Square implements Serializable{
	private static final long serialVersionUID = 1l;
	int number;
	Ship ship;
	boolean bombedAt = false;

	public Square(int number) {
		this.number = number;
	}

	public void addShip(Ship ship) {
		this.ship = ship;
	}
	
	public boolean isThereAShip() {
		if(this.ship == null) {
			return false;
		}
		return true;
	}
	
	public void dropABomb() {
		bombedAt = true;
	}
}
