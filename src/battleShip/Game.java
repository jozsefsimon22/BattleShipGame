package battleShip;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Game {

	public static void main(String[] args) {
		// Class variables
		String menuOption;
		ArrayList<User> users;

		// Reading in existing/previous users
		users = readInUsers();

		// Printing welcome message to the user and asking for a name input
		String currentPlayerName = JOptionPane.showInputDialog(null,
				"Welcome to the BattleShip 2022 Game!\n\n Please enter your name:", "Welcome",
				JOptionPane.INFORMATION_MESSAGE);

		// Setting the current player and adding it the the player list// <<ADD A
		// FUNCTION TO CHECK IF THE USER EXISTS IN THE DATABASE IF YES PRINT WELCOME
		// BACK MESSAGE AND DON'T ADD IT TO THE USER LIST JUST SET IT TO THE CURRENT
		// USER
		User currentPlayer = player(currentPlayerName);
		users.add(currentPlayer);

		// Game starts here
		JOptionPane.showMessageDialog(null, "Welcome " + users.get(0).getName(), "BattleShip 2022",
				JOptionPane.INFORMATION_MESSAGE);

		menuOption = menu().toLowerCase();

		while (!menuOption.equals("e")) {
			// Starting new game
			if (menuOption.equals("n")) {
				// Printing new game message to the screen
				JOptionPane.showMessageDialog(null, "New Game");

				// Generating a grid with ships added
				Grid map = newMap();

				// Calling the game function to start a new regular game
				game(map, "", currentPlayer);

				menuOption = menu().toLowerCase();

			} else if (menuOption.equals("l")) {
				JOptionPane.showMessageDialog(null, "Loading Game");
				menuOption = menu().toLowerCase();
			} else if (menuOption.equals("d")) {
				JOptionPane.showMessageDialog(null, "Debug Mode");

				// Generating a grid with ships added
				Grid map = newMap();

				// Calling the game method to start a new game in debug mode
				game(map, "debug", currentPlayer);

				// Presenting the main menu to the user
				menuOption = menu().toLowerCase();
			} else if (menuOption.equals("h")) {
				JOptionPane.showMessageDialog(null, "High-Score Table");
				JOptionPane.showMessageDialog(null, currentPlayer.getHighestScore());
				menuOption = menu().toLowerCase();
			} else if (menuOption.equals("e")) {
				JOptionPane.showMessageDialog(null, "Exiting Game");
			} else {
				JOptionPane.showMessageDialog(null, "Invalid Input!", "Error", JOptionPane.ERROR_MESSAGE);
				menuOption = menu().toLowerCase();
			}
		}

		JOptionPane.showMessageDialog(null, "Goodbye!");

	}// End of main method

	// This method generates a new map. The map is a grid with ships randomly placed
	// on it.
	public static Grid newMap() {

		String[] shipNames = { "Aircraft Carrier", "Battleship", "Submarine", "Destroyer", "Patrol boat" };
		ArrayList<Ship> ships = new ArrayList<Ship>();
		int counter = 0;

		// Creating a new grid
		Grid grid = new Grid();

		// Adding ships to the grid
		while (ships.size() < shipNames.length) {
			Ship tempShip = new Ship(shipNames[counter]);

			if (!grid.checkIfThereIsShip(tempShip)) {
				ships.add(tempShip);
				grid.addShip(tempShip);
				counter++;
			}

		}

		return grid;
	}

	// This method returns the main menu
	public static String menu() {
		String menuDisplay;
		String menuInput;

		// Creating menu option
		menuDisplay = "N - New Game" + "\n";
		menuDisplay += "L - Load Game" + "\n";
		menuDisplay += "D - Debug Mode" + "\n";
		menuDisplay += "H - High-Score Table" + "\n";
		menuDisplay += "E - Exit" + "\n";

		menuInput = JOptionPane.showInputDialog(null, "Please select an option:\n\n" + menuDisplay, "BattleShip 2022",
				JOptionPane.INFORMATION_MESSAGE).toLowerCase();

		return menuInput;

	}

	// This method generates a new game
	public static void game(Grid map, String mode, User player) {
		String statusBar;
		String rowInputAsString;
		String columnInputAsString;
		String bombedLocations = "";
		String message;
		String gameOverMessage;
		String sinkedShips = "";
		int rowInput;
		int columnInput;
		int score = 0; // Stores the user's score
		final int maxScore = 30; // The max score that can be achieved by sinking all the ships
		int bombsRemaining = 10; // The maximum tries a user can have
		int tries = 0;
		Square targetedLocation;

		while (bombsRemaining > 0 && score < maxScore) {

			if (mode.equals("debug")) {
				statusBar = "Debug Mode - Ship Locations:\n";
				statusBar += map.returnAllShipsAllLocationAsString();
			} else {
				statusBar = "";
			}
			statusBar += "\nScore: " + score + "\t|";
			statusBar += "Bombs Left: " + bombsRemaining + "\n";
			statusBar += "\nBombed Locations: " + bombedLocations + "\n";
			statusBar += "\nSinked Ships Locations: \n" + sinkedShips + "\n";

			// Asking the user for the coordinate input
			rowInputAsString = JOptionPane.showInputDialog(statusBar + "Enter row number: ");
			columnInputAsString = JOptionPane.showInputDialog(statusBar + "Enter column number: ");
			// Parsing input to Integer values
			rowInput = Integer.parseInt(rowInputAsString);
			columnInput = Integer.parseInt(columnInputAsString);

			targetedLocation = map.grid[rowInput][columnInput];

			if (targetedLocation.bombedAt) {
				message = "You already dropped a bomb here. Try another location.";
			} else {
				if (!targetedLocation.isThereAShip()) {
					message = "It's a miss, please try again.";
				} else if (targetedLocation.ship.isDestroyed()) {
					message = "You already destroyed the " + targetedLocation.ship.getType()
							+ ". Try another location.";
				} else {
					message = "It's a hit!";
					message += "\nYou sinked a " + targetedLocation.ship.getType();
					message += "\nYou got +" + targetedLocation.ship.getPoints() + " points!";
					score += targetedLocation.ship.getPoints();
//					sinkedShips += map.returnOneShipsAllLocationAsString(targetedLocation.ship, map); 
					sinkedShips += "\t" + targetedLocation.ship.getType() + ": " + targetedLocation.ship.positions
							+ "\n";
//					sinkedShips += targetedLocation.ship.positionsArray.toString();
					targetedLocation.ship.destroyShip();
				}
				bombedLocations += " (" + rowInputAsString + ", " + columnInputAsString + ")";
				targetedLocation.bombedAt = true;
				bombsRemaining--;
				tries++;
			}
			JOptionPane.showMessageDialog(null, message);
		}

		// Presenting score to the user
		gameOverMessage = "Game Over";
		gameOverMessage += "\nScore: " + score;
		gameOverMessage += "\nNumber of tries " + tries;
		gameOverMessage += "\nShips sinked";
		gameOverMessage += "\n" + sinkedShips;

		JOptionPane.showMessageDialog(null, gameOverMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);

		player.checkHighestScore(score);

	}

	public static User player(String name) {
		User player;

		player = new User(name);

		return player;
	}

	// This method reads in user objects from a .txt file and returns an ArrayList
	// with the objects
	public static ArrayList<User> readInUsers() {
		// Method variables
		ArrayList<User> output;
		FileInputStream fileInput;
		ObjectInputStream objectInput;
		User user;

		// Initialising method variables, reading users and adding user objects to the
		// output ArrayList
		output = new ArrayList<User>();
		try {
			fileInput = new FileInputStream(new File("userData.txt"));
			objectInput = new ObjectInputStream(fileInput);
			while (true) {
				try {
					user = (User) objectInput.readObject();
					output.add(user);
				} catch (EOFException endOfFile) {
					break;
				}
			}
			fileInput.close();
			objectInput.close();
		} catch (Exception e) {
			System.out.println("Error");
		}

		return output;

	}

}// End of class
