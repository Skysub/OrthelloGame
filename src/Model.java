import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Model {

	View view;
	int emptyValue = Constants.EMPTY;
	int state = Constants.START;
	int nrCheckersToFlip;

	int boardSize;
	int whichColourTurn;
	int nrPlayers;
	Board gameBoard;

	PathGrid gamePathGrid; // For mapping possible paths
	int turnsTaken = 0; // We use this as a counter for the starting steps and the score

	int turnsSkipped = 0;

	boolean isGameOver = false;

	Model(View view, int boardSize, int nrPlayers) {
		this.view = view;
		this.boardSize = boardSize;
		this.nrPlayers = nrPlayers;

		Random randomObject = new Random();
		this.whichColourTurn = randomObject.nextInt(nrPlayers);

		this.gameBoard = new Board(boardSize);
		this.gamePathGrid = new PathGrid(boardSize);
	}




	void step(int[] coords) {
		switch (this.state) {

			// Start
			case Constants.START:
				boolean moveResult = startingMove(coords);
				recordTurnTaken(moveResult);

				// Each player gets to put 2 checkers on the board
				if (this.turnsTaken % 2 == 0 && turnsTaken>0 && moveResult) {
					this.setNextTurn();
				}

				//After each player has taken 2 turns, we start the main part of the game
				if (turnsTaken == nrPlayers * 2) {
					this.state = Constants.PLACEMENT; // Now we place a brick
					this.calculatePossiblePaths();
				}

				break;
			// Place checkers
			case Constants.PLACEMENT:
				recordTurnTaken(placementMove(coords));
				this.calculatePossiblePaths();

				// If there are now moves
				if (getNrNonNullPaths() == 0) {
					this.state = Constants.TURN_SKIPPED; // Skip
				}

				break;

			// Turn has been skipped
			case Constants.TURN_SKIPPED:
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
				break;
				// C'est le end
			case Constants.GAME_ENDED:
				System.out.println("GAME ENDED");
				// Play the most dramatic ending game music ever
				setEndingScreenForView();
				break;
		}

		view.updateBoard(this.gameBoard);
	}

	boolean startingMove(int[] coords) {

		// Can't place outside the center square, can only place where there is no
		// checker
		if (isLegalStartingMove(coords)) {
			this.flipChecker(coords);

			return true;
		}

		return false;
	}



	public boolean placementMove(int[] coords) {

		// We check if the coordinates correspond to the starting coordinates of any path
		//If it does, we flip all checkers (include the one in the starting coordinates) to the current player's colour
		if (this.gamePathGrid.pathExists(coords)) {
			Path pathChosen = getPathFromCoords(coords);
			flipCheckersInPath(pathChosen);
			this.setNextTurn();
			this.gamePathGrid.resetGrid();
			return true;
		}

		return false;
	}

	// Checks if a player has the same colour as the checker he/she wishes to flip
	boolean isNotAlreadyFlipped(Checker chosenChecker) {
		return (this.whichColourTurn != chosenChecker.getState());
	}

	// In other Orthello games, which are 1-indexed, the "center" contains the
	// indices 4 and 5
	boolean isLegalStartingMove(int[] coords) {
		int center_coord = this.boardSize / 2;
		return gameBoard.isWithinSquare(coords, center_coord - 1, center_coord + 1)
				&& (gameBoard.getElementAt(coords).isEmpty());
	}

	void recordTurnTaken(Boolean moveValue) {
		if (moveValue) {
			this.turnsTaken += 1;
		}
	}

	// Sets status of the checker to be flipped to the current player's colour
	void flipChecker(Checker chosenChecker) {
		setStateOfChecker(chosenChecker, this.whichColourTurn);
	}

	void flipChecker(int[] coords) {
		setStateOfChecker(coords, this.whichColourTurn);
	}

	void setStateOfChecker(int[] coords, int newState) {
		Checker chosenChecker = getCheckerFromCoords(coords);
		chosenChecker.state = newState;
	}

	void setStateOfChecker(Checker chosenChecker, int newState) {
		chosenChecker.state = newState;
	}

	void setNextTurn() {
		this.whichColourTurn = ++this.whichColourTurn % this.nrPlayers;
		view.updateCurrentPlayer(this.whichColourTurn);
	}

	Checker getCheckerFromCoords(int[] coords) {
		return this.gameBoard.getElementAt(coords);
	}

	Path getPathFromCoords(int[] coords) {
		return this.gamePathGrid.getElementAt(coords);
	}

	void resetGrid() {
		this.gamePathGrid.resetGrid();
	}

	// RandomlyAddsPaths to the PathGrid
	void calculatePossiblePaths() {
		for (int i = 0; i < this.boardSize; i++) {

			Path horizontalPath = new Path();
			Path verticalPath = new Path();
			Path diagonalTopToBottomPath = new Path();
			Path diagonalBottomToRightPath = new Path();
			Path diagonalTopToLeftPath = new Path();

			for (int j = 0; j < this.boardSize; j++) {

				int[] horizontalCoords = getHorizontalCoords(i, j);
				int[] verticalCoords = getVerticalCoords(i, j);

				int[] diagonalTopToBottomCoords = getDiagonalTopToBottomCoords(i, j);
				int[] diagonalBotttomToRightCoords = getDiagonalBottomToRightCoords(i, j);
				int[] diagonalTopToLeftCoords = getDiagonalTopToLeftCoords(i,j);

				//Calculate the vertical and horizontal paths
				horizontalPath = iteratePathAlgorithm(horizontalCoords, horizontalPath);
				verticalPath = iteratePathAlgorithm(verticalCoords, verticalPath);

				// The diagonal paths are dependent on this guard
				if (j < boardSize - i) {
					diagonalTopToBottomPath = iteratePathAlgorithm(diagonalTopToBottomCoords, diagonalTopToBottomPath);
					diagonalBottomToRightPath = iteratePathAlgorithm(diagonalBotttomToRightCoords, diagonalBottomToRightPath);
					diagonalTopToLeftPath = iteratePathAlgorithm(diagonalTopToLeftCoords,diagonalTopToLeftPath);
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
		return new int[] { j, i + j };
	}

	int[] getDiagonalBottomToRightCoords(int i, int j) {
		return new int[] { i+ j, this.boardSize - (1 + j) };
	}

	int[] getDiagonalTopToLeftCoords(int i, int j){
		return new int[] {this.boardSize - (1 + j + i),j};
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
			setStartingCoordsOfPathToCheckers(currentPath, currentChecker);
			currentPath = foundPossiblePath(currentPath);
		}
		// Empty V !CSeen <=> !C... Ø... C or Ø...C
		else {
			resetPath(currentPath);
		}

		// We set the starting coords of the path here regardless
		setStartingCoordsOfPathToCheckers(currentPath, currentChecker);
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

	void setStartingCoordsOfPathToCheckers(Path currentPath, Checker chosenChecker) {
		currentPath.setCoords(chosenChecker.coordinates);
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

	void flipCheckersInPath(Path chosenPath){
		chosenPath.flipCheckersInPath(this.whichColourTurn);
	}

	//The current way we calculate the score is just to count how many checkers each player has
	int[] calculateScoreArray(){
		int[] scoreArray = countCheckersForEachPlayerArray();
		return scoreArray;
	}

	//Returns an array the size of nrPlayers, where each element signifies the number of checker's with that player's colour
	int[] countCheckersForEachPlayerArray(){
		int[] countArray = new int[nrPlayers];

		for(int i = 0; i<boardSize;i++){
			for(int j = 0; j<boardSize;j++){
				Checker currentChecker = this.gameBoard.getElementAt(new int[] {i,j});
				if(!currentChecker.isEmpty()){
					countArray[currentChecker.getState()] += 1;
				}
			}
		}
		return countArray;
	}

	int calculateWinnerNr(int[] scoreArray){

		//Empty value is -1
		int currentHighestScore = -1;
		int currentBestPlayer = -1;
		for(int idx = 0; idx<nrPlayers;idx++){
			//Vi antager, at der ikke er en eneste spiller uden en eneste checker
			if(scoreArray[idx]>currentHighestScore){
				currentBestPlayer = idx;
				currentHighestScore = scoreArray[idx];
			}

		}

		return currentBestPlayer;
	}

	void setEndingScreenForView(){
		int[] scoreArray = calculateScoreArray();
		int winnerNr = calculateWinnerNr(scoreArray);
		view.endGame(winnerNr,scoreArray[winnerNr]);
	}

}

class DebugModel extends Model {

	DebugModel(View view, int size, int nrPlayers) {
		super(view ,size, nrPlayers);
	}

	void setColumnCheckers(int x, int lower, int upper, int newState) {
		for (int y = lower; y < upper; y++) {
			int[] currentCoords = { x, y };
			super.setStateOfChecker(currentCoords, newState);
		}
	}
}

abstract class Element{

	int emptyValue = Constants.EMPTY;

	//All coords start as undefined/Empty
	int[] coordinates = new int[] {emptyValue,emptyValue};

	abstract boolean isEmpty();
}
class Checker extends Element{

	//Initialiserer dette som en tom værdi
	int state = emptyValue ;

	Checker(int x, int y) {
		this.coordinates[0] = x;
		this.coordinates[1] = y;
	}

	boolean isEmpty() {
		return this.state == emptyValue;
	}

	int getState() {
		return state;
	}

	void setState(int newState){
		this.state = newState;
	}

}


class Grid<E> {
	int gridSize;

	int emptyValue = Constants.EMPTY;
	E[] gridArray;

	/*
	 * Koden er baseret på kode skrevet af user4910279, link:
	 * https://stackoverflow.com/a/45045080/12190113
	 */
	Grid(int size, E... classtype) {
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

class Board extends Grid<Checker> {

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

}

class PathGrid extends Grid<Path> {

	int nrNonNullPaths = 0;
	ArrayList<Path> nonNullPaths = new ArrayList<Path>();

	PathGrid(int boardSize) {
		super(boardSize);
	}

	// Turns all indices in the pathGrid into null pointers pointing towards Path
	// objects
	void resetGrid() {
		nonNullPaths = new ArrayList<Path>();
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

class Path extends Element{
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

	void flipCheckersInPath(int newState){
		int sizeOfPath = this.getSizeOfPath();
		for(int i = 0; i<sizeOfPath;i++){
			this.checkersInPath.get(i).setState(newState);
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