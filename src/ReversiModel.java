/*

Skrevet af: Benjamin Mirad Gurini
Studienummer: S214590


 */

import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;


public class ReversiModel{
	GameView GameView;
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

    int lastStartingIndex = Settings.previousStartingIndex;

    void selectStartingPlayer() {
        if(Settings.previousStartingIndex == Constants.UNDEFINED){
            Random randomObject = new Random();
            this.currentPlayerIndex = randomObject.nextInt(nrPlayers);
            Settings.previousStartingIndex = this.currentPlayerIndex;
            this.currentPlayer = gamePlayerManager.getPlayerAtIndex(currentPlayerIndex);
            gamePlayerManager.setFirstPlayerIndex(currentPlayerIndex);
        }
        else{
            Settings.previousStartingIndex = (Settings.previousStartingIndex + 1)%nrPlayers;
            this.currentPlayerIndex = Settings.previousStartingIndex;
            this.currentPlayer = gamePlayerManager.getPlayerAtIndex(currentPlayerIndex);
            GameView.updateTurnText(currentPlayer);
        }
    }
    
    void selectStartingPlayer(int index) {
        this.currentPlayerIndex = index;
        this.currentPlayer = gamePlayerManager.getPlayerAtIndex(currentPlayerIndex);
        gamePlayerManager.setFirstPlayerIndex(currentPlayerIndex);
    }

    ReversiModel(GameView view) {
        this.GameView = view;
        this.boardSize = Settings.boardSize;
        this.nrPlayers = Settings.nrPlayers;
        this.gameBoard = new Board(boardSize);
        this.gamePathGrid = new PathGrid(boardSize);
        this.gamePlayerManager = new PlayerManager(Settings.nrPlayers,Settings.playerColors,Settings.playerNames,Settings.playerAIModes);

        selectStartingPlayer();
    }
    void updateView(){
        GameView.updateBoard(this.gameBoard);
        GameView.updateTurnText(this.currentPlayer);
    }
    void endGame() {
        System.out.println("GAME ENDED");
        state = Constants.GAME_ENDED; // End the game
        isGameOver = true;
        setEndingScreenForView();
        GameView.updateBoard(gameBoard);
    }

    //Calculates the starting moves for the AI
    int[] AIStartingMove(){
        int center_coord = this.boardSize / 2;
        for(int x = center_coord; x >= center_coord - 1; x--){
            for(int y = center_coord; y >= center_coord - 1 ; y--){
                int[] possibleCoords = new int[] {x,y};
                if(isLegalStartingMove(possibleCoords)){
                    return possibleCoords;
                }
            }
        }
        return new int[] {Constants.UNDEFINED,Constants.UNDEFINED};
    }
    void skipTurn(Turn turntaken) {
        // In order to get to this state, we need to skip a turn
        this.turnsSkipped += 1;
        System.out.println("TURN SKIPPED");

        //If this function has been called, we know that we've taken a legal skipTurn move
        recordTurnTaken(true,turntaken);

        // See if the next person can play their turn
        setNextTurn();
        calculatePossiblePaths();
        int nrPossiblePaths = getNrNonNullPaths();

        // No one has been able to play
        if (this.turnsSkipped == nrPlayers) {
            this.endGame();
            // The next player also has no possible moves
        } else if (nrPossiblePaths == 0) {
            this.gamePathGrid.resetGrid();
            state = Constants.TURN_SKIPPED;
            // The next player has possible moves
        } else {
            state = Constants.PLACEMENT; // We can now place a tile
            this.turnsSkipped = 0; // we reset the counter
        }
    }

    void step(int[] coords) {

        Turn currentTurn = new Turn(coords, state, currentPlayerIndex);

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
                break;
            }

