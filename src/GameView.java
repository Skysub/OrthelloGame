/*
Skrevet af: Mads Christian Wrang Nielsen
Studienummer: s224784
*/

import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GameView {

	// Colors
	private static final Color TILE_COLOR = Color.BEIGE;
	private static final Color POSSIBLE_MOVE_COLOR = new Color(0.25, 0.25, 1, 0.25);
	private static final Color POSSIBLE_MOVE_HIGHLIGHET_COLOR = new Color(0.25, 0.25, 1, 0.5);
	private static final Color STROKE_COLOR = Color.BLACK;

	// UI Constants
	private static final double STROKE_WIDTH = 2;	// The size of the stroke of the tiles
	private static final double PIECE_RATIO = 0.85; // How big a percentage the piece takes up on the tile

	// Animation
	public static final int ANIMATION_DURATION_MS = 250;

	// MCV
	ViewManager manager;
	private ReversiModel model;
	private GameController controller;

	// UI Elements
	public Scene scene;
	private AnchorPane grid;
	private Label turnText;
	private HBox horizontalLabels;
	private VBox verticalLabels;
	private Button passButton;
	private Circle[][] pieces;

	// A reference to the last highlighted move, used to reset the colors once the move is no longer highlighted
	private Path lastHighlightedMove;

	public GameView(ViewManager manager) {
		this.manager = manager;

		try {
			// Load UI from FXML and create an instance of the corresponding controller class "GameController"
			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("game.fxml"));
			scene = loader.load();
			controller = loader.getController();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		controller.setView(this);
		controller.setSaveLoad(new SaveLoad(this));

		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.R) {
				Animation.playRap();
			}
		});

		// Retrieve objects from Controller FXML
		grid = controller.getGrid();
		turnText = controller.getTurnText();
		horizontalLabels = controller.getHorizontalLabels();
		verticalLabels = controller.getVerticalLabels();
		passButton = controller.getPassButton();
	}
	
	public void setModel(ReversiModel newModel) {
		this.model = newModel;
	}

	public void onEnter() {
		// Setup Model and UI
		model = Settings.createModel(this);
		controller.setModel(model);
		initializeBoard();
		updateBoard(this.model.gameBoard);
		updateTurnText(this.model.currentPlayer);
		controller.getGameEndScreen().setVisible(false);

		if (model.currentPlayer.isAI()) {
			controller.AIPress();
		}
	}

	public void toMenu() {
		manager.toMenu();
	}

	//Needed in order to load a previous game
	public void LoadInitialization() {
		model = Settings.createModel(this); // Construct new model based on gamemode
		controller.setModel(model); 		// Updates the controllers reference
		initializeBoard();
	}

	public void initializeBoard() {
		// Clear board elements
		grid.getChildren().clear();
		verticalLabels.getChildren().clear();
		horizontalLabels.getChildren().clear();

		// Calculate UI sizes
		double boardWidth = grid.getPrefWidth(); // Width and height are identical, defined in game.fxml
		// For a NxN board there are (N+1) strokes
		// Remove the total strokesize (N+1)*STROKE_WIDTH from the boardWidth, and divide by the number tiles N
		double tileSize = (boardWidth - STROKE_WIDTH * (model.getBoardSize() + 1)) / model.getBoardSize(); 
		double pieceSize = tileSize * PIECE_RATIO;

		// Initialize Circle[][] array storing pieces
		pieces = new Circle[model.getBoardSize()][model.getBoardSize()];

		// Loop through each tile
		for (int row = 0; row < model.getBoardSize(); row++) {
			for (int col = 0; col < model.getBoardSize(); col++) {
				// Create the tile, with the calculated size
				Rectangle tile = new Rectangle(tileSize, tileSize, TILE_COLOR);
				tile.setStroke(STROKE_COLOR);
				tile.setStrokeWidth(STROKE_WIDTH);
				tile.setStrokeType(StrokeType.OUTSIDE);

				// Set events
				tile.setOnMousePressed(controller::tilePress);
				if (Settings.showMoveHints) {
					tile.setOnMouseEntered(event -> onHover(tile));
				}
				// Set ID, used to identify which tile was pressed, in GameController.tilePress()
				tile.setId(Util.toId(row, col));

				// Set the anchors of the tile
				AnchorPane.setTopAnchor(tile, row * (tileSize + STROKE_WIDTH));
				AnchorPane.setLeftAnchor(tile, col * (tileSize + STROKE_WIDTH));

				// Create the piece. Set to transparent by default
				Circle piece = new Circle(pieceSize / 2, Color.TRANSPARENT);
				piece.setStroke(Color.TRANSPARENT);
				piece.setStrokeWidth(STROKE_WIDTH / 2);
				piece.setStrokeType(StrokeType.INSIDE);
				piece.setVisible(false);
				// Set piece to be mouse-transparent, so they don't block mouse-clicks from tiles
				piece.setMouseTransparent(true);

				// Set anchor of pieces. Adds half the difference between the tileSize and pieceSize, to ensure tiles and pieces line up
				AnchorPane.setTopAnchor(piece, STROKE_WIDTH + (tileSize - pieceSize) / 2 + (tileSize + STROKE_WIDTH) * row);
				AnchorPane.setLeftAnchor(piece, STROKE_WIDTH + (tileSize - pieceSize) / 2 + (tileSize + STROKE_WIDTH) * col);

				// Save the piece in pieces array, and add tile and piece to AnchorPane "Grid"
				pieces[row][col] = piece;
				grid.getChildren().addAll(tile, piece);
			}
		}

		// Create axislabels
		for (int i = 0; i < model.getBoardSize(); i++) {
			var vLabel = createAxisLabel(model.getBoardSize() - i + "");
			vLabel.setPrefSize(verticalLabels.getPrefWidth(), tileSize + STROKE_WIDTH);
			verticalLabels.getChildren().add(vLabel);

			var hLabel = createAxisLabel((char) (i + 97) + "");
			hLabel.setPrefSize(tileSize + STROKE_WIDTH, horizontalLabels.getPrefHeight());
			horizontalLabels.getChildren().add(hLabel);
		}
	}

	private Label createAxisLabel(String text) {
		Label label = new Label(text);
		label.setFont(new Font(12));
		label.setTextAlignment(TextAlignment.CENTER);
		label.setAlignment(Pos.CENTER);
		return label;
	}

	// Sets all pieces to transparent
	public void resetBoard() {
		turnText.setVisible(true);
		for (int row = 0; row < model.getBoardSize(); row++) {
			for (int col = 0; col < model.getBoardSize(); col++) {
				pieces[row][col].setFill(Color.TRANSPARENT);
				pieces[row][col].setStroke(Color.TRANSPARENT);
			}
		}
	}

	public void updateBoard(Board gameBoard) {
		// Loop through each tile
		for (int row = 0; row < gameBoard.gridSize; row++) {
			for (int col = 0; col < gameBoard.gridSize; col++) {
				// Retrieve the corrensponding piece and checker
				Circle piece = pieces[row][col];
				Checker checker = gameBoard.getElementAt(new int[] { row, col });

				// Set the visibility based on whether is tile is empty or not
				piece.setVisible(!checker.isEmpty());

				// Handle animations
				// We only want to play animations if animations are enabled in settings, and the color of the piece is different from what it should be (the checkers color)
				if (Settings.showAnimations && (piece.getFill() != checker.getColor() && piece.getFill() != POSSIBLE_MOVE_COLOR)) {
					if (piece.getFill() == Color.TRANSPARENT || piece.getFill() == POSSIBLE_MOVE_HIGHLIGHET_COLOR) {
						// If the piece was transparent, then it's the piece being placed, where we play Animation.halfFlip()
						Animation.playSound();
						Animation.halfFlip(piece, ANIMATION_DURATION_MS / 2, checker.getColor());
					} else {
						// Else the piece has a different color, meaning it should flip from it's current color to the checkers color, playing the full animation
						Animation.flipPiece(piece, ANIMATION_DURATION_MS, (Color) piece.getFill(), checker.getColor());
					}
				} else {
					// Else just set the color and stroke
					// Reached when animations are disabled or when loading games
					piece.setFill(checker.getColor());
					piece.setStroke(STROKE_COLOR);
				}
			}
		}

		// Only show passButton when the current player has no possible moves and is a human
		passButton.setVisible(model.FSMDState == Constants.TURN_SKIPPED && !model.currentPlayer.isAI());
		
		// Only show move hints when the current player is human, and move hints are enabled
		if (!Settings.showMoveHints || model.currentPlayer.isAI()) {
			return;
		}

		// Loop through possible moves and set their color, stroke and visibility
		ArrayList<Path> possibleMoves = model.gamePathGrid.getNonNullPaths();
		for (Path move : possibleMoves) {
			int[] coords = move.coordinates;
			Circle c = pieces[coords[0]][coords[1]];
			c.setFill(POSSIBLE_MOVE_COLOR);
			c.setStroke(Color.TRANSPARENT);
			c.setVisible(true);
		}
	}

	public void updateTurnText(Player currentPlayer) {
		turnText.setText("Current Player: " + currentPlayer.getPlayerName());
	}

	public void showEndGame(ArrayList<Player> winners) {
		turnText.setVisible(false);
		passButton.setVisible(false);

		if (winners.size() == 1) {
			controller.getGameEndText().setText("Winner: " + winners.get(0).getPlayerName());
		} else {
			// If the number of winners are bigger than 1, then it's a draw (or partial draw in Rolit)
			controller.getGameEndText().setText("Draw");
		}

		// Show all players score in scoreText
		ArrayList<Player> players = model.gamePlayerManager.players;
		String scoreText = players.get(0).getPlayerName() + ": " + players.get(0).getScore();

		for (int i = 1; i < players.size(); i++) {
			// Added " - " delimeter between 2 players, and newline "\n" for Rolit (4-player)
			scoreText += (i != 2) ? " - " : "\n"; 
			scoreText += players.get(i).getPlayerName() + ": " + players.get(i).getScore();
		}

		controller.getScoreText().setText(scoreText);
		controller.getGameEndScreen().setVisible(true);
	}

	// Called when hovering over a tile
	private void onHover(Rectangle rect) {
		// If a animation is ongoing, or the AI is moving, we return
		if (Animation.isAnimating() || controller.aiIsMoving) {
			return;
		}
		// Retrieve the coords of the rectangle that is hovered on
		int[] coords = Util.fromId(rect.getId());

		// If there is a listHighligtedMove, we color the tiles back to their original color
		if (lastHighlightedMove != null) {
			for (Checker checkerToFlip : lastHighlightedMove.checkersInPath) {
				pieces[checkerToFlip.coordinates[0]][checkerToFlip.coordinates[1]].setFill(checkerToFlip.getColor());
			}
			var moveStartingChecker = model.gameBoard.getElementAt(lastHighlightedMove.coordinates);

			if (moveStartingChecker.isEmpty()) {
				pieces[moveStartingChecker.coordinates[0]][moveStartingChecker.coordinates[1]]
						.setFill(POSSIBLE_MOVE_COLOR);
			}
			lastHighlightedMove = null;
		}

		// Try to get a possible move on the hovered tile
		Path move = model.gamePathGrid.getElementAt(coords);

		// If the move is valid, show which tiles will be flipped by the move, by changing their color
		if (move != null) {
			lastHighlightedMove = move;
			for (Checker checkerFromPath : move.checkersInPath) {
				Color c = checkerFromPath.getColor();
				c = Color.rgb((int) c.getRed(), (int) c.getGreen(), (int) c.getBlue(), 0.5);
				pieces[checkerFromPath.coordinates[0]][checkerFromPath.coordinates[1]].setFill(c);
			}
			pieces[move.coordinates[0]][move.coordinates[1]].setFill(POSSIBLE_MOVE_HIGHLIGHET_COLOR);
		}
	}
}