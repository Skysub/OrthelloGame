import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Model {

    int state = 0; //0: start, 1: place, 2: flip, 3: skip, 4: end

    int nrCheckersToFlip;

    Path pathChosen;
    int boardSize;
    int whichColourTurn;
    int nrPlayers;
    Board gameBoard;

    PathGrid gamePathGrid; //For mapping possible paths
    int turnsTaken = 0; //We use this as a counter for the starting steps and the score

    int turnsSkipped = 0;

    boolean isGameOver = false;

    public static void main(String[] args){
        DebugModel myDeg = new DebugModel(4,2);
        myDeg.whichColourTurn = 1;

        //1. kollonne
        myDeg.setStateOfChecker(new int[] {0,0},1);
        myDeg.setStateOfChecker(new int[] {0,1},1);
        myDeg.setStateOfChecker(new int[] {0,2},2);
        myDeg.setStateOfChecker(new int[] {0,3},0);

        //2. Kolonne
        myDeg.setStateOfChecker(new int[] {1,0},0);
        myDeg.setStateOfChecker(new int[] {1,1},2);
        myDeg.setStateOfChecker(new int[] {1,2},1);
        myDeg.setStateOfChecker(new int[] {1,3},2);

        //3. kolonne
        myDeg.setStateOfChecker(new int[] {2,0},1);
        myDeg.setStateOfChecker(new int[] {2,1},1);
        myDeg.setStateOfChecker(new int[] {2,2},0);
        myDeg.setStateOfChecker(new int[] {2,3},1);

        myDeg.calculatePossiblePaths();
        myDeg.getListOfNonNullPaths();
    }


    Model(int boardSize, int nrPlayers){
        this.boardSize = boardSize;
        this.nrPlayers = nrPlayers;

        Random randomObject = new Random();
        this.whichColourTurn = randomObject.nextInt(nrPlayers);

        this.gameBoard = new Board(boardSize);
        this.gamePathGrid = new PathGrid(boardSize);
    }

    void step(int[] coords){
        switch (this.state){

            //Start
            case 0:
                recordTurnTaken(startingMove(coords));

                if(turnsTaken==nrPlayers*2){
                    this.state = 1; //Now we place a brick
                    this.calculatePossiblePaths();
                }

                //Each player gets to put 2 checkers on the board
                if (this.turnsTaken%2 == 0){
                    this.setNextTurn();
                }

                break;
            //Place checkers
            case 1:
                recordTurnTaken(placementMove(coords));
                this.calculatePossiblePaths();

                //This check will only be necessary in iteration 2, once we remove case 2
                if(getNrNonNullPaths() == 0){
                    this.state = 3; //Skip
                }else{
                    this.state = 2; //Flip
                }
                break;
            //Flip checkers - will be removed in iteration 2
            case 2:
                recordTurnTaken(flippingMove(coords));

                if(this.nrCheckersToFlip == 0){
                    this.state = 1;
                    this.setNextTurn();
                    this.calculatePossiblePaths();
                }

                if(getNrNonNullPaths() == 0){
                    this.state = 3; //Skip
                }else{
                    this.state = 1; //Flip
                }

                break;
            //Turn has been skipped
            case 3:
                //In order to get to this state, we need to skip a turn
                this.turnsSkipped += 1;

                //See if the next person can play their turn
                setNextTurn();
                calculatePossiblePaths();
                int nrPossiblePaths = getNrNonNullPaths();

                //No one has been able to play
                if(this.turnsSkipped == nrPlayers){
                    state = 4; //End the game
                    isGameOver = true;
                } else if (nrPossiblePaths == 0){
                    state = 3;
                } else{
                    state = 1; //We can now place a brick
                    this.turnsSkipped = 0; //we reset the counter
                }
                //C'est le end
            case 4:
                //Play the most dramatic ending game music ever
                break;
        }
    }


    boolean startingMove(int[] coords){

        //Can't place outside the center square, can only place where there is no checker
        if(isLegalStartingMove(coords)){
            this.flipChecker(coords);
            this.turnsTaken += 1;

            return true;
        }

        return false;
    }



    public boolean flippingMove(int coords[]) {
        Checker chosenChecker = getCheckerFromCoords(coords);

        if ( isLegalFlip(coords) ) {
            this.flipChecker(chosenChecker);

            //update the stats
            this.nrCheckersToFlip -= 1;
            this.turnsTaken += 1;

            return true;
        }

        return false;
    }


    //There are no checkers to flip, so the players just places a brick in one of the
    //Returns true if turn taken, otherwise returns false
    public boolean placementMove (int[] coords) {

        //We check if the path exists, i.e is a possible path
        if(this.gamePathGrid.pathExists(coords)){
            this.pathChosen = getPathFromCoords(coords);
            this.nrCheckersToFlip = this.pathChosen.sizeOfPath;
            this.flipChecker(coords);
            return true;
        }

        return false;
    }

    //Checks if the player may flip the chosen checker - will be deleted in iteration 2 where we automatically flip
    boolean isLegalFlip ( int[] coords) {
        Checker chosenChecker = getCheckerFromCoords(coords);
        Path pathChosen = getPathFromCoords(coords);

        //This checker is in the chosen path and has not already been flipped
        return pathChosen.isCheckerInPath(chosenChecker) && this.isNotAlreadyFlipped(chosenChecker);
    }

    //Checks if a player has the same colour as the checker he/she wishes to flip
    boolean isNotAlreadyFlipped (Checker chosenChecker){
        return (this.whichColourTurn != chosenChecker.state);
    }

    boolean isLegalStartingMove( int[] coords){
        int center_coord = this.boardSize/2;
        return gameBoard.isWithinSquare(coords,center_coord,center_coord+1) && (gameBoard.getElementAt(coords).state == 0);
    }

    void recordTurnTaken(Boolean moveValue){
        if(moveValue){this.turnsTaken +=1;}
    }


    //Sets status of the checker to be flipped to the current player's colour
    void flipChecker (Checker chosenChecker){
        setStateOfChecker(chosenChecker,this.whichColourTurn);
    }

    void flipChecker (int[] coords){
        setStateOfChecker(coords,this.whichColourTurn);
    }

    void setStateOfChecker( int[] coords, int newState){
        Checker chosenChecker = getCheckerFromCoords(coords);
        chosenChecker.state = newState;
    }

    void setStateOfChecker(Checker chosenChecker, int newState){
        chosenChecker.state = newState;
    }

    void setNextTurn () {
        this.whichColourTurn = ++this.whichColourTurn % this.nrPlayers;
    }

    Checker getCheckerFromCoords ( int[] coords){
        return this.gameBoard.getElementAt(coords);
    }

    Path getPathFromCoords(int[] coords){ return this.gamePathGrid.getElementAt(coords);}

    void resetGrid(){
        this.gamePathGrid.resetGrid();
    }

    //RandomlyAddsPaths to the PathGrid
    void calculatePossiblePaths() {
        for (int x = 0; x < this.boardSize; x++) {
            Path horizontalPath = new Path();
            Path verticalPath = new Path();

            for (int y = 0; y < this.boardSize; y++) {
                int[] horizontalCoords = getHorizontalCoords(x,y);
                int[] verticalCoords = getVerticalCoords(x,y);

                horizontalPath = iteratePathAlgorithm(horizontalCoords,horizontalPath);
                verticalPath = iteratePathAlgorithm(verticalCoords,verticalPath);
            }
        }
    }

    //Top to down
    int[] getHorizontalCoords(int[] originalCoords){
        return originalCoords;
    }

    int[] getHorizontalCoords(int x, int y){
        return new int[] {x,y};
    }

    //Left to right
    int[] getVerticalCoords(int[] originalCoords) {
        return new int[] {originalCoords[1],originalCoords[0]};
    }

    int[] getVerticalCoords(int x, int y){
        return new int[] {y,x};
    }

    Path iteratePathAlgorithm(int[] coords,Path currentPath){

        Checker currentChecker = getCheckerFromCoords(coords);

        //The checker is empty
        if (currentChecker.isEmpty()) {
            currentPath = foundEmptyChecker(currentPath,currentChecker);
        }

        //The checker is of the opponent's colour
        else if (isNotAlreadyFlipped(currentChecker)) {
            foundCheckerNotOurColour(currentPath, currentChecker);
        }

        //If checker isn't of opponent's colour or empty it is of our
        else {
            currentPath = foundCheckerOfSameColour(currentPath,currentChecker);
        }

        return currentPath;
    }



    /*
    Used in the calculate possible paths algorithm.
    Used when we've observed a possible path for the player to select.
     */
    Path foundPossiblePath(Path chosenPath){
        this.gamePathGrid.addPathToGrid(chosenPath);
        chosenPath = new Path();

        return chosenPath;
    }

    void foundCheckerNotOurColour(Path currentPath,Checker chosenChecker){
        currentPath.addCheckerToPath(chosenChecker);
    }

    Path foundEmptyChecker(Path currentPath, Checker currentChecker){
        //!Empty && CSeen <=> C !C... Ø
        if (!currentPath.isEmpty() && currentPath.getStatusOfCurrentColourSeen()) {
            setStartingCoordsOfPathToCheckers(currentPath, currentChecker);
            currentPath = foundPossiblePath(currentPath);
        }
        //Empty V !CSeen <=> !C... Ø... C or Ø...C
        else {
            resetPath(currentPath);
        }

        //We set the starting coords of the path here regardless
        setStartingCoordsOfPathToCheckers(currentPath,currentChecker);
        return currentPath;
    }

    Path foundCheckerOfSameColour(Path currentPath, Checker currentChecker){

        //HasCoords && !empty <=> Ø !C... C
        if (!currentPath.isEmpty() && currentPath.hasStartingCoords()) {
            currentPath = foundPossiblePath(currentPath);
            //Not above implies either: C !C ... C V !C ... Ø... C C
            //We reset the path and set SeenC true
        } else {
            resetPath(currentPath);
        }

        currentPath.setStatusOfCurrentColourSeen(true);
        return currentPath;

    }


    void setStartingCoordsOfPathToCheckers(Path currentPath, Checker chosenChecker){
        currentPath.setStartingCoords(chosenChecker.coordinates);
    }

    void resetPath(Path chosenPath){
        chosenPath.resetPath();
    }

    ArrayList<Path> getListOfNonNullPaths(){
        return this.gamePathGrid.getNonNullPaths();
    }

    int getNrNonNullPaths(){
        return this.gamePathGrid.getNrNonNullPaths();
    }

    int getBoardSize(){
        return this.boardSize;
    }

}