            case Constants.TURN_SKIPPED -> {
                // In order to get to this state, we need to skip a turn
                skipTurn(currentTurn);
                break;
            }
        }
        GameView.updateBoard(this.gameBoard);
        if(gameOverBeforeSkip() && this.state != Constants.GAME_ENDED){
            endGame();
        }
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
            Path pathChosen = this.gamePathGrid.getElementAt(coords);
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

    boolean gameOverBeforeSkip(){
        return isBoardFilled() || playerHasNoMoreCheckers();
    }

    boolean playerHasNoMoreCheckers(){
        return this.currentPlayer.getNrCheckers() == 0 && this.state != Constants.START;
    }
    boolean isBoardFilled(){
        return gamePlayerManager.getSumOfCheckersPlaced() == this.boardSize * this.boardSize;
    }

    // In other Othello games, which are 1-indexed, the "center" contains the
    // indices 4 and 5
    boolean isLegalStartingMove(int[] coords) {
        int center_coord = this.boardSize / 2;
        return gameBoard.isWithinSquare(coords, center_coord - 1, center_coord + 1)
                && (gameBoard.getElementAt(coords).isEmpty());
    }

    void recordTurnTaken(Boolean moveValue,Turn turnTaken) {
        if (moveValue) {
            this.turnsTaken += 1;
            gamePlayerManager.players.get(turnTaken.playerIndex).recordTurn(turnTaken);
        }
    }

    void setNextTurn() {
        this.currentPlayerIndex = ++this.currentPlayerIndex % this.nrPlayers;
        this.currentPlayer = this.gamePlayerManager.getPlayerAtIndex(currentPlayerIndex);
        GameView.updateTurnText(this.currentPlayer);
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

    //The algorithm that is based on the pattern C !C Ø
    void possiblePathsAlgorithm1(){
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

    int[] getHorizontalCoords(int i, int j) {
        return new int[] { i, j };
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
        Checker currentChecker = this.gameBoard.getElementAt(coords);
        // The checker is empty
        if (currentChecker.isEmpty()) {
            currentPath = foundEmptyChecker(currentPath, currentChecker);
        }
        // The checker is of the opponent's colour
        else if (isNotAlreadyFlipped(currentChecker)) {
            currentPath = foundCheckerNotOurColour(currentPath, currentChecker);
        }
        // If checker isn't of opponent's colour or empty it is of our
        else {
            currentPath = foundCheckerOfSameColour(currentPath, currentChecker);
        }
        return currentPath;
    }

    // Used in the calculate possible paths algorithm. Used when we've observed a
    // possible path for the player to select.
    Path foundPossiblePath(Path chosenPath) {
        //We add the checker at the path's starting coordinate to the Path
        Checker startingCoordinatesChecker = this.gameBoard.getElementAt(chosenPath.coordinates);
        chosenPath.addCheckerToPath(startingCoordinatesChecker);
        this.gamePathGrid.addPathToGrid(chosenPath);
        chosenPath = new Path();

        return chosenPath;
    }

    Path foundCheckerNotOurColour(Path currentPath, Checker chosenChecker) {
        currentPath.addCheckerToPath(chosenChecker);
        return currentPath;
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
        GameView.showEndGame(winnersArrayList);
    }
}

class OthelloModel extends ReversiModel{

    OthelloModel(GameView view){
        super(view);
        startingMoves();

        //We don't have a starting state in Othello, so we simply skip this
        this.state = Constants.PLACEMENT;
        this.calculatePossiblePaths();
    }

    @Override
    void selectStartingPlayer() {
        this.currentPlayerIndex = 0;
        this.currentPlayer = this.gamePlayerManager.getPlayerAtIndex(0);
    }

    void startingMoves(){
        int[][] startingCoords = getCenterCoords();
        for(int i = 0; i < startingCoords.length; i++){
            Checker currentChecker = this.gameBoard.getElementAt(startingCoords[i]);
            if (startingCoords[i][0] == startingCoords[i][1]) {
                currentChecker.flipChecker(currentPlayer);
            }
            else {
                currentChecker.flipChecker(gamePlayerManager.getPlayerAtIndex(1));
            }
        }
    }

    int[][] getCenterCoords(){
        int[][] centerCoords = new int[4][2];
        int halfOfSize = this.boardSize/2;
        int coordIndex = 0;

        for(int row = halfOfSize - 1; row <= halfOfSize; row++){
            for(int col = halfOfSize - 1; col <= halfOfSize; col++){
                centerCoords[coordIndex] = new int[] {row,col};
                coordIndex += 1;
            }
        }
        return centerCoords;
    }
}


class RolitModel extends OthelloModel{

    RolitModel(GameView view){
        super(view);
    }

    @Override
    void startingMoves() {
            int[][] centerCoords = getCenterCoords();
            for(int i = 0;i<centerCoords.length;i++){
                Player currentPlayer = this.gamePlayerManager.getPlayerAtIndex(i);
                Checker currentChecker = this.gameBoard.getElementAt(centerCoords[i]);
                currentChecker.flipChecker(currentPlayer);
            }
        }

        //In Rolit, we only end if the board is filled
    @Override
    boolean gameOverBeforeSkip() {
        return isBoardFilled();
    }

    @Override
    Path foundEmptyChecker(Path currentPath, Checker currentChecker) {
        if(playerHasNoMoreCheckers()) {
            if(currentPath.isEmpty()){
                currentPath.resetPath();
            }else{
                currentPath.setCoords(currentChecker.coordinates);
                currentPath.resetCheckersInPath();
                currentPath = foundPossiblePath(currentPath);
            }

        }
        else{
            // !Empty && CSeen <=> C !C... Ø
            if (!currentPath.isEmpty() && currentPath.getStatusOfCurrentColourSeen()) {
                currentPath.setCoords(currentChecker.coordinates);
                currentPath = foundPossiblePath(currentPath);
            }
            // Empty V !CSeen <=> !C... Ø... C or Ø...C
            else {
                resetPath(currentPath);
            }
        }

        // We set the starting coords of the path here regardless
        currentPath.setCoords(currentChecker.coordinates);
        return currentPath;
    }

    @Override
    Path foundCheckerNotOurColour(Path currentPath, Checker chosenChecker) {
        if(playerHasNoMoreCheckers()){

            //Has coords & is empty -> This is the first !C we find, and we've found a Ø before
                if(currentPath.hasCoords() && currentPath.isEmpty()) {
                //We remove the checker of the opponent's colour, as we can only flip empties
                currentPath.resetCheckersInPath();
                currentPath = foundPossiblePath(currentPath);
                }
                else{
                    currentPath.resetPath();                }

        }
        currentPath.addCheckerToPath(chosenChecker);

        return currentPath;
    }


}
