import java.util.ArrayList;
import java.util.Random;

public class Model {
    
    private View view;
    private Random random;
    
    private int boardSize = 8;
    private Tile[][] board;
	private ArrayList<PossibleMove> possibleMoves;

    enum GameState { Start, Main }
    private GameState gameState;

    private TileType currentPlayer;         // The next tile to be places
    private TileType startedPreviousGame;   // The player that started the previous game
    private boolean passedPreviousTurn;     // Whether the previous player passed their turn
    private int noOfMoves;                  // The numbers of moves played. Used to determine the GameState and if the game is finished

    public Model(View view) {
        this.view = view;
        random = new Random();
		possibleMoves = new ArrayList<PossibleMove>();
    }

    // Public getters used by View to retrieve the state of the game
    public int getBoardSize() {return boardSize;}
    public Tile[][] getBoard() {return board;}
	public ArrayList<PossibleMove> getPossibleMoves() {return possibleMoves;}
    public TileType getCurrentPlayer() {return currentPlayer;}

    public void newGame() {
        // Create empty board
        board = new Tile[boardSize][boardSize];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = new Tile(i, j, TileType.Empty);
            }
        }

        // Determine starting player
        if (startedPreviousGame == null) {
            if (random.nextBoolean()) {
                currentPlayer = TileType.White;
            } else {
                currentPlayer = TileType.Black;
            }
        }
        else {
            currentPlayer = startedPreviousGame.flip();
        }

        startedPreviousGame = currentPlayer;
        gameState = GameState.Start;
        passedPreviousTurn = false;
        noOfMoves = 0;

		calculatePossibleMoves();
    }

    public void tryMove(int row, int col) {

        if (!isInsideBoard(row, col)) {
            System.out.println("ERROR: Not inside board!");
			return;
        }

        Tile tile = board[row][col];

		// Check if the pressed tile is in the list of possible moves
		var move = getPossibleMove(row, col);

		if (move != null) {

			tile.setType(currentPlayer);
			move.flipTiles();
			
			if (gameState == GameState.Start) {
				if (++noOfMoves >= 4) {
					gameState = GameState.Main;
				}
				if (noOfMoves % 2 == 0) {
					currentPlayer = currentPlayer.flip();
				}
			}
			else if (gameState == GameState.Main) {
				currentPlayer = currentPlayer.flip();
				if (++noOfMoves >= boardSize * boardSize) {
					endGame();
					return;
				}
			}

			passedPreviousTurn = false;
			expandEdge(tile);
			calculatePossibleMoves();
	
			view.updateBoard();
			view.updateTurnText();
		}
    }

	// Called by controller when a player presses the "Pass Turn" button
	public void pass() {
		// Can only pass turn if the player has no available moves
		if (possibleMoves.size() > 0) {
			return;
		}
		if (!passedPreviousTurn) {
			passedPreviousTurn = true;
			currentPlayer = currentPlayer.flip();
			calculatePossibleMoves();
			view.updateBoard();
			view.updateTurnText();
		}
		else {
			endGame();
		}
	}

	public Tile getTile(int row, int col) {
		if (!isInsideBoard(row, col)) {
			return null;
		}
		return board[row][col];
	}

	public PossibleMove getPossibleMove(int row, int col) {
		for (PossibleMove move : possibleMoves) {
			if (move.getTile().getRow() == row && move.getTile().getCol() == col) {
				return move;
			}
		}
		return null;
	}

	private void calculatePossibleMoves() {
		possibleMoves.clear();
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				Tile t = board[row][col];

				if (gameState == GameState.Start && isValidStartMove(t)) {
					possibleMoves.add(new PossibleMove(currentPlayer, t, new ArrayList<Tile>()));
				}
				else if (gameState == GameState.Main && t.isEdge()) {
					var flip = tilesToBeFlipped(t);
					if (flip.size() > 0) {
						possibleMoves.add(new PossibleMove(currentPlayer, t, flip));
					}
				}
			}
		}
	}

	// Returns true if the argument "tile" is empty and inside the 4 center squares (assuming boardSize is even).
    private boolean isValidStartMove(Tile tile) {
        if (!tile.isEmpty()) {
            return false;
        }
        boolean inCenterRows = tile.getRow() == boardSize / 2 || tile.getRow() == (boardSize / 2) - 1;
        boolean inCenterColumns = tile.getCol() == boardSize / 2 || tile.getCol() == (boardSize / 2) - 1;
        return inCenterRows && inCenterColumns;
    }

	// Expands the edge around places pieces from the argument "fromTile"
	private void expandEdge(Tile fromTile) {
		// Set isEdge flag to true for all empty neighbour tiles
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				int row = fromTile.getRow() + dx;
				int col = fromTile.getCol() + dy;

				if (!isInsideBoard(row, col)) {
					continue;
				}
				board[row][col].setEdge(board[row][col].isEmpty());
			}
		}
	}

    private boolean isInsideBoard(int row, int col) {
        return 0 <= row && row < boardSize && 0 <= col && col < boardSize;
    }

	// Returns a list of all the tiles to be flipped given a move on "from"
	private ArrayList<Tile> tilesToBeFlipped(Tile from) {
        ArrayList<Tile> toBeFlipped = new ArrayList<Tile>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) {
					continue;
				}
                toBeFlipped.addAll(checkDirection(from, dx, dy)); 
            }
        }
		return toBeFlipped;
	}

	// Returns a list of all the tiles that will be flipped given a move on tile in the given direction.
    private ArrayList<Tile> checkDirection(Tile tile, int dx, int dy) {
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        int x = tile.getRow() + dx;
        int y = tile.getCol() + dy;

        while (isInsideBoard(x, y) && !board[x][y].isEmpty()) {
            if (board[x][y].isTile(currentPlayer.flip())) {
                tiles.add(board[x][y]);
            }
            else if (board[x][y].isTile(currentPlayer)) {
                return tiles;
            }
            x += dx;
            y += dy;
        }
		// We've reached and empty tile or the edge of the board, meaning the move isn't valid.
		// Therefore there are no tiles to flip in this direction.
        tiles.clear();
        return tiles;
    }

    private void endGame() {
		possibleMoves.clear();
		view.updateBoard();
        int whiteTiles = 0;
        int blackTiles = 0;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col].isTile(TileType.White)) {
                    whiteTiles++;
                }
                else if (board[row][col].isTile(TileType.Black)) {
                    blackTiles++;
                }
            }
        }

        if (whiteTiles > blackTiles) {
            view.showEndGame(TileType.White, whiteTiles, blackTiles);
        }
        else if (whiteTiles < blackTiles) {
            view.showEndGame(TileType.Black, whiteTiles, blackTiles);
        }
        else {
            view.showEndGame(TileType.Empty, whiteTiles, blackTiles);
        }
    }
}

