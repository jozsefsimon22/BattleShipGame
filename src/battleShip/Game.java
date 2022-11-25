package battleShip;

import java.io.Console;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SecureCacheResponse;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

public class Game {

	public static void main(String[] args) {
		// Class variables
		String menuOption;
		String nameInput;
		String welcomeMessage;
		User currentPlayer;
		Grid map;
		ArrayList<User> users = new ArrayList<User>();
		ArrayList<SavedGame> savedGames = new ArrayList<SavedGame>();

		// Reading in existing/previous users and saved games
		users = readInUsers();
		savedGames = readSavedGamesIn();

		// Printing welcome message to the user and asking for a name input
		nameInput = JOptionPane.showInputDialog(null,
				"Welcome to the BattleShip 2022 Game!\n\n Please enter your name:", "Welcome",
				JOptionPane.INFORMATION_MESSAGE);

		// Setting the current player. If it's a new player it's added o the users
		// ArrayList
		currentPlayer = validateUser(nameInput, users);

		if (!users.contains(currentPlayer)) {
			users.add(currentPlayer);
			welcomeMessage = "Welcome " + nameInput;
		} else {
			welcomeMessage = "Welcome back " + currentPlayer.getName();
		}

		JOptionPane.showMessageDialog(null, welcomeMessage, "BattleShip 2022", JOptionPane.INFORMATION_MESSAGE);

		menuOption = menu().toLowerCase();

		while (!menuOption.equals("e")) {
			// Starting new game
			if (menuOption.equals("n")) {
				// Printing new game message to the screen
				JOptionPane.showMessageDialog(null, "New Game");

				// Generating a grid with ships added
				map = newMap();

				// Calling the game function to start a new regular game
				SavedGame currentGame = game(map, "", currentPlayer);

				if (currentGame != null) {
					savedGames.add(currentGame);
				}

				menuOption = menu().toLowerCase();

			} else if (menuOption.equals("l")) {
				String loadGameSelectionDisplay = "Select a saved game to load in\n\n";
				String loadGameSelectionAsString;
				int loadGameSelection;
				int minInput = 0;
				int savedGamesLength = savedGames.size();

				for (int game = 0; game < savedGamesLength; game++) {
					loadGameSelectionDisplay += "Slot " + game;
					loadGameSelectionDisplay += "   ";
					loadGameSelectionDisplay += savedGames.get(game).player.getName();
					loadGameSelectionDisplay += "\n";
				}
				loadGameSelectionAsString = JOptionPane.showInputDialog(loadGameSelectionDisplay);

				while (true)
					try {
						if (loadGameSelectionAsString == null) {
							break;
						} else {
							loadGameSelection = Integer.parseInt(loadGameSelectionAsString);
							if (loadGameSelection >= minInput && loadGameSelection < savedGamesLength) {
								JOptionPane.showMessageDialog(null, "Loading Game");
								loadGame(savedGames.get(loadGameSelection));
								break;
							}
							JOptionPane.showMessageDialog(null, "Saved game not found");
							loadGameSelectionAsString = JOptionPane.showInputDialog(loadGameSelectionDisplay);
						}

					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Invalid input");
						loadGameSelectionAsString = JOptionPane.showInputDialog(loadGameSelectionDisplay);
					}

				menuOption = menu().toLowerCase();

			}

			else if (menuOption.equals("d")) {
				JOptionPane.showMessageDialog(null, "Debug Mode");

				// Generating a grid with ships added
				map = newMap();

				// Calling the game method to start a new game in debug mode
				SavedGame currentGame = game(map, "debug", currentPlayer);

				if (currentGame != null) {
					savedGames.add(currentGame);
				}

				// Presenting the main menu to the user
				menuOption = menu().toLowerCase();
			} else if (menuOption.equals("h")) {
				JOptionPane.showMessageDialog(null, "High-Score Table");
				highScoreTable(users);
				menuOption = menu().toLowerCase();
			} else if (menuOption.equals("e")) {
				JOptionPane.showMessageDialog(null, "Exiting Game");
			} else {
				JOptionPane.showMessageDialog(null, "Invalid Input!", "Error", JOptionPane.ERROR_MESSAGE);
				menuOption = menu().toLowerCase();
			}
		}

		writeOutUsers(users);
		writeSavedGamesOut(savedGames);
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
	public static SavedGame game(Grid map, String mode, User player) {
		String statusBar;
		String rowInputAsString;
		String columnInputAsString;
		String bombedLocations = "";
		String message;
		String gameOverMessage;
		String sunkShips = "";
		int rowInput = 0;
		int columnInput = 0;
		int score = 0; // Stores the user's score
		int bombsRemaining = 10; // The maximum tries a user can have
		int tries = 0;
		int exitGameQuestion;
		final int maxScore = 30; // The max score that can be achieved by sinking all the ships
		final int minInput = 0; // Used to validate user input
		final int maxInput = 9; // Used to validate user input
		boolean validInput;
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
			statusBar += "\nSunk Ships' Locations: \n" + sunkShips + "\n";

			// Asking the user for the coordinate input
			rowInputAsString = JOptionPane.showInputDialog(statusBar + "Enter row number: ");
			validInput = false;
			while (!validInput) {
				try {
					if (rowInputAsString == null) {
						exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to exit this game?",
								"Exit Game", JOptionPane.YES_NO_OPTION);
						if (exitGameQuestion == 0) {
							exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to save this game?",
									"Exit Game", JOptionPane.YES_NO_OPTION);
							if (exitGameQuestion == 0) {
								SavedGame game = new SavedGame(map, mode, player, statusBar, bombedLocations,
										sunkShips, score, bombsRemaining, tries);
								return game;
							} else if (exitGameQuestion == 1) {
								JOptionPane.showMessageDialog(null, "Game not saved");
								return null;
							}

						} else if (exitGameQuestion == 1) {
							JOptionPane.showMessageDialog(null, "OK");
							break;
						}
					} else {
						rowInput = Integer.parseInt(rowInputAsString);
						if (rowInput >= minInput && rowInput <= maxInput) {
							validInput = true;
						} else {
							JOptionPane.showMessageDialog(null,
									"Invalid input, please ensure you enter a number between 0 and 9");
							rowInputAsString = JOptionPane.showInputDialog(statusBar + "Enter row number: ");
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Invalid input, please ensure you enter a number between 0 and 9");
					rowInputAsString = JOptionPane.showInputDialog(statusBar + "Enter row number: ");
				}
			}

			columnInputAsString = JOptionPane.showInputDialog(statusBar + "Enter column number: ");

			validInput = false;

			while (!validInput) {
				try {
					if (columnInputAsString == null) {
						exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to exit this game?",
								"Save Game", JOptionPane.YES_NO_OPTION);
						if (exitGameQuestion == 0) {
							exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to save this game?",
									"Save Game", JOptionPane.YES_NO_OPTION);
							if (exitGameQuestion == 0) {
								SavedGame game = new SavedGame(map, mode, player, statusBar, bombedLocations,
										sunkShips, score, bombsRemaining, tries);
								return game;
							} else if (exitGameQuestion == 1) {
								JOptionPane.showMessageDialog(null, "Game not saved");
								return null;
							}
						} else if (exitGameQuestion == 1) {
							JOptionPane.showMessageDialog(null, "OK");
							break;
						}
					}
					// Parsing input to Integer values
					columnInput = Integer.parseInt(columnInputAsString);
					if (columnInput >= minInput && columnInput <= maxInput) {
						validInput = true;
					} else {
						JOptionPane.showMessageDialog(null,
								"Invalid input, please ensure you enter a number between 0 and 9");
						columnInputAsString = JOptionPane.showInputDialog(statusBar + "Enter column number: ");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Invalid input, please ensure you enter a number between 0 and 9");
					columnInputAsString = JOptionPane.showInputDialog(statusBar + "Enter column number: ");
				}
			}

			// Applying user input on the map and checking if it's a hit
			targetedLocation = map.grid[rowInput][columnInput];

			if (targetedLocation.bombedAt) {
				message = "You already dropped a bomb here. Try another location.";
			} else {
				if (!targetedLocation.isThereAShip()) {
					message = "It's a miss, please try again.";
					bombsRemaining--;
					tries++;

				} else if (targetedLocation.ship.isDestroyed()) {
					message = "You already destroyed the " + targetedLocation.ship.getType()
							+ ". Try another location.";
				} else {
					message = "It's a hit!";
					message += "\nYou sinked a " + targetedLocation.ship.getType();
					message += "\nYou got +" + targetedLocation.ship.getPoints() + " points!";
					score += targetedLocation.ship.getPoints();
//					sinkedShips += map.returnOneShipsAllLocationAsString(targetedLocation.ship, map); 
					sunkShips += "\t" + targetedLocation.ship.getType() + ": " + targetedLocation.ship.positions
							+ "\n";
//					sinkedShips += targetedLocation.ship.positionsArray.toString();
					targetedLocation.ship.destroyShip();
					bombsRemaining--;
					tries++;
				}
				bombedLocations += " (" + rowInputAsString + ", " + columnInputAsString + ")";
				targetedLocation.bombedAt = true;
			}
			JOptionPane.showMessageDialog(null, message);
		}

