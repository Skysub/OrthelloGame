/*
Skrevet af: Benjamin Mirad Gurini
Studienummer: s214590
*/

import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

class Turn implements Serializable {

	private static final long serialVersionUID = 514006825282299253L;
	int[] coordinates;
	int gameStateAtTurnTaken = Constants.UNDEFINED;
	int playerIndex;

	Turn(int[] coordinates, int gameStateAtTurnTaken, int playerIndex) {
		this.coordinates = coordinates;
		this.gameStateAtTurnTaken = gameStateAtTurnTaken;
		this.playerIndex = playerIndex;
	}
}

class Player {
	private String playerName;
	private int score;

	//Unused - would've been used to calculate the score
	private ArrayList<Turn> turnHistory = new ArrayList<Turn>();
	private Color playerColor;

	//Whenever we flip a checker belonging to this player we decrement, otherwise if we flip a checker to this player's favour we increment this variable
	private int nrCheckers;
	private Ai AIObject;

	Player(String name, Color playerColor, AIModes aiMode) {
		this.playerName = name;
		this.playerColor = playerColor;
		this.decideAIMode(aiMode);

		if (isAI()) {
			this.playerName += " (AI)";
		}
	}

	// Public Getters
	Color getPlayerColor() {
		return this.playerColor;
	}

	int getScore() {
		return this.score;
	}
	
	ArrayList<Turn> getTurnHistory() {
		return this.turnHistory;
	}

	int getNrCheckers() {
		return this.nrCheckers;
	}

	String getPlayerName() {
		return this.playerName;
	}

	int[] getAICalculatedCoords(ArrayList<Path> nonNullPaths) {
		return this.AIObject.AIGetSteppingCoords(nonNullPaths);
	}

	void decideAIMode(AIModes aiMode) {
		switch (aiMode) {
		case HumanPlayer -> {
		}
		case AIGreedy -> {
			this.AIObject = new GreedyAI();
		}
		case AIRandom -> {
			this.AIObject = new RandomAI();
		}
		case AIWeighted -> {
			this.AIObject = new WeightedAI();
		}
		}
	}

	void recordTurn(Turn newTurn) {
		turnHistory.add(newTurn);
	}

	void decreaseNumberOfCheckers() {
		this.nrCheckers -= 1;
	}

	void increaseNumberOfCheckers() {
		this.nrCheckers += 1;
	}

	// Method for calculating and updating score, in case we use a different measurement for score in the future
	int calculateScore() {
		this.score = this.getNrCheckers();
		return this.score;
	}

	boolean isAI() {
		return Objects.nonNull(this.AIObject);
	}
}

class PlayerManager {

	//A list of size nrPlayers
	ArrayList<Player> players = new ArrayList<Player>();
	int highScore = Constants.UNDEFINED;
	int currentPlayerIndex;
	int nrPlayers;
	int nrTurnsTaken;
	int firstPlayerIndex;

	PlayerManager(int nrPlayers, ArrayList<javafx.scene.paint.Color> playerColors, ArrayList<String> playerNames,
			AIModes[] playerAIModes) {
		this.nrPlayers = nrPlayers;
		for (int i = 0; i < nrPlayers; i++) {
			Color currentColor = playerColors.get(i);
			String currentName = playerNames.get(i);
			AIModes currentAIMode = playerAIModes[i];
			Player newPlayer = new Player(currentName, currentColor, currentAIMode);
			this.addPlayer(newPlayer);
		}

		Random randomObject = new Random();
		this.currentPlayerIndex = randomObject.nextInt(nrPlayers);

	}

	void addPlayer(Player newPlayer) {
		players.add(newPlayer);
	}

	/*
	 * Sets the class variable highScore and gets an arrayList, where: For all
	 * elements in the calculated arrayList, there exists no element in the
	 * playersArray, that has a higher score. For all elements in the calculated
	 * arrayList, all elements in the playersArray have the same or lower score.
	 */
	ArrayList<Player> setHighScoreAndGetHighestScoringPlayers() {
		ArrayList<Player> highestScoringPlayersArray = new ArrayList<Player>();

		for (Player currentPlayer : this.players) {

			currentPlayer.calculateScore();
			int currentScore = currentPlayer.getScore();

			// Hvis playeren har samme score som highscoren tilføjer vi det til arrayet
			if (currentScore == this.highScore) {
				highestScoringPlayersArray.add(currentPlayer);
			}
			// Hvis denne playeren har højere score end den nuværende highscore, betyder
			// det, at alle andre i arrayet har lavere score end denne player,
			// og vi genstarter derfor arrayet.
			else if (currentScore > this.highScore) {
				highestScoringPlayersArray = new ArrayList<Player>();
				highestScoringPlayersArray.add(currentPlayer);
				this.highScore = currentPlayer.getScore();
			}
		}
		return highestScoringPlayersArray;
	}

	Player getPlayerAtIndex(int i) {
		return players.get(i);
	}

	ArrayList<Player> getPlayers() {
		return players;
	}

	void setFirstPlayerIndex(int index) {
		firstPlayerIndex = index;
	}
	
	int getFirstPlayerIndex() {
		return firstPlayerIndex;
	}

	//Used to check if the gameBoard is filled up
	int getSumOfCheckersPlaced(){
		int sum = 0;
		for(int i = 0; i<players.size();i++){
			sum += getPlayerAtIndex(i).getNrCheckers();
		}
		return sum;
	}
}