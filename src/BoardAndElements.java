/*
Skrevet af: Benjamin Mirad Gurini
Studienummer: s214590
*/

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/*
Abstract class used to implement Path and Checker, as both of these classes are only instantiated in grids.
 */
abstract class GridElement {

    int emptyValue = Constants.EMPTY;

    //All coords start as undefined/Empty
    int[] coordinates = new int[] {emptyValue,emptyValue};

    abstract boolean isEmpty();
}
class Checker extends GridElement {

    Player owner;

    Checker(int x, int y) {
        this.coordinates[0] = x;
        this.coordinates[1] = y;
    }

    boolean isEmpty() {
        return Objects.isNull(this.owner);
    }

    Player getOwner() {
        return owner;
    }

    void flipChecker(Player newPlayer){
        if(!isEmpty()){
            Player oldPlayer = this.owner;
            oldPlayer.decreaseNumberOfCheckers();
        }
        newPlayer.increaseNumberOfCheckers();
        this.owner = newPlayer;
    }

    Color getColor(){
        if(isEmpty()){
            return Color.TRANSPARENT;
        }
        return this.owner.getPlayerColor();
    }
}

// Generic 2D grid used by both Board and PathGrid
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

    /*
    We use a 1D array to represent a 2D array, so have to calculate the 2D coords in the 1D array.
     */
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

/*
Class representing the gameboard with checkers.
 */
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


    /*
     * Checks if the coords are within a designated square - primarily used for the starting state of Reversi.
     * Lower:inclusive, Upper: Exclusive
     * f(x,y) = true <=>
     * lower<=x<upper && lower<=y<upper
     */
    public boolean isWithinSquare(int[] coords, int lower, int upper) {
        int x = coords[0];
        int y = coords[1];
        return (x >= lower && x < upper && y >= lower && y < upper);
    }

}

/*
Main object for organizing Paths.

Used in the calculatePossiblePaths algorithm and has methods primarily concerned with making sure no two paths have the same coordinates.
And methods for getting paths with a certain predicate, f.ex all paths that are not empty.
 */
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
     * If two paths have the same starting coords, we will just concatenate the checkers in their paths
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

/*
Paths represent possible paths of checkers that the current player can flip.

The Path class is used in the calculatePossiblePath algorithm, and contains three class variables used in the algorithm:
checkersInPath, which is used to keep track of which checkers this path will flip.
Coordinates, that tells us where on the board, this path starts, i.e which tile the player can press in order to flip the checkers in this path.
currentPlayerColourSeen, a flag which is used in the calculatePossiblePaths algorithm.
 */
class Path extends GridElement {
    ArrayList<Checker> checkersInPath = new ArrayList<Checker>();
    boolean currentPlayerColourSeen;

    // Appends the checkersInPath of another path to this' - used for when multiple
    // paths have the same coordinates
    void concatCheckersInPaths(Path otherPath) {
        this.checkersInPath.addAll(otherPath.checkersInPath); // None of the checkers will overlap and count twice, so we know each entry is unique
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