		// Presenting score to the user if the game is completed
		gameOverMessage = "Game Over";
		gameOverMessage += "\nScore: " + score;
		gameOverMessage += "\nNumber of tries " + tries;
		gameOverMessage += "\nShips sinked";
		gameOverMessage += "\n" + sunkShips;

		JOptionPane.showMessageDialog(null, gameOverMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);

		player.checkHighestScore(score);
		return null;
	}

	// This method loads in a game previously saved
	public static SavedGame loadGame(SavedGame game) {
		String statusBar = "";
		String rowInputAsString;
		String columnInputAsString;
		String bombedLocations = game.bombedLocations;
		String message;
		String gameOverMessage;
		String sunkShips = game.sunkShips;
		String mode = game.mode;
		int rowInput = 0;
		int columnInput = 0;
		int score = game.score; // Stores the user's score
		int bombsRemaining = game.bombsRemaining;
		int tries = game.tries;
		int exitGameQuestion;
		final int maxScore = 30; // The max score that can be achieved by sinking all the ships
		final int minInput = 0; // Used to validate user input
		final int maxInput = 9; // Used to validate user input
		boolean validInput;
		Square targetedLocation;
		Grid map = game.map;
		User player = game.player;

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
			statusBar += "\nSunk Ships' Locations: \n" + sunkShips + "\n";

			// Asking the user for the coordinate input
			rowInputAsString = JOptionPane.showInputDialog(statusBar + "Enter row number: ");
			validInput = false;
			while (!validInput) {
				try {
					if (rowInputAsString == null) {
						exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to exit this game?",
								"Exit Game", JOptionPane.YES_NO_OPTION);
						if (exitGameQuestion == 0) {
							exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to save this game?",
									"Exit Game", JOptionPane.YES_NO_OPTION);
							if (exitGameQuestion == 0) {
								 game = new SavedGame(map, mode, player, statusBar, bombedLocations,
										sunkShips, score, bombsRemaining, tries);
								return game;
							} else if (exitGameQuestion == 1) {
								JOptionPane.showMessageDialog(null, "Game not saved");
								return null;
							}

						} else if (exitGameQuestion == 1) {
							JOptionPane.showMessageDialog(null, "OK");
							break;
						}
					} else {
						rowInput = Integer.parseInt(rowInputAsString);
						if (rowInput >= minInput && rowInput <= maxInput) {
							validInput = true;
						} else {
							JOptionPane.showMessageDialog(null,
									"Invalid input, please ensure you enter a number between 0 and 9");
							rowInputAsString = JOptionPane.showInputDialog(statusBar + "Enter row number: ");
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Invalid input, please ensure you enter a number between 0 and 9");
					rowInputAsString = JOptionPane.showInputDialog(statusBar + "Enter row number: ");
				}
			}

			columnInputAsString = JOptionPane.showInputDialog(statusBar + "Enter column number: ");

			validInput = false;

			while (!validInput) {
				try {
					if (columnInputAsString == null) {
						exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to exit this game?",
								"Save Game", JOptionPane.YES_NO_OPTION);
						if (exitGameQuestion == 0) {
							exitGameQuestion = JOptionPane.showConfirmDialog(null, "Would you like to save this game?",
									"Save Game", JOptionPane.YES_NO_OPTION);
							if (exitGameQuestion == 0) {
								 game = new SavedGame(map, mode, player, statusBar, bombedLocations,
										sunkShips, score, bombsRemaining, tries);
								return game;
							} else if (exitGameQuestion == 1) {
								JOptionPane.showMessageDialog(null, "Game not saved");
								return null;
							}
						} else if (exitGameQuestion == 1) {
							JOptionPane.showMessageDialog(null, "OK");
							break;
						}
					}
					// Parsing input to Integer values
					columnInput = Integer.parseInt(columnInputAsString);
					if (columnInput >= minInput && columnInput <= maxInput) {
						validInput = true;
					} else {
						JOptionPane.showMessageDialog(null,
								"Invalid input, please ensure you enter a number between 0 and 9");
						columnInputAsString = JOptionPane.showInputDialog(statusBar + "Enter column number: ");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,
							"Invalid input, please ensure you enter a number between 0 and 9");
					columnInputAsString = JOptionPane.showInputDialog(statusBar + "Enter column number: ");
				}
			}

			// Applying user input on the map and checking if it's a hit
			targetedLocation = map.grid[rowInput][columnInput];

			if (targetedLocation.bombedAt) {
				message = "You already dropped a bomb here. Try another location.";
			} else {
				if (!targetedLocation.isThereAShip()) {
					message = "It's a miss, please try again.";
					bombsRemaining--;
					tries++;

				} else if (targetedLocation.ship.isDestroyed()) {
					message = "You already destroyed the " + targetedLocation.ship.getType()
							+ ". Try another location.";
				} else {
					message = "It's a hit!";
					message += "\nYou sinked a " + targetedLocation.ship.getType();
					message += "\nYou got +" + targetedLocation.ship.getPoints() + " points!";
					score += targetedLocation.ship.getPoints();
//					sinkedShips += map.returnOneShipsAllLocationAsString(targetedLocation.ship, map); 
					sunkShips += "\t" + targetedLocation.ship.getType() + ": " + targetedLocation.ship.positions
							+ "\n";
//					sinkedShips += targetedLocation.ship.positionsArray.toString();
					targetedLocation.ship.destroyShip();
					bombsRemaining--;
					tries++;
				}
				bombedLocations += " (" + rowInputAsString + ", " + columnInputAsString + ")";
				targetedLocation.bombedAt = true;
			}
			JOptionPane.showMessageDialog(null, message);
		}

		// Presenting score to the user if the game is completed
		gameOverMessage = "Game Over";
		gameOverMessage += "\nScore: " + score;
		gameOverMessage += "\nNumber of tries " + tries;
		gameOverMessage += "\nShips sinked";
		gameOverMessage += "\n" + sunkShips;

		JOptionPane.showMessageDialog(null, gameOverMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);

		player.checkHighestScore(score);
		return null;
	}

	// This method takes in a user name and generates and returns a new user object
	public static User player(String name) {
		User player;

		player = new User(name);

		return player;
	}

	// This method reads in user objects from a text file and returns an ArrayList
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
		} catch (FileNotFoundException fileNotFound) {
			System.out.println("User file not found");
		} catch (Exception e) {
			System.out.println("Error reading User file in");
		}

		return output;

	}

	public static ArrayList<SavedGame> readSavedGamesIn() {
		ArrayList<SavedGame> output;
		FileInputStream fileInput;
		ObjectInputStream objectInput;
		SavedGame game;

		output = new ArrayList<SavedGame>();
		try {
			fileInput = new FileInputStream(new File("savedGames.txt"));
			objectInput = new ObjectInputStream(fileInput);
			while (true) {
				try {
					game = (SavedGame) objectInput.readObject();
					output.add(game);
				} catch (EOFException endOfFile) {
					break;
				}
			}
			fileInput.close();
			objectInput.close();
		} catch (FileNotFoundException fileNotFound) {
			System.out.println("Saved Games File not found");
		} catch (Exception e) {
			System.out.println("Error reading Saved Games file in");
		}

		return output;
	}

	// This method writes the user objects out to a .txt file
	public static void writeOutUsers(ArrayList<User> users) {

		// Method variables
		FileOutputStream fileOutput;
		ObjectOutputStream objectOutput;
		User user;

		try {
			fileOutput = new FileOutputStream(new File("userData.txt"));
			objectOutput = new ObjectOutputStream(fileOutput);

			for (int loop = 0; loop < users.size(); loop++) {
				user = users.get(loop);
				objectOutput.writeObject(user);
			}
		} catch (Exception e) {
			System.out.println("Error writing User Data out");
		}

	}

	public static void writeSavedGamesOut(ArrayList<SavedGame> savedGames) {
		FileOutputStream fileOutput;
		ObjectOutputStream objectOutput;
		SavedGame game;

		try {
			fileOutput = new FileOutputStream(new File("savedGames.txt"));
			objectOutput = new ObjectOutputStream(fileOutput);

			for (int loop = 0; loop < savedGames.size(); loop++) {
				game = savedGames.get(loop);
				objectOutput.writeObject(game);
			}
		} catch (Exception e) {
			System.out.println("Error writing saved games out");
		}

	}

	// This method checks if a user has been previously created based on the user
	// name input. If the user has already been created, it return the corresponding
	// user object. If not, it creates a new user object and returns it.
	public static User validateUser(String name, ArrayList<User> users) {
		User currentPlayer;
		User user;

		for (int loop = 0; loop < users.size(); loop++) {
			user = users.get(loop);
			if (user.getName().toLowerCase().equals(name.toLowerCase())) {
				currentPlayer = user;
				return currentPlayer;
			}
		}

		currentPlayer = player(name);
		return currentPlayer;

	}

	// This method generates a string representation of the high-score table
	public static void highScoreTable(ArrayList<User> users) {
		String output = "";
		String nameText = "Name: ";
		String scoreText = "Score: ";
		User user;

		Collections.sort(users, User.sortByUserScoreDesc);

		for (int loop = 0; loop < users.size(); loop++) {
			user = users.get(loop);
			output += String.format("%10d%10s%10d", (loop + 1), users.get(loop).getName(),
					users.get(loop).getHighestScore());
			output += "\n";
		}

		JOptionPane.showMessageDialog(null, output, "High-Score Table", JOptionPane.INFORMATION_MESSAGE);
	}

}// End of class
