import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;

public class Model {
   
    // MVC
    private GameView view;

    enum GameState {Start, Main }
    private GameState gameState;
    public GameType gameType;

    // Random to determine which player should start the game
    private Random random;
   
    // Game specific variables
    private int boardSize;
    private Tile[][] board;
	private ArrayList<Move> possibleMoves;  // List of possible moves to be performed by the current player
    private ArrayList<Move> moves;          // A list of all the moves performed in the game
    private boolean passedPreviousTurn;     // Whether the previous player passed their turn. Only used in Reversi & Othello

    private ArrayList<Player> players;
    private int currentPlayerIndex;
    private int startedPreviousIndex = -1;  // The index of the player that started the previous game. Used in Reversi, to alternate who starts

    // Public getters used by View to retrieve the state of the game
    public int getBoardSize() {return boardSize;}
    public Tile[][] getBoard() {return board;}
    public Player getCurrentPlayer() {return players.get(currentPlayerIndex);}
    public ArrayList<Player> getPlayers() {return players;}
    public ArrayList<Move> getPossibleMoves() {return possibleMoves;}


    public Model(GameView view) {
        this.view = view;
        random = new Random();
    }
    
    public void newGame() {

        boolean resetStartingPlayer = (this.gameType != SettingsController.settings.gameType);

        this.gameType = SettingsController.settings.gameType;
        this.boardSize = SettingsController.settings.boardSize;

        board = new Tile[boardSize][boardSize];
        // Initialize all tiles as empty
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = new Tile(i, j);
            }
        }
        
        // Determine starting player
        players = Player.getDefaultPlayers(gameType);

        if (gameType == GameType.Reversi) {
            if (resetStartingPlayer || startedPreviousIndex == -1) {
                currentPlayerIndex = random.nextInt(players.size());
            }
            else {
                currentPlayerIndex = (startedPreviousIndex + 1) % players.size();
            }
        }
        else if (gameType == GameType.Rolit) {
            currentPlayerIndex = random.nextInt(players.size());
        }
        else if (gameType == GameType.Othello) {
            currentPlayerIndex = 0;
        }

        startedPreviousIndex = currentPlayerIndex;

        setupInitialBoard();

        passedPreviousTurn = false;
        moves = new ArrayList<Move>();
        possibleMoves = calculatePossibleMoves(getCurrentPlayer());
    }

    private void setupInitialBoard() {
        if (gameType == GameType.Reversi) {
            gameState = GameState.Start;
            return;
        }
        int max = boardSize / 2;
        int min = max - 1;
        for (int row = min; row <= max; row++) {
            for (int col = max; col >= min; col--) {
                board[row][col].flipTo(getCurrentPlayer());
                expandEdge(board[row][col]);

                if (gameType == GameType.Rolit) {
                    nextTurn();
                }
                else if (gameType == GameType.Othello && col == max) {
                    nextTurn();
                }
            }
            
        }
        gameState = GameState.Main;
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
			move.performMove();
			
			if (gameState == GameState.Start) {
				if (moves.size() >= 4) {
                    gameState = GameState.Main;
				}
				if (moves.size() % 2 == 0) {
                    nextTurn();
				}
			}
			else if (gameState == GameState.Main) {
                if (gameType == GameType.Reversi) {
                    if (moves.size() >= (boardSize * boardSize)) {
                        endGame();
                        return;
                    }
                }
                else if (gameType == GameType.Othello || gameType == GameType.Rolit) {
                    if (moves.size() >= (boardSize * boardSize) - 4) {
                        endGame();
                        return;
                    }
                }
                nextTurn();
			}
            
			passedPreviousTurn = false;
			expandEdge(tile);
			possibleMoves = calculatePossibleMoves(getCurrentPlayer());
	
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
            nextTurn();
			possibleMoves = calculatePossibleMoves(getCurrentPlayer());
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
			if (move.getMoveTile().getRow() == row && move.getMoveTile().getCol() == col) {
				return move;
			}
		}
		return null;
	}

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

	private ArrayList<Move> calculatePossibleMoves(Player player) {
        var result = new ArrayList<Move>();
        // If we're at the start of the game, only check the 4 center squares.
        if (gameState == GameState.Start) {
            for (int row = (boardSize / 2) - 1; row <= boardSize / 2; row++) {
                for (int col = (boardSize / 2) - 1; col <= boardSize / 2; col++) {
                    if (isValidStartMove(board[row][col])) {
                        result.add(new Move(player, board[row][col], new ArrayList<Tile>()));
                    }
                }
            }
            return result;
        }
        // Else, loop through the board and check squares that are part of the edge
		for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
				if (board[row][col].isEdge) {
					var flip = tilesToBeFlipped(board[row][col], player);
					if (flip.size() > 0) {
                        result.add(new Move(player, board[row][col], flip));
					}
				}
			}
		}

        if (gameType == GameType.Rolit && result.size() == 0) {
            // If the game is Rolit and there are no possible moves,
            // then let the current player places anywhere that has a tile text to it, the edge
            ArrayList<Tile> edge = getEdge();
            for (Tile tile : edge) {
                result.add(new Move(player, tile, new ArrayList<Tile>()));
            }
        }

        return result;
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
				board[row][col].isEdge = board[row][col].isEmpty();
			}
		}
	}

    private ArrayList<Tile> getEdge() {
        var list = new ArrayList<Tile>();
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col].isEdge) {
                    list.add(board[row][col]);
                }
            }
        }
        return list;
    }
    
    private boolean isInsideBoard(int row, int col) {
        return 0 <= row && row < boardSize && 0 <= col && col < boardSize;
    }

	// Returns a list of all the tiles to be flipped given a move on "from"
	private ArrayList<Tile> tilesToBeFlipped(Tile tile, Player player) {
        ArrayList<Tile> toBeFlipped = new ArrayList<Tile>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) {
                    continue;
				}
                toBeFlipped.addAll(checkDirection(tile, player, dx, dy)); 
            }
        }
		return toBeFlipped;
	}
    
	// Returns a list of all the tiles that will be flipped given a move on tile in the given direction.
    private ArrayList<Tile> checkDirection(Tile tile, Player player, int dx, int dy) {
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        int x = tile.getRow() + dx;
        int y = tile.getCol() + dy;
        
        while (isInsideBoard(x, y) && !board[x][y].isEmpty()) {
            if (board[x][y].getOwner().equals(player)) {
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
        
        int[] scores = new int[players.size()];

        // Add up no. of tiles for each player
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                for (int i = 0; i < scores.length; i++) {
                    if (players.get(i) == board[row][col].getOwner()) {
                        scores[i] = scores[i] + 1;
                    }
                }
            }
        }

        // Determine the winner(s)
        
        ArrayList<Player> winners = new ArrayList<Player>();
        int maxScore = -1;

        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                winners.clear();
                winners.add(players.get(i));
                maxScore = scores[i];
            }
            else if (scores[i] == maxScore) {
                winners.add(players.get(i));
            }
        }
        view.showEndGame(winners, scores);
    }
}

class Tile {
    private int row, col;
    private Player owner;
	boolean isEdge;

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
        this.owner = null;
		this.isEdge = false;
    }
	
    public int getRow() {return row;}
    public int getCol() {return col;}
	public Player getOwner() {return owner;}
    public Color getColor() {
        if (owner == null) {
            return Color.TRANSPARENT;
        }
        return owner.getColor();
    }

    public void flipTo(Player player) {
        owner = player;
    }
    
    public boolean isEmpty() {
        return owner == null;
    }
}

class Move {
    Player player;
	Tile tile;
	ArrayList<Tile> flips;
    
	public Move(Player possibleFor,Tile moveTile, ArrayList<Tile> toBeFlipped) {
        this.player = possibleFor;
		this.tile = moveTile;
		this.flips = toBeFlipped;
	}
    
	public Tile getMoveTile() {return tile;}
    public Player getPlayer() {return player;}
	public ArrayList<Tile> getFlips() {return flips;}
    
	public void performMove() {
        tile.flipTo(player);
		for (Tile tile : flips) {
			tile.flipTo(player);
		}
	}
}

enum GameType {Reversi, Othello, Rolit}