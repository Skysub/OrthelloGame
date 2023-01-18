/*
Skrevet af: Mads Christian Wrang Nielsen
Studienummer: s224784
*/

import javafx.animation.FadeTransition;
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

public class GameController {

	ReversiModel model;
	private GameView view;
	private SaveLoad saveLoad;

	public boolean aiIsMoving = false;
	private Timeline aiTimeline = null;

	@FXML private AnchorPane grid;
    @FXML private Label turnText;
    @FXML private HBox horizontalLabels;
    @FXML private VBox verticalLabels;
    @FXML private Button passButton;
    @FXML private VBox gameEndScreen;
    @FXML private Label gameEndText;
    @FXML private Label scoreText;
    @FXML private Label saveLoadText;

	public void setView(GameView view) {
		this.view = view;
	}

	public void setModel(ReversiModel model) {
		this.model = model;
	}
	
	public void setSaveLoad(SaveLoad saveLoad) {
		this.saveLoad = saveLoad;
		saveLoad.setController(this);
		saveLoad.setText(saveLoadText);
	}

	// Getters for UI elements, used by GameView
	public AnchorPane getGrid() {return grid;}
    public Label getTurnText() {return turnText;}
    public Label getGameEndText() {return gameEndText;}
    public Button getPassButton() {return passButton;}
    public Label getScoreText() {return scoreText;}
    public VBox getGameEndScreen() {return gameEndScreen;}
    public HBox getHorizontalLabels() {return horizontalLabels;}
    public VBox getVerticalLabels() {return verticalLabels;}
    public Label getsaveLoadText() {return saveLoadText;}

	// Called upon pressing a tile
	public void tilePress(MouseEvent event) {
		if (Animation.isAnimating() || aiIsMoving || model.FSMDState == Constants.TURN_SKIPPED || model.FSMDState == Constants.GAME_ENDED) {
			return;
		}

		// Retrieve tile-coordinates based on ID
		Rectangle tile = (Rectangle) event.getTarget();
		int[] coords = Util.fromId(tile.getId());
		// Try to make a move on the pressed tile
		model.FSMDStep(coords);

		// If the next players is an AI, let the AI make their move
		if (model.currentPlayer.isAI()) {
			AIPress();
		}
	}

	public void AIPress() {
		// Play AI move after delay specified in settings 
		// The animation duration is added, if animations are enabled, so the AI takes it's move after animations are finished
		int aiDelay = Settings.aiWaitMs + (Settings.showAnimations ? GameView.ANIMATION_DURATION_MS : 0);
		aiTimeline = new Timeline(new KeyFrame(Duration.millis(aiDelay), e -> {
			if (model != null && model.FSMDState == Constants.START) {
				model.FSMDStep(model.AIStartingMove());
				model.FSMDStep(model.AIStartingMove());
			} else {
				model.FSMDStep(model.currentPlayer.getAICalculatedCoords(model.getListOfNonNullPaths()));
			}
			// After the AI has made it's move, reset the boolean and set the Timeline to null;
			aiIsMoving = false;
			aiTimeline = null;
			// If the next player is also an AI, call the function recursively. Enables AI vs AI
			if (model.currentPlayer.isAI() && model.FSMDState != Constants.GAME_ENDED) {
				AIPress();
			}
		}));
		// Before starting the timeline, set the boolean to true, which makes sure tile-presses are ignored in the meantime
		aiIsMoving = true;
		aiTimeline.play();
	}

	public void passButton(ActionEvent event) {
		if (model.FSMDState == Constants.TURN_SKIPPED) {
			model.FSMDStep(new int[] { Constants.UNDEFINED, Constants.UNDEFINED });
			// After performing the SKIP/PASS, check if the next player is an AI, and let it make it's move
			if(model.currentPlayer.isAI()){
				AIPress();
			}
		}
	}

	public void playAgain(ActionEvent event) {
		// Create a new game with the same settings as the previous game
		resetGame();
		gameEndScreen.setVisible(false);

		// If the AI starts, make it's move
		if (model.currentPlayer.isAI()) {
			AIPress();
		}
	}

	public void quitGame(ActionEvent event) {
		// If the AI is in progress, stop the timeline
		if (aiTimeline != null) {
			aiTimeline.stop();
			aiTimeline = null;
		}
		view.toMenu();
		// Reset the index of the previous starting player, which is only used when playing multiple games in a row of Reversi
		Settings.previousStartingIndex = Constants.UNDEFINED;
		resetGame();
		Animation.stopRap();
	}

	public void resetGame() {
		this.model = Settings.createModel(this.view);
		view.setModel(model);
		view.resetBoard();
		view.updateBoard(model.gameBoard);
	}

	public void SaveGamePressed(ActionEvent event) {
		saveLoad.SaveGame(false); // The false flag makes it save in appdata
	}

	public void ExportGamePressed(ActionEvent event) {
		saveLoad.SaveGame(true); // The true flag makes it export the game
	}

	public void LoadGamePressed(ActionEvent event) {
		saveLoad.LoadGame(FileHandler.LoadGame()); // Loads the game and passes the result of the method that loads the save file
	}

	public void ImportGamePressed(ActionEvent event) {
		Object o = FileHandler.ImportFile(view.manager.stage); // The method for opening the dialog and loading the file
		if (o instanceof SaveGame)
			saveLoad.LoadGame((SaveGame) o); // We load the game if the object is of the correct type
		else {
			System.out.println("Loaded file is not of type SaveGame");
			saveLoadText.setText("No file / Wrong file");
			FadeTransition fader = saveLoad.createFader(saveLoadText); // Show the error message and fade it out
			saveLoadText.setVisible(true);
			fader.play();
		}
	}
}