class DebugModel extends Model{

    DebugModel(int size, int nrPlayers){
        super(size,nrPlayers);
    }

    void setColumnCheckers(int x, int lower, int upper, int newState){
        for(int y = lower;y<upper;y++){
            int[] currentCoords = {x,y};
            super.setStateOfChecker(currentCoords,newState);
        }
    }
}

class Checker {

    public int[] coordinates = new int[2];//x,y
    public int state; //0: ingen brick, 1: hvid brick, 2: sort brick

    Checker(int x, int y,int state){
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        this.state = state;
    }

    boolean isEmpty(){
        return this.state == 0;
    }

}


class Grid<E>{
    int gridSize;
    E[] gridArray;

    /*
    Koden er baseret på kode skrevet af user4910279, link: https://stackoverflow.com/a/45045080/12190113
     */
    Grid(int size,E... classtype){
        this.gridSize = size;
        gridArray = Arrays.copyOf(classtype,gridSize*gridSize);
    }

    int getPositionFromCoords(int[] coords){
        return coords[0] + coords[1]*this.gridSize;
    }

    //Assuming they're 0-indexed
    int getPositionFromCoords(int x, int y){
        return x + y*this.gridSize;
    }

    E getElementAt(int[] coords){
        int position = this.getPositionFromCoords(coords);
        return this.gridArray[position];
    }

