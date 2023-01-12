import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Model {

	View view;
	int state = Constants.START;

	int boardSize;

	PlayerManager gamePlayerManager;
	int currentPlayerIndex;
	Player currentPlayer;
	int nrPlayers;
	Board gameBoard;

	PathGrid gamePathGrid; // For mapping possible paths
	int turnsTaken = 0; // We use this as a counter for the starting steps and the score

	int turnsSkipped = 0;

	boolean isGameOver = false;

	Model(View view, int boardSize, int nrPlayers, ArrayList<Color> playerColors, ArrayList<String> playerNames) {
		this.view = view;
		this.boardSize = boardSize;
		this.nrPlayers = nrPlayers;
		this.gameBoard = new Board(boardSize);
		this.gamePathGrid = new PathGrid(boardSize);
		this.gamePlayerManager = new PlayerManager(nrPlayers,playerColors,playerNames);
	}


	void endGame(){
		System.out.println("GAME ENDED");
		//TODO: Play the most dramatic ending game music ever
		setEndingScreenForView();
		view.updateBoard(gameBoard);
	}

	void skipTurn(){
		// In order to get to this state, we need to skip a turn
		this.turnsSkipped += 1;
		System.out.println("TURN SKIPPED");

		// See if the next person can play their turn
		setNextTurn();
		calculatePossiblePaths();
		int nrPossiblePaths = getNrNonNullPaths();

		// No one has been able to play
		if (this.turnsSkipped == nrPlayers) {
			state = Constants.GAME_ENDED; // End the game
			isGameOver = true;
			// The next player also has no possible moves
		} else if (nrPossiblePaths == 0) {
			this.gamePathGrid.resetGrid();
			state = Constants.TURN_SKIPPED;
			// The next player has possible moves
		} else {
			state = Constants.PLACEMENT; // We can now place a brick
			this.turnsSkipped = 0; // we reset the counter
		}

		view.updateBoard(this.gameBoard);
	}

	void step(int[] coords) {
		Turn currentTurn = new Turn(coords);

		switch (this.state) {

			// Start
			case Constants.START -> {
				boolean moveResult = startingMove(coords);
				recordTurnTaken(moveResult,currentTurn);

				// Each player gets to put 2 checkers on the board
				if (this.turnsTaken % 2 == 0 && turnsTaken > 0 && moveResult) {
					this.setNextTurn();
				}

				//After each player has taken 2 turns, we start the main part of the game
				if (turnsTaken == nrPlayers * 2) {
					this.state = Constants.PLACEMENT; // Now we place a brick
					this.calculatePossiblePaths();
				}
			}
			// Place checkers
			case Constants.PLACEMENT -> {
				recordTurnTaken(placementMove(coords),currentTurn);

				// If there are no moves
				if (getNrNonNullPaths() == 0) {
					this.state = Constants.TURN_SKIPPED; // Skip
				}
			}
		}

		view.updateBoard(this.gameBoard);
	}

	boolean startingMove(int[] coords) {

		// Can't place outside the center square, can only place where there is no
		// checker
		if (isLegalStartingMove(coords)) {
			Checker currentChecker = this.gameBoard.getElementAt(coords);
			currentChecker.flipChecker(currentPlayer);

			return true;
		}

		return false;
	}



	public boolean placementMove(int[] coords) {

		// We check if the coordinates correspond to the starting coordinates of any path
		//If it does, we flip all checkers (include the one in the starting coordinates) to the current player's colour
		if (this.gamePathGrid.pathExists(coords)) {
			Path pathChosen = getPathFromCoords(coords);
			pathChosen.flipCheckersInPath(this.currentPlayer);
			this.setNextTurn();
			this.gamePathGrid.resetGrid();
			this.calculatePossiblePaths();
			return true;
		}

		return false;
	}

	// Checks if a player has the same colour as the checker he/she wishes to flip
	boolean isNotAlreadyFlipped(Checker chosenChecker) {
		return (this.currentPlayer != chosenChecker.getState());
	}

	// In other Orthello games, which are 1-indexed, the "center" contains the
	// indices 4 and 5
	boolean isLegalStartingMove(int[] coords) {
		int center_coord = this.boardSize / 2;
		return gameBoard.isWithinSquare(coords, center_coord - 1, center_coord + 1)
				&& (gameBoard.getElementAt(coords).isEmpty());
	}

	void recordTurnTaken(Boolean moveValue,Turn turnTaken) {
		if (moveValue) {
			this.turnsTaken += 1;
			this.currentPlayer.recordTurn(turnTaken);
		}
	}

	void setNextTurn() {
		this.currentPlayerIndex = ++this.currentPlayerIndex % this.nrPlayers;
		this.currentPlayer = this.gamePlayerManager.getPlayerAtIndex(currentPlayerIndex);
		view.updateCurrentPlayer(this.currentPlayerIndex);
	}

	Checker getCheckerFromCoords(int[] coords) {
		return this.gameBoard.getElementAt(coords);
	}

	Path getPathFromCoords(int[] coords) {
		return this.gamePathGrid.getElementAt(coords);
	}


	// RandomlyAddsPaths to the PathGrid
	void calculatePossiblePaths() {
		for (int i = 0; i < this.boardSize; i++) {

			Path horizontalPath = new Path();
			Path verticalPath = new Path();
			Path diagonalTopToBottomPath = new Path();
			Path diagonalBottomToRightPath = new Path();
			Path diagonalTopToLeftPath = new Path();
			Path diagonalTopToRightPath = new Path();

			for (int j = 0; j < this.boardSize; j++) {

				int[] horizontalCoords = getHorizontalCoords(i, j);
				int[] verticalCoords = getVerticalCoords(i, j);

				int[] diagonalTopToBottomCoords = getDiagonalTopToBottomCoords(i, j);
				int[] diagonalBotttomToRightCoords = getDiagonalBottomToRightCoords(i, j);
				int[] diagonalTopToLeftCoords = getDiagonalTopToLeftCoords(i,j);
				int[] diagonalTopToRightCoords = getDiagonalTopToRightCoords(i,j);

				//Calculate the vertical and horizontal paths
				horizontalPath = iteratePathAlgorithm(horizontalCoords, horizontalPath);
				verticalPath = iteratePathAlgorithm(verticalCoords, verticalPath);

				// The diagonal paths are dependent on this guard
				if (j < boardSize - i) {
					diagonalTopToBottomPath = iteratePathAlgorithm(diagonalTopToBottomCoords, diagonalTopToBottomPath);
					diagonalBottomToRightPath = iteratePathAlgorithm(diagonalBotttomToRightCoords, diagonalBottomToRightPath);
					diagonalTopToLeftPath = iteratePathAlgorithm(diagonalTopToLeftCoords,diagonalTopToLeftPath);
					diagonalTopToRightPath = iteratePathAlgorithm(diagonalTopToRightCoords,diagonalTopToRightPath);
				}
			}
		}
	}

	// Top to down
	int[] getHorizontalCoords(int[] originalCoords) {
		return originalCoords;
	}

	int[] getHorizontalCoords(int i, int j) {
		return new int[] { i, j };
	}

	// Left to right
	int[] getVerticalCoords(int[] originalCoords) {
		return new int[] { originalCoords[1], originalCoords[0] };
	}

	int[] getVerticalCoords(int i, int j) {
		return new int[] { j, i };
	}

	int[] getDiagonalTopToBottomCoords(int i, int j) {
		return new int[] { j+i,  j };
	}

	int[] getDiagonalBottomToRightCoords(int i, int j) {
		return new int[] { i+ j, this.boardSize - (1 + j) };
	}

	int[] getDiagonalTopToLeftCoords(int i, int j){
		return new int[] {this.boardSize - (1 + j + i),j};
	}

	int[] getDiagonalTopToRightCoords(int i, int j){
		return new int[] { j,  j+i };
	}


	Path iteratePathAlgorithm(int[] coords, Path currentPath) {

		Checker currentChecker = getCheckerFromCoords(coords);

		// The checker is empty
		if (currentChecker.isEmpty()) {
			currentPath = foundEmptyChecker(currentPath, currentChecker);
		}

		// The checker is of the opponent's colour
		else if (isNotAlreadyFlipped(currentChecker)) {
			foundCheckerNotOurColour(currentPath, currentChecker);
		}

		// If checker isn't of opponent's colour or empty it is of our
		else {
			currentPath = foundCheckerOfSameColour(currentPath, currentChecker);
		}

		return currentPath;
	}

	/*
	 * Used in the calculate possible paths algorithm. Used when we've observed a
	 * possible path for the player to select.
	 */
	Path foundPossiblePath(Path chosenPath) {
		//We add the checker at the path's starting coordinate to the Path
		Checker startingCoordinatesChecker = getCheckerFromCoords(chosenPath.coordinates);
		chosenPath.addCheckerToPath(startingCoordinatesChecker);
		this.gamePathGrid.addPathToGrid(chosenPath);
		chosenPath = new Path();

		return chosenPath;
	}

	void foundCheckerNotOurColour(Path currentPath, Checker chosenChecker) {
		currentPath.addCheckerToPath(chosenChecker);
	}

	Path foundEmptyChecker(Path currentPath, Checker currentChecker) {
		// !Empty && CSeen <=> C !C... Ø
		if (!currentPath.isEmpty() && currentPath.getStatusOfCurrentColourSeen()) {
			currentPath.setCoords(currentChecker.coordinates);
			currentPath = foundPossiblePath(currentPath);
		}
		// Empty V !CSeen <=> !C... Ø... C or Ø...C
		else {
			resetPath(currentPath);
		}

		// We set the starting coords of the path here regardless
		currentPath.setCoords(currentChecker.coordinates);
		return currentPath;
	}

	Path foundCheckerOfSameColour(Path currentPath, Checker currentChecker) {

		// HasCoords && !empty <=> Ø !C... C
		if (!currentPath.isEmpty() && currentPath.hasCoords()) {
			currentPath = foundPossiblePath(currentPath);
			// Not above implies either: C !C ... C V !C ... Ø... C C
			// We reset the path and set SeenC true
		} else {
			resetPath(currentPath);
		}

		currentPath.setStatusOfCurrentColourSeen(true);
		return currentPath;

	}


	void resetPath(Path chosenPath) {
		chosenPath.resetPath();
	}

	ArrayList<Path> getListOfNonNullPaths() {
		return this.gamePathGrid.getNonNullPaths();
	}

	int getNrNonNullPaths() {
		return this.gamePathGrid.getNrNonNullPaths();
	}

	int getBoardSize() {
		return this.boardSize;
	}


	void setEndingScreenForView(){
		ArrayList<Player> winnersArrayList = this.gamePlayerManager.setHighScoreAndGetHighestScoringPlayers();
		view.endGame(winnersArrayList, gamePlayerManager.highScore);
	}

}



