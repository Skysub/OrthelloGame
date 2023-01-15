import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class GameController {

	private ReversiModel model;
	private GameView view;

	private boolean aiIsMoving = false;

	@FXML
	private AnchorPane grid;
	@FXML
	private Label turnText;
	@FXML
	private HBox horizontalLabels;
	@FXML
	private VBox verticalLabels;
	@FXML
	private Button passButton;
	@FXML
	private VBox gameEndScreen;
	@FXML
	private Label gameEndText;
	@FXML
	private Label scoreText;
	@FXML
	private Button saveButton;
	@FXML
	private Button loadButton;

	public void setModelAndView(ReversiModel model, GameView view) {
		this.model = model;
		this.view = view;
	}

	public AnchorPane getGrid() {
		return grid;
	}

	public Label getTurnText() {
		return turnText;
	}

	public Label getGameEndText() {
		return gameEndText;
	}

	public Button getPassButton() {
		return passButton;
	}

	public Label getScoreText() {
		return scoreText;
	}

	public VBox getGameEndScreen() {
		return gameEndScreen;
	}

	public HBox getHorizontalLabels() {
		return horizontalLabels;
	}

	public VBox getVerticalLabels() {
		return verticalLabels;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public Button getLoadButton() {
		return loadButton;
	}

	public void tilePress(MouseEvent event) {
		if (Animation.isAnimating() || aiIsMoving)
			return;

		Rectangle tile = (Rectangle) event.getTarget();
		int[] coords = Util.fromId(tile.getId());
		model.step(coords);

		if (model.currentPlayer.isAI()) {
			// Play AI move after 1 second
			var timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
				// TODO Should this be done elsewhere?
				ArrayList<Path> nonNullArray = model.getListOfNonNullPaths();

				if (model.state == Constants.TURN_SKIPPED) {
					model.skipTurn();
				} else if (model.state == Constants.START) {
					model.step(new int[] { 3, 3 });
					model.step(new int[] { 3, 4 });
					model.step(new int[] { 4, 4 });
					model.step(new int[] { 4, 3 });
				} else {
					model.step(model.currentPlayer.getAICalculatedCoords(nonNullArray));
				}
				aiIsMoving = false;
			}));

			aiIsMoving = true;
			timeline.play();
		}
	}

	public void passButton(ActionEvent event) {
		if (model.state == Constants.TURN_SKIPPED) {
			model.skipTurn();
		}
	}

	public void playAgain(ActionEvent event) {
		// Create a new game with the same settings as the previous game
		model.resetGame();
		gameEndScreen.setVisible(false);
	}

	public void quitGame(ActionEvent event) {
		view.toMenu();
	}

	public void SaveGame(ActionEvent event) {
		// Constructs a single arraylist of all turns taken, in order, from the Players' turnHistory
		ArrayList<Turn> turns = new ArrayList<Turn>();
		int first = model.gamePlayerManager.getFirstPlayerIndex(); // takes into account the starting player
		int playerTurnIndex = -1;

		// Tilføjer startmoves
		if(model.turnsTaken < 4) return; //TODO fejlbesked
		if (model.nrPlayers == 2) {
			turns.add(model.gamePlayerManager.players.get(first % model.nrPlayers).getTurnHistory().get(0));
			turns.add(model.gamePlayerManager.players.get(first % model.nrPlayers).getTurnHistory().get(1));
			turns.add(model.gamePlayerManager.players.get((first + 1) % model.nrPlayers).getTurnHistory().get(0));
			turns.add(model.gamePlayerManager.players.get((first + 1) % model.nrPlayers).getTurnHistory().get(1));
			playerTurnIndex += 2;
		} else {
			turns.add(model.gamePlayerManager.players.get(first % model.nrPlayers).getTurnHistory().get(0));
			turns.add(model.gamePlayerManager.players.get((first + 1) % model.nrPlayers).getTurnHistory().get(0));
			turns.add(model.gamePlayerManager.players.get((first + 2) % model.nrPlayers).getTurnHistory().get(0));
			turns.add(model.gamePlayerManager.players.get((first + 3) % model.nrPlayers).getTurnHistory().get(0));
			playerTurnIndex ++;
		}

		for (int i = first + 4; i < model.turnsTaken + first; i++) {
			if ((i - first) % model.nrPlayers == 0) {
				// increments playerTurnIndex when all players' turn of the current index has been recorded
				playerTurnIndex++;
			}
			turns.add(model.gamePlayerManager.players.get(i % model.nrPlayers).getTurnHistory().get(playerTurnIndex));
		}
		SaveGame save = new SaveGame(turns, new Settings());

		// Calls the method that actually saves the file, returns true if all goes well
		LoadSave.SaveGame(save);

		// TODO Make text pop up that shows if the saving was successful or not
	}

	public void LoadGame(ActionEvent event) {
		SaveGame save = LoadSave.LoadGame();
		if (save == null)
			return;// TODO: make error text pop up
		Settings.setSettings(save.getSettings()); // Makes the settings mirror the settings of the saved game
		ArrayList<Turn> turns = save.getTurns();

		view.MakeNewModel(); // Makes a new model to recreate the game from a fresh board with the correct new settings
		model.selectStartingPlayer(turns.get(0).playerIndex);
		
		// Plays the board up to the latest move from the turn list
		for (int i = 0; i < turns.size(); i++) {
			model.step(turns.get(i).coordinates);
		}
		view.onEnter();
		// TODO make success text pop up
	}
}
