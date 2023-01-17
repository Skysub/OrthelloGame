/*
Skrevet af: Mads Christian Wrang Nielsen 
Studienummer: s224784 
*/

import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameView extends Application {

	private static final Color TILE_COLOR = Color.BEIGE;
	private static final double STROKE_WIDTH = 2;
	private static final double PIECE_RATIO = 0.85; // How big a percentage the piece takes up on the tile
	private static final Color STROKE_COLOR = Color.BLACK;

	// MCV
	private ReversiModel model;
	private GameController controller;

	// UI Elements
	private Stage stage;
	private Scene scene;
	private AnchorPane grid;
	private Label turnText;
	private HBox horizontalLabels;
	private VBox verticalLabels;
	private Button passButton;
	private Rectangle[][] tiles;
	private Circle[][] pieces;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {

		this.stage = stage;

		try {
			// Load UI from FXML and create an instance of the corresponding controller class "Controller"
			FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("game.fxml"));
			scene = loader.load();
			controller = loader.getController();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		controller.setView(this);
		
		// Retrieve objects from Controller FXML
		grid = controller.getGrid();
		turnText = controller.getTurnText();
		horizontalLabels = controller.getHorizontalLabels();
		verticalLabels = controller.getVerticalLabels();
		passButton = controller.getPassButton();

		// Setup Model and UI
		model = new ReversiModel(this);
		controller.setModel(model);
		initializeBoard();
		updateBoard(this.model.gameBoard);
		updateTurnText(this.model.currentPlayer);
		controller.getGameEndScreen().setVisible(false);

		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	public void onEnter() {
	}

	public void initializeBoard() {
		// Clear board
		grid.getChildren().clear();
		verticalLabels.getChildren().clear();
		horizontalLabels.getChildren().clear();

		// Right now they are defined to in game.fxml, but if the layout is changed we need to make sure to calculate it correctly
		double boardWidth = grid.getPrefWidth();
		double tileSize = (boardWidth - STROKE_WIDTH * (model.getBoardSize() + 1)) / model.getBoardSize();
		double pieceSize = tileSize * PIECE_RATIO;

		tiles = new Rectangle[model.getBoardSize()][model.getBoardSize()];
		pieces = new Circle[model.getBoardSize()][model.getBoardSize()];

		for (int row = 0; row < model.getBoardSize(); row++) {
			for (int col = 0; col < model.getBoardSize(); col++) {
				Rectangle tile = new Rectangle(tileSize, tileSize, TILE_COLOR);
				tile.setStroke(STROKE_COLOR);
				tile.setStrokeWidth(STROKE_WIDTH);
				tile.setStrokeType(StrokeType.OUTSIDE);
				tile.setOnMousePressed(controller::tilePress);
				tile.setId(Util.toId(row, col));

				AnchorPane.setTopAnchor(tile, row * (tileSize + STROKE_WIDTH));
				AnchorPane.setLeftAnchor(tile, col * (tileSize + STROKE_WIDTH));

				Circle piece = new Circle(pieceSize / 2, Color.TRANSPARENT);
				piece.setStroke(Color.TRANSPARENT);
				piece.setStrokeWidth(STROKE_WIDTH / 2);

				piece.setStrokeType(StrokeType.INSIDE);
				piece.setMouseTransparent(true);
				piece.setVisible(false);

				AnchorPane.setTopAnchor(piece,
						STROKE_WIDTH + (tileSize - pieceSize) / 2 + (tileSize + STROKE_WIDTH) * row);
				AnchorPane.setLeftAnchor(piece,
						STROKE_WIDTH + (tileSize - pieceSize) / 2 + (tileSize + STROKE_WIDTH) * col);

				tiles[row][col] = tile;
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
		for (int row = 0; row < gameBoard.gridSize; row++) {
			for (int col = 0; col < gameBoard.gridSize; col++) {
				Circle c = pieces[row][col];
				Checker t = gameBoard.getElementAt(new int[] { row, col });
				c.setVisible(!t.isEmpty());
				c.setFill(t.getColor());
				c.setStroke(STROKE_COLOR);
			}
		}

		// Only show passButton when the current player has no possible moves and is a human
		passButton.setVisible(model.state == Constants.TURN_SKIPPED);
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
			controller.getGameEndText().setText("Draw");
		}

		ArrayList<Player> players = model.gamePlayerManager.players;
		String scoreText = players.get(0).getPlayerName() + ": " + players.get(0).getScore() + " - " + players.get(1).getPlayerName() + ": " + players.get(1).getScore();

		controller.getScoreText().setText(scoreText);
		controller.getGameEndScreen().setVisible(true);
	}

	public void setModel(ReversiModel newModel) {
		this.model = newModel;
	}

    public void quitGame() {
		stage.close();
    }
}