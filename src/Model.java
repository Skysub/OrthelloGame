import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Model {

    private App app;
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
        PathGrid myPathGrid = new PathGrid(4);


        Checker c1 = new Checker(1,2,1);
        Checker c2 = new Checker(2,2,0);

        Path myPath1 = new Path();
        Path myPath2 = new Path();

        myPath1.addCheckerToPath(c1);
        myPath2.addCheckerToPath(c2);

        myPathGrid.addPathToGrid(myPath1);
        myPathGrid.addPathToGrid(myPath2);
        myPathGrid.resetGrid();
}

    public Model(App app) {
        this.app = app;
    }


    Model(int boardSize, int nrPlayers){
        this.boardSize = boardSize;
        this.nrPlayers = nrPlayers;

        Random randomObject = new Random();
        this.whichColourTurn = randomObject.nextInt(nrPlayers);

        this.gameBoard = new Board(boardSize);
        this.gamePathGrid = new PathGrid(boardSize);
    }

    public void setBoardSize(int boardSize) {
        //TODO Validate?
        this.boardSize = boardSize;
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


    void flipChecker (Checker chosenChecker){
        chosenChecker.state = this.whichColourTurn;
    }

    void flipChecker (int[] coords){
        Checker chosenChecker = getCheckerFromCoords(coords);
        chosenChecker.state = this.whichColourTurn;
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
    void calculatePossiblePaths(){
        gamePathGrid.resetGrid();

        Path selfMadePath = new Path();
        int[] coords = {1,2};
        selfMadePath.startingCoords = coords;

        Random myR = new Random();

        for(int i = 0; i<this.boardSize;i++){
            int[] checkerCoords = {myR.nextInt(i), myR.nextInt(i)};
            selfMadePath.addCheckerToPath(getCheckerFromCoords(coords));

        }

        gamePathGrid.addPathToGrid(selfMadePath);
    }




    ArrayList<Path> getListOfNonNullPaths(){
        return this.gamePathGrid.getNonNullPaths();
    }

    int getNrNonNullPaths(){
        return this.gamePathGrid.getNrNonNullPaths();
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

    int[] startingCoords = new int[2];
    ArrayList<Checker> checkersInPath = new ArrayList<Checker>();
    boolean seenWhite;
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

    int getSizeOfPath(){
        return sizeOfPath;
    }


    void updateSizeOfPath(){
        this.sizeOfPath = this.checkersInPath.size();
    }
}