enum TileType {
    Empty,
    White,
    Black;

    public TileType flip() {
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

class Tile {
    private int row, col;
    private TileType type;
	private boolean isEdge;

    public Tile(int row, int col, TileType type) {
        this.row = row;
        this.col = col;
        this.type = type;

		this.isEdge = false;
    }
	
    public int getRow() {return row;}
    public int getCol() {return col;}
	public TileType getType() {return type;}
	public boolean isEdge() {return isEdge;}

    public void flip() {
        type = type.flip();
    }

    public boolean isEmpty() {
        return type == TileType.Empty;
    }

    public void setType(TileType type) {
        this.type = type;
    }

	public void setEdge(boolean isEdge) {
		this.isEdge = isEdge;
	}

    public boolean isTile(TileType type) {
        return this.type == type;
    }
}

class PossibleMove {
	TileType possibleFor;
	Tile moveTile;
	ArrayList<Tile> toBeFlipped;

	public PossibleMove(TileType possibleFor,Tile moveTile, ArrayList<Tile> toBeFlipped) {
		this.possibleFor = possibleFor;
		this.moveTile = moveTile;
		this.toBeFlipped = toBeFlipped;
	}

	public Tile getTile() {return moveTile;}
	public ArrayList<Tile> getToBeFlipped() {return toBeFlipped;}

	public void flipTiles() {
		for (Tile tile : toBeFlipped) {
			tile.flip();
		}
	}
}