    E getElementAt(int position){
        return this.gridArray[position];
    }



    void setElementAt(int[] coords,E element){
        int position = this.getPositionFromCoords(coords);
        this.gridArray[position] = element;
    }

    void setElementAt(int position,E element){
        this.gridArray[position] = element;
    }

    E[] getState(){
        return this.gridArray;
    }

}
class Board extends Grid<Checker>{

    Board(int size){
        super(size);

        //We fill the board in with empty checkers
        this.fillInitialBoard();
    }

    //Method for filling in the boardSize x boardSize 2D array with empty checkers
    private void fillInitialBoard(){

        for(int x_0 = 0; x_0<this.gridSize; x_0++){
            for(int y_0 = 0; y_0<this.gridSize; y_0++){
                int position = getPositionFromCoords(x_0,y_0);
                this.gridArray[position] = new Checker(x_0,y_0,0);
            }


        }

    }


    public boolean isWithinBoard(int[] coords){
        return isWithinSquare(coords,0, this.gridSize);
    }

    /*Checks if the coords are within a designated square - primarily used for the starting scenario.
    Lower:inclusive, Upper: Exclusive
    f(x,y) = true <=> lower<=x<upper && lower<=y<upper
     */
    public boolean isWithinSquare(int[] coords,int lower, int upper){
        int x = coords[0];
        int y = coords[1];
        return (x>=lower && x<upper && y>=lower && y<upper);
    }

}

