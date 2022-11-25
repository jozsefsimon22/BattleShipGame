package battleShip;

import java.io.Serializable;
import java.util.ArrayList;

public class Grid implements Serializable{
	private static final long serialVersionUID = 1l;
	final int length = 10;
	final int width = 10;

	Square[][] grid;

	// This constructor generates a Square 2D Array with a 'Square' object at each
	// element
	public Grid() {
		int squareNumber = 0;
		this.grid = new Square[length][width];
		for (int row = 0; row < grid.length; row++) {
			for (int column = 0; column < grid[row].length; column++) {
				this.grid[row][column] = new Square(squareNumber);
				squareNumber++;
			}
		}
	} // End of constructor 


	// This method adds a ship to multiple 'Square' objects for the whole length of
	// the ship
	public void addShip(Ship ship) {
		int direction = ship.getDirection();
		int x = ship.getStartingPositionX();
		int y = ship.getStartingPositionY();
		int length = ship.getLength();

		for (int loop = 0; loop < length; loop++) {
			if (direction == 0) {
				this.grid[x][y + loop].addShip(ship);
				ship.positions += " (" + x + ", " + (y + loop) + ") ";
				ship.positionsArray.add(x);
				ship.positionsArray.add(y + loop);
			} else if (direction == 1) {
				this.grid[x + loop][y].addShip(ship);
				ship.positions += " (" + (x + loop) + ", " + y + ") ";
				ship.positionsArray.add(x + loop);
				ship.positionsArray.add(y);
				ship.positionsArray.add(this.grid[x][y].number);
			}
		}
	}

	// Checks if there is a ship at the give locations. This method is used when
	// creating a new ship object. It iterates through every square the new ship
	// will occupy to ensure it won't cross with another ship
	public boolean checkIfThereIsShip(Ship ship) {
		int direction = ship.getDirection();
		int x = ship.getStartingPositionX();
		int y = ship.getStartingPositionY();
		int length = ship.getLength();
		boolean isThereAShip = false;

		for (int loop = 0; loop < length; loop++) {
			if (direction == 0) {
				if (this.grid[x][y + loop].isThereAShip() == true) {
					isThereAShip = true;
				}
			} else if (direction == 1) {
				if (this.grid[x + loop][y].isThereAShip() == true) {
					isThereAShip = true;
				}
			}
		}
		return isThereAShip;
	}

	// Returns all ships location as String
	public String returnAllShipsAllLocationAsString() {
		ArrayList<Ship> ships = new ArrayList<Ship>();
		String output = "";

		for (int row = 0; row < this.grid.length; row++) {
			for (int column = 0; column < this.grid[row].length; column++) {
				if (this.grid[row][column].ship != null && ships.contains(this.grid[row][column].ship) == false) {
					ships.add(this.grid[row][column].ship);
				}
			}
		}

		for (Ship ship : ships) {
			output += ship.getType();
			for (int row = 0; row < this.grid.length; row++) {
				for (int column = 0; column < this.grid[row].length; column++) {
					if (this.grid[row][column].ship == ship) {
						output += " (" + row + ", " + column + ")";
					}
				}
			}
			output += "\n";

		}

		return output;

	}
}
