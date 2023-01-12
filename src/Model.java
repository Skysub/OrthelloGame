import java.util.ArrayList;
import java.util.Random;

public class Model {
    
    private View view;
    private Random random;
    
    private int boardSize = 8;
    private Tile[][] board;
	private ArrayList<Move> possibleMoves;  // List of possible moves to be performed by the current player
    private ArrayList<Move> moves;          // A list of all the moves performed in the game

    enum GameState { Start, Main }
    private GameState gameState;

    private TileType currentPlayer;         // The next tile to be places
    private TileType startedPreviousGame;   // The player that started the previous game
    private boolean passedPreviousTurn;     // Whether the previous player passed their turn

    public Model(View view) {
        this.view = view;
        random = new Random();
    }
    
    // Public getters used by View to retrieve the state of the game
    public int getBoardSize() {return boardSize;}
    public Tile[][] getBoard() {return board;}
	public ArrayList<Move> getPossibleMoves() {return possibleMoves;}
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
        
        moves = new ArrayList<Move>();
        possibleMoves = new ArrayList<Move>();
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

            moves.add(move);

			tile.setType(currentPlayer);
			move.flipTiles();
			
			if (gameState == GameState.Start) {
				if (moves.size() >= 4) {
					gameState = GameState.Main;
				}
				if (moves.size() % 2 == 0) {
					currentPlayer = currentPlayer.flip();
				}
			}
			else if (gameState == GameState.Main) {
				currentPlayer = currentPlayer.flip();
				if (moves.size() >= boardSize * boardSize) {
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
        // If the previous player didn't pass, then we switch player and calculate the new moves
		else if (!passedPreviousTurn) {
			passedPreviousTurn = true;
			currentPlayer = currentPlayer.flip();
			calculatePossibleMoves();
			view.updateBoard();
			view.updateTurnText();
		}
        // If the previous player passed, then the game should end
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

	public Move getPossibleMove(int row, int col) {
		for (Move move : possibleMoves) {
			if (move.getTile().getRow() == row && move.getTile().getCol() == col) {
				return move;
			}
		}
		return null;
	}

	private void calculatePossibleMoves() {
		possibleMoves.clear();
        // If we're at the start of the game, only check the 4 center squares.
        if (gameState == GameState.Start) {
            for (int row = (boardSize / 2) - 1; row <= boardSize / 2; row++) {
                for (int col = (boardSize / 2) - 1; col <= boardSize / 2; col++) {
                    if (isValidStartMove(board[row][col])) {
                        possibleMoves.add(new Move(currentPlayer, board[row][col], new ArrayList<Tile>()));
                    }
                }
            }
            return;
        }
        // Else, loop through the board and check squares that are part of the edge
		for (int row = 0; row < boardSize; row++) {
			for (int col = 0; col < boardSize; col++) {
				if (board[row][col].isEdge()) {
					var flip = tilesToBeFlipped(board[row][col]);
					if (flip.size() > 0) {
						possibleMoves.add(new Move(currentPlayer, board[row][col], flip));
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
		// Set isEdge flag to true for all empty neighbour tiles, and false for all non-empty tiles (including itself)
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
            if (board[x][y].isTile(currentPlayer)) {
                return tiles;
            }
            else {
                // IF the tiles isn't empty or the current player it's an opponents tile
                // which will be flipped if the current players tiles is at the end of the chain.
                tiles.add(board[x][y]);
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
        //Clears the possible moves and updates the board one last time to show the last move
		possibleMoves.clear();
		view.updateBoard();

        //TODO Make more general way to determine this.
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

class Move {
	TileType player;
	Tile tile;
	ArrayList<Tile> flips;

	public Move(TileType possibleFor,Tile moveTile, ArrayList<Tile> toBeFlipped) {
		this.player = possibleFor;
		this.tile = moveTile;
		this.flips = toBeFlipped;
	}

	public Tile getTile() {return tile;}
	public ArrayList<Tile> getFlips() {return flips;}

	public void flipTiles() {
		for (Tile tile : flips) {
			tile.flip();
		}
	}
}