abstract class BoardElement {

	int emptyValue = Constants.EMPTY;

	//All coords start as undefined/Empty
	int[] coordinates = new int[] {emptyValue,emptyValue};

	abstract boolean isEmpty();
}
class Checker extends BoardElement {

	Player state;

	Checker(int x, int y) {
		this.coordinates[0] = x;
		this.coordinates[1] = y;
	}

	boolean isEmpty() {
		return Objects.isNull(this.state);
	}

	Player getState() {
		return state;
	}

	void flipChecker(Player newPlayer){
		if(!isEmpty()){
			Player oldPlayer = this.state;
			oldPlayer.decreaseNumberOfCheckers();
		}
		newPlayer.increaseNumberOfCheckers();
		this.state = newPlayer;
	}

}


class TwoDimensionalGrid<E> {
	int gridSize;

	int emptyValue = Constants.EMPTY;
	E[] gridArray;

	/*
	 * Koden er baseret på kode skrevet af user4910279, link:
	 * https://stackoverflow.com/a/45045080/12190113
	 */
	TwoDimensionalGrid(int size, E... classtype) {
		this.gridSize = size;
		gridArray = Arrays.copyOf(classtype, gridSize * gridSize);
	}

	int getPositionFromCoords(int[] coords) {
		return coords[0] + coords[1] * this.gridSize;
	}

