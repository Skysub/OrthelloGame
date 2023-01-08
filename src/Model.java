import java.util.ArrayList;
import java.util.Random;

public class Model {
    
    private View view;
    private Random random;
    
    private int boardSize = 8;
    private Tile[][] board;

    enum GameState { Start, Main }
    private GameState gameState;

    private Tile currentPlayer;             // The next tile to be places
    private Tile startedPreviousGame;       // The player that started the previous game
    private boolean passedPreviousTurn;     // Whether the previous player passed their turn
    private int noOfMoves;                  // The numbers of moves played. Used to determine the GameState and if the game is finished

    public Model(View view) {
        this.view = view;
        random = new Random();
    }

    // Public getters used by View to retrieve the state of the game
    public int getBoardSize() {return boardSize;}
    public Tile[][] getBoard() {return board;}
    public Tile getCurrentPlayer() {return currentPlayer;}

    public void newGame() {
        // Create empty board
        board = new Tile[boardSize][boardSize];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = Tile.Empty;
            }
        }

        // Determine starting player
        if (startedPreviousGame == null) {
            if (random.nextBoolean()) {
                currentPlayer = Tile.White;
            } else {
                currentPlayer = Tile.Black;
            }
        }
        else {
            currentPlayer = startedPreviousGame.flip();
        }

        startedPreviousGame = currentPlayer;
        gameState = GameState.Start;
        passedPreviousTurn = false;
        noOfMoves = 0;

        view.updateTurnText();
    }

    public void tryMove(int row, int col) {
        switch (gameState) {
            case Start:
                if (isValidStartMove(row, col)) {
                    board[row][col] = currentPlayer;
                    currentPlayer = currentPlayer.flip();
                    if (++noOfMoves >= 4) {
                        gameState = GameState.Main;
                    }
                    passedPreviousTurn = false;

                    view.updateBoard();
                    view.updateTurnText();
                }
                break;
            case Main:
                if (isValidMove(row, col)) {
                    board[row][col] = currentPlayer;
                    flipTiles(row, col, currentPlayer);
                    passedPreviousTurn = false;

                    currentPlayer = currentPlayer.flip();
                    if (++noOfMoves >= boardSize * boardSize) {
                        endGame();
                        return;
                    }

                    view.updateBoard();
                    view.updateTurnText();
                }
                break;
        }
    }

    private boolean isValidStartMove(int row, int col) {

        if (!insideBoard(row, col) || board[row][col] != Tile.Empty) {
            return false;
        }

        boolean inCenterRows = row == boardSize / 2 || row == (boardSize / 2) - 1;
        boolean inCenterColumns = col == boardSize / 2 || col == (boardSize / 2) - 1;

        return inCenterRows && inCenterColumns;
    }

    private boolean isValidMove(int row, int col) {
        if (!insideBoard(row, col) || board[row][col] != Tile.Empty) {
            return false;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }
                if (checkDirection(row, col, dx, dy).size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean insideBoard(int row, int col) {
        return 0 <= row && row < boardSize && 0 <= col && col < boardSize;
    }

    private void flipTiles(int row, int col, Tile tile) {
        ArrayList<Index> toBeFlipped = new ArrayList<Index>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                toBeFlipped.addAll(checkDirection(row, col, i, j)); 
            }
        }

        for (int i = 0; i < toBeFlipped.size(); i++) {
            Index n = toBeFlipped.get(i);
            board[n.x][n.y] = currentPlayer;
        }
    }

    private ArrayList<Index> checkDirection(int x, int y, int dx, int dy) {
        ArrayList<Index> tiles = new ArrayList<Index>();

        x += dx;
        y += dy;

        while (insideBoard(x, y) && board[x][y] != Tile.Empty) {
            if (board[x][y] == currentPlayer.flip()) {
                tiles.add(new Index(x, y));
            }
            else if (board[x][y] == currentPlayer) {
                return tiles;
            }
            x += dx;
            y += dy;
        }
        tiles.clear();
        return tiles;
    }

    //TODO Remove this by implementing Tile class instead of Enum
    class Index {
        int x;
        int y;

        public Index(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void pass() {
        // Can't pass your turn in the start of the game
        if (gameState == GameState.Start) {
            return;
        }
        else if (passedPreviousTurn) {
            endGame();
        }

        passedPreviousTurn = true;
        currentPlayer = currentPlayer.flip();
        view.updateTurnText();
    }

    //TODO Make a GameResult type which holds information on who won and by how much
    private void endGame() {
        int whiteTiles = 0;
        int blackTiles = 0;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] == Tile.White) {
                    whiteTiles++;
                }
                else if (board[row][col] == Tile.Black) {
                    blackTiles++;
                }
            }
        }

        if (whiteTiles > blackTiles) {
            view.showEndGame(Tile.White, whiteTiles, blackTiles);
        }
        else if (whiteTiles < blackTiles) {
            view.showEndGame(Tile.Black, whiteTiles, blackTiles);
        }
        else {
            view.showEndGame(Tile.Empty, whiteTiles, blackTiles);
        }
    }
}

//TODO Make a Tile class instead, so it can have a reference to it's own position, and is an object.
enum Tile {
    Empty,
    White,
    Black;

    public Tile flip() {
        switch (this) {
            case White:
                return Black;
            case Black:
                return White;
            default:
                System.out.println("Trying to flip empty tile!");
                return Empty;
        }
    }
}