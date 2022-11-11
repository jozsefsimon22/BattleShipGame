package battleShip;

import java.util.ArrayList;
import java.util.Random;

public class Ship {
	// Class attributes
	private String type;
	private int direction;
	private int startingPositionX;
	private int startingPositionY;
	private int length;
	private int points;
	private boolean destroyed;
	public String positions;
	public ArrayList<Integer> positionsArray; 

	// Generating ships based on the type input. Each type input will generate a
	// ship with different instance variables to ensure values are allocated to the
	// ships correctly. The length of the ship types also functions as a control
	// variable when generating the ship locations, this is to ensures that the
	// ships don't hand over the grid.
	public Ship(String type) {
		Random randomNumberGenerator = new Random();
		setDirection(randomNumberGenerator.nextInt(2)); // Setting the direction of the ship
		if (type.equals("Aircraft Carrier")) {
			setPoints(2);
			setType(type);
			setLength(5);
			setStartingPositionX(randomNumberGenerator.nextInt(5));
			setStartingPositionY(randomNumberGenerator.nextInt(5));

		} else if (type.equals("Battleship")) {
			setPoints(4);
			setType(type);
			setDestroyed(false);
			setLength(4);
			setStartingPositionX(randomNumberGenerator.nextInt(6));
			setStartingPositionY(randomNumberGenerator.nextInt(6));

		} else if (type.equals("Submarine")) {
			setPoints(6);
			setType(type);
			setDestroyed(false);
			setLength(3);
			setStartingPositionX(randomNumberGenerator.nextInt(7));
			setStartingPositionY(randomNumberGenerator.nextInt(7));

		} else if (type.equals("Destroyer")) {
			setPoints(8);
			setType(type);
			setDestroyed(false);
			setLength(2);
			setStartingPositionX(randomNumberGenerator.nextInt(8));
			setStartingPositionY(randomNumberGenerator.nextInt(8));

		} else if (type.equals("Patrol boat")) {
			setPoints(10);
			setType(type);
			setDestroyed(false);
			setLength(1);
			setStartingPositionX(randomNumberGenerator.nextInt(10));
			setStartingPositionY(randomNumberGenerator.nextInt(10));

		}
		this.destroyed = false;
		this.positions = "";
		this.positionsArray = new ArrayList<>();

	}// End of constructor

	// Returns a string representation of the Ship and it attributes 
	public String toString() {
		String output;
		output = "Type: " + getType() + "\n";
		output += "Direction: " + getDirection() + "\n";
		output += "Points: " + getPoints() + "\n";
		return output;
	}
	
	public void destroyShip() {
		this.destroyed = true;
	}

	// Setters and getters
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	public int getStartingPositionX() {
		return startingPositionX;
	}

	public void setStartingPositionX(int startingPositionX) {
		this.startingPositionX = startingPositionX;
	}

	public int getStartingPositionY() {
		return startingPositionY;
	}

	public void setStartingPositionY(int startingPositionY) {
		this.startingPositionY = startingPositionY;
	}

} // End of class