	// Assuming they're 0-indexed
	int getPositionFromCoords(int x, int y) {
		return x + y * this.gridSize;
	}

	E getElementAt(int[] coords) {
		int position = this.getPositionFromCoords(coords);
		return this.gridArray[position];
	}

	E getElementAt(int position) {
		return this.gridArray[position];
	}

	void setElementAt(int[] coords, E element) {
		int position = this.getPositionFromCoords(coords);
		this.gridArray[position] = element;
	}

	void setElementAt(int position, E element) {
		this.gridArray[position] = element;
	}

	E[] getState() {
		return this.gridArray;
	}

}

class Board extends TwoDimensionalGrid<Checker> {

	Board(int size) {
		super(size);

		// We fill the board in with empty checkers
		this.fillInitialBoard();
	}

	// Method for filling in the boardSize x boardSize 2D array with empty checkers
	private void fillInitialBoard() {

		for (int x_0 = 0; x_0 < this.gridSize; x_0++) {
			for (int y_0 = 0; y_0 < this.gridSize; y_0++) {
				int position = getPositionFromCoords(x_0, y_0);
				this.gridArray[position] = new Checker(x_0, y_0);
			}

		}

	}

	public boolean isWithinBoard(int[] coords) {
		return isWithinSquare(coords, 0, this.gridSize);
	}