class PathGrid extends Grid<Path>{


    int nrNonNullPaths = 0;
    ArrayList<Path> nonNullPaths = new ArrayList<Path>();

    PathGrid(int boardSize){
        super(boardSize);
    }

    //Turns all indices in the pathGrid into null pointers pointing towards Path objects
    void resetGrid(){
        nonNullPaths = new ArrayList<Path>();
        gridArray = new Path[gridSize*gridSize];
    }

    /*
    Adds the path to the gridArray

    If two paths have the same starting coords, we will just concat the checkers in their paths
     */
    void addPathToGrid(Path newPath){
        int positionOfNewPath = this.getPositionFromCoords(newPath.startingCoords);
        Path currentPath = this.getElementAt(positionOfNewPath);

        //There already exists a path from this coordinate
        if(java.util.Objects.nonNull(currentPath)){
            currentPath.concatCheckersInPaths(newPath);
        }else{
            this.setElementAt(positionOfNewPath,newPath);
            this.nonNullPaths.add(0,newPath);
            this.nrNonNullPaths += 1;
        }

    }

    boolean pathExists(Path chosenPath){
        return java.util.Objects.nonNull(chosenPath);
    }

    boolean pathExists(int[] coords){
        Path chosenPath = getElementAt(coords);
        return java.util.Objects.nonNull(chosenPath);
    }

    ArrayList<Path> getNonNullPaths(){
        return this.nonNullPaths;
    }

    int getNrNonNullPaths(){
        return nrNonNullPaths;
    }



}
class Path{

    final int numberEmptyCoords = 1000;
    int[] startingCoords = new int[] {numberEmptyCoords,numberEmptyCoords};
    ArrayList<Checker> checkersInPath = new ArrayList<Checker>();
    boolean currentPlayerColourSeen;
    int sizeOfPath = 0; //For the AI later on

    //Appends the checkersInPath of another path to this' - used for when multiple paths for the same placement of a checker
    void concatCheckersInPaths(Path otherPath){
        this.checkersInPath.addAll(otherPath.checkersInPath); //None of the checkers will overlap and count twice, so we can append all
        this.updateSizeOfPath();
    }

    //Checks if a checker is in this path
    boolean isCheckerInPath(Checker chosenChecker){
        return this.checkersInPath.contains(chosenChecker);
    }

    void addCheckerToPath(Checker chosenChecker){
        this.checkersInPath.add(chosenChecker);
        this.updateSizeOfPath();
    }

    boolean getStatusOfCurrentColourSeen(){
        return this.currentPlayerColourSeen;
    }

    void setStatusOfCurrentColourSeen(boolean status){
        this.currentPlayerColourSeen = status;
    }

    int getSizeOfPath(){
        return sizeOfPath;
    }

    boolean isEmpty(){
        return getSizeOfPath()==0;
    }


    void updateSizeOfPath(){
        this.sizeOfPath = this.checkersInPath.size();
    }

    void resetCheckersInPath(){
        this.checkersInPath = new ArrayList<Checker>();
        updateSizeOfPath();
    }

    void resetCoords(){
        this.startingCoords = new int[] {numberEmptyCoords,numberEmptyCoords};
    }

    void resetCurrentPlayerColourSeen(){
        this.currentPlayerColourSeen = false;
    }
    void resetPath(){
        resetCheckersInPath();
        resetCoords();
        resetCurrentPlayerColourSeen();
    }

    void setStartingCoords(int[] coords){
        this.startingCoords = coords;
    }

    //Vi kunne lave et flag i stedet...?
    boolean hasStartingCoords(){
        return startingCoords[0] != 1000;
    }
}