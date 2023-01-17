/*
Skrevet af: Benjamin Mirad Gurini
Studienummer: S214590
*/

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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

    Color getColor(){
        if(isEmpty()){
            return Color.TRANSPARENT;
        }
        return this.state.getPlayerColor();
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

    // Appends the checkersInPath of another path to this' - used for when multiple
    // paths for the same placement of a checker
    void concatCheckersInPaths(Path otherPath) {
        this.checkersInPath.addAll(otherPath.checkersInPath); // None of the checkers will overlap and count twice, so
        // we can append all
    }

    // Checks if a checker is in this path
    boolean isCheckerInPath(Checker chosenChecker) {
        return this.checkersInPath.contains(chosenChecker);
    }

    void addCheckerToPath(Checker chosenChecker) {
        this.checkersInPath.add(chosenChecker);
    }

    boolean getStatusOfCurrentColourSeen() {
        return this.currentPlayerColourSeen;
    }

    void setStatusOfCurrentColourSeen(boolean status) {
        this.currentPlayerColourSeen = status;
    }

    int getSizeOfPath() {
        return checkersInPath.size();
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

    void resetCheckersInPath() {
        this.checkersInPath = new ArrayList<Checker>();
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