	/*
	 * Checks if the coords are within a designated square - primarily used for the
	 * starting scenario. Lower:inclusive, Upper: Exclusive f(x,y) = true <=>
	 * lower<=x<upper && lower<=y<upper
	 */
	public boolean isWithinSquare(int[] coords, int lower, int upper) {
		int x = coords[0];
		int y = coords[1];
		return (x >= lower && x < upper && y >= lower && y < upper);
	}

	void flipCheckerAtCoords(int[] coords, Player newPlayer){
		Checker chosenChecker = getElementAt(coords);
		chosenChecker.flipChecker(newPlayer);
	}

}

class PathGrid extends TwoDimensionalGrid<Path> {

	int nrNonNullPaths = 0;
	ArrayList<Path> nonNullPaths = new ArrayList<Path>();

	PathGrid(int boardSize) {
		super(boardSize);
	}

	// Turns all indices in the pathGrid into null pointers pointing towards Path
	// objects
	void resetGrid() {
		nonNullPaths = new ArrayList<Path>();
		nrNonNullPaths = 0;
		gridArray = new Path[gridSize * gridSize];
	}

	/*
	 * Adds the path to the gridArray
	 *
	 * If two paths have the same starting coords, we will just concat the checkers
	 * in their paths
	 */
	void addPathToGrid(Path newPath) {
		int positionOfNewPath = this.getPositionFromCoords(newPath.coordinates);
		Path currentPath = this.getElementAt(positionOfNewPath);

		// There already exists a path from this coordinate
		if (java.util.Objects.nonNull(currentPath)) {
			currentPath.concatCheckersInPaths(newPath);
		} else {
			this.setElementAt(positionOfNewPath, newPath);
			this.nonNullPaths.add(0, newPath);
			this.nrNonNullPaths += 1;
		}

	}

	boolean pathExists(Path chosenPath) {
		return java.util.Objects.nonNull(chosenPath);
	}

	boolean pathExists(int[] coords) {
		Path chosenPath = getElementAt(coords);
		return java.util.Objects.nonNull(chosenPath);
	}

	ArrayList<Path> getNonNullPaths() {
		return this.nonNullPaths;
	}

	int getNrNonNullPaths() {
		return nrNonNullPaths;
	}

}

class Path extends BoardElement {
	ArrayList<Checker> checkersInPath = new ArrayList<Checker>();
	boolean currentPlayerColourSeen;
	int sizeOfPath = 0; // For the AI later on

	// Appends the checkersInPath of another path to this' - used for when multiple
	// paths for the same placement of a checker
	void concatCheckersInPaths(Path otherPath) {
		this.checkersInPath.addAll(otherPath.checkersInPath); // None of the checkers will overlap and count twice, so
		// we can append all
		this.updateSizeOfPath();
	}

	// Checks if a checker is in this path
	boolean isCheckerInPath(Checker chosenChecker) {
		return this.checkersInPath.contains(chosenChecker);
	}

	void addCheckerToPath(Checker chosenChecker) {
		this.checkersInPath.add(chosenChecker);
		this.updateSizeOfPath();
	}

	boolean getStatusOfCurrentColourSeen() {
		return this.currentPlayerColourSeen;
	}

	void setStatusOfCurrentColourSeen(boolean status) {
		this.currentPlayerColourSeen = status;
	}

