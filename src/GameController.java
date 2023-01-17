/*

Skrevet af: Mads
Studienummer: TODO: Find på mads' studienummer og identitet


 */

import java.util.ArrayList;
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

	private Timeline tl = null;

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

	public AnchorPane getGrid() {return grid;}
    public Label getTurnText() {return turnText;}
    public Label getGameEndText() {return gameEndText;}
    public Button getPassButton() {return passButton;}
    public Label getScoreText() {return scoreText;}
    public VBox getGameEndScreen() {return gameEndScreen;}
    public HBox getHorizontalLabels() {return horizontalLabels;}
    public VBox getVerticalLabels() {return verticalLabels;}
    public Label getsaveLoadText() {return saveLoadText;}

	public void tilePress(MouseEvent event) {
		if (Animation.isAnimating() || aiIsMoving || model.state == Constants.TURN_SKIPPED
				|| model.state == Constants.GAME_ENDED)
			return;

		Rectangle tile = (Rectangle) event.getTarget();
		int[] coords = Util.fromId(tile.getId());
		model.step(coords);

		// Check if AI should make a move!
		if (model.currentPlayer.isAI()) {
			AIPress();
		}
	}

	public void AIPress() {
		// Play AI move after 1 second
		tl = new Timeline(new KeyFrame(Duration.seconds((double)(Settings.aiWaitMs + (Settings.showAnimations ? GameView.ANIMATION_DURATION_MS : 0)) / 1000), e -> {
			if (model != null && model.state == Constants.START) {
				model.step(model.AIStartingMove());
				model.step(model.AIStartingMove());
			} else {
				model.step(model.currentPlayer.getAICalculatedCoords(model.getListOfNonNullPaths()));
			}
			aiIsMoving = false;
			tl = null;
			if (model.currentPlayer.isAI() && model.state != Constants.GAME_ENDED) {
				AIPress();
			}
		}));
		aiIsMoving = true;
		tl.play();
	}

	public void passButton(ActionEvent event) {
		if (model.state == Constants.TURN_SKIPPED) {
			model.step(new int[] { Constants.UNDEFINED, Constants.UNDEFINED });
			if(model.currentPlayer.isAI()){
				AIPress();
			}
		}
	}

	public void playAgain(ActionEvent event) {
		// Create a new game with the same settings as the previous game
		resetGame();
		gameEndScreen.setVisible(false);
		if (model.currentPlayer.isAI()) {
			AIPress();
		}
	}

	public void quitGame(ActionEvent event) {
		if (tl != null) {
			tl.stop();
			tl = null;
		}
		view.toMenu();
		resetGame();
		Settings.previousStartingIndex = Constants.UNDEFINED;
		Animation.stopRap();
	}

	public void resetGame() {
		this.model = Settings.createModel(this.view);
		view.setModel(model);
		view.resetBoard();
		view.updateBoard(model.gameBoard);
	}

	public void SaveGamePressed(ActionEvent event) {
		saveLoad.SaveGame(false); //The false flag makes it save in appdata
	}

	public void ExportGamePressed(ActionEvent event) {
		saveLoad.SaveGame(true); //The true flag makes it expport the game
	}

	public void LoadGamePressed(ActionEvent event) {
		saveLoad.LoadGame(FileHandler.LoadGame()); //Loads the game and passes the result of the method that loads the save file
	}

	public void ImportGamePressed(ActionEvent event) {
		Object o = FileHandler.ImportFile(view.manager.stage); //The method for opening the dialog and loading the file
		if (o instanceof SaveGame)
			saveLoad.LoadGame((SaveGame) o); //We load the game if the object is of the correct type
		else {
			System.out.println("Loaded file is not of type SaveGame");
			saveLoadText.setText("No file / Wrong file");
			FadeTransition fader = saveLoad.createFader(saveLoadText); //Show the error message and fade it out
			saveLoadText.setVisible(true);
			fader.play();
		}
	}

	

}