	int getSizeOfPath() {
		return sizeOfPath;
	}

	void flipCheckersInPath(Player newPlayer){
		int sizeOfPath = this.getSizeOfPath();
		for(int i = 0; i<sizeOfPath;i++){
			this.checkersInPath.get(i).flipChecker(newPlayer);
		}
	}

	boolean isEmpty() {
		return getSizeOfPath() == 0;
	}

	void updateSizeOfPath() {
		this.sizeOfPath = this.checkersInPath.size();
	}

	void resetCheckersInPath() {
		this.checkersInPath = new ArrayList<Checker>();
		updateSizeOfPath();
	}

	void resetCoords() {
		this.coordinates = new int[] { emptyValue, emptyValue };
	}

	void resetCurrentPlayerColourSeen() {
		this.currentPlayerColourSeen = false;
	}

	void resetPath() {
		resetCheckersInPath();
		resetCoords();
		resetCurrentPlayerColourSeen();
	}

	void setCoords(int[] coords) {
		this.coordinates = coords;
	}

	boolean hasCoords() {
		return coordinates[0] != this.emptyValue;
	}
}

class Turn{

	int[] coordinates;
	int timeTaken;

	Turn(int[] coordinates){
		this.coordinates = coordinates;
	}
}

class Player{
	private String playerName;
	private int score;
	private ArrayList<Turn> turnHistory;
	private Color playerColor;
	private int nrCheckers;
	private boolean isPlayer;


	Player(String name,Color playerColor){
		this.playerName = name;
		this.playerColor = playerColor;
	}

	void recordTurn(Turn newTurn){
		turnHistory.add(newTurn);
	}

	void decreaseNumberOfCheckers(){
		this.nrCheckers -= 1;
	}

	void increaseNumberOfCheckers(){
		this.nrCheckers += 1;
	}

	Color getPlayerColor(){
		return this.playerColor;
	}

	int getScore(){
		return this.score;
	}

	int getNrCheckers(){ return this.nrCheckers;}

	//Calculates and updates the score
	int calculateScore(){
		this.score = this.getNrCheckers();
		return this.score;
	}

}

class PlayerManager {
	ArrayList<Player> playersArray = new ArrayList<Player>();
	int highScore = Constants.UNDEFINED;
	int currentPlayerIndex;
	int nrPlayers;
	int nrTurnsTaken;

	PlayerManager(int nrPlayers, ArrayList<javafx.scene.paint.Color> playerColors, ArrayList<String> playerNames){
		this.nrPlayers = nrPlayers;
		for(int i = 0; i<nrPlayers; i++){
			Color currentColor = playerColors.get(i);
			String currentName = playerNames.get(i);
			Player newPlayer = new Player(currentName,currentColor);
		}

		Random randomObject = new Random();
		this.currentPlayerIndex = randomObject.nextInt(nrPlayers);

	}
	void addPlayer(Player newPlayer){
		playersArray.add(newPlayer);
	}

	/*
	Sets the class variable highScore and gets an arrayList, where:
	For all elements in the calculated arrayList, there exists no element in the playersArray, that has a higher score.
	For all elements in the calculated arrayList, all elements in the playersArray have the same or lower score.
	 */
	ArrayList<Player> setHighScoreAndGetHighestScoringPlayers(){
		ArrayList<Player> highestScoringPlayersArray = new ArrayList<Player>();

		for (Player currentPlayer : this.playersArray) {

			currentPlayer.calculateScore();
			int currentScore = currentPlayer.getScore();

			//Hvis playeren har samme score som highscoren tilføjer vi det til arrayet
			if (currentScore == this.highScore) {
				highestScoringPlayersArray.add(currentPlayer);


			}

			/*
				Hvis denne playeren har højere score end den nuværende highscore, betyder det, at alle andre i arrayet har lavere score end denne player,
				og vi genstarter derfor arrayet.
				 */
			else if (currentScore > this.highScore) {
				highestScoringPlayersArray = new ArrayList<Player>();
				highestScoringPlayersArray.add(currentPlayer);
				this.highScore = currentPlayer.getScore();
			}

		}
		return highestScoringPlayersArray;
	}

	Player getPlayerAtIndex(int i){
		return this.playersArray.get(i);
	}







}