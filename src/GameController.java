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

	private ReversiModel model;
	private GameView view;

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
		SaveGame(false); //The false flag makes it save in appdata
	}

	public void ExportGamePressed(ActionEvent event) {
		SaveGame(true); //The true flag makes it expport the game
	}

	public void LoadGamePressed(ActionEvent event) {
		LoadGame(LoadSave.LoadGame()); //Loads the game and passes the result of the method that loads the save file
	}

	public void ImportGamePressed(ActionEvent event) {
		Object o = LoadSave.ImportFile(view.manager.stage); //The method for opening the dialog and loading the file
		if (o instanceof SaveGame)
			LoadGame((SaveGame) o); //We load the game if the object is of the correct type
		else {
			System.out.println("Loaded file is not of type SaveGame");
			saveLoadText.setText("No file / Wrong file");
			FadeTransition fader = createFader(saveLoadText); //Show the error message and fade it out
			saveLoadText.setVisible(true);
			fader.play();
		}
	}

	void SaveGame(boolean export) {
		// Constructs a single arraylist of all turns taken, in order, from the Players' turnHistory
		ArrayList<Turn> turns = new ArrayList<Turn>();
		int first = model.gamePlayerManager.getFirstPlayerIndex(); // takes into account the starting player
		int playerTurnIndex = -1; //Used to keep track of what turnindex to use with the individual turnHistories
		int extra = 0;
		
		if (model.turnsTaken < 4) {
			saveLoadText.setText("Can't save game under 4 turns");
			FadeTransition fader = createFader(saveLoadText); //Fading error message
			saveLoadText.setVisible(true);
			fader.play();
			return;
		}
		if(Settings.gameMode == Constants.GAMEMODE_REVERSI) {
			//adds the first 4 startingmoves
			if (model.nrPlayers == 2) {
				turns.add(model.gamePlayerManager.players.get(first % model.nrPlayers).getTurnHistory().get(0));
				turns.add(model.gamePlayerManager.players.get(first % model.nrPlayers).getTurnHistory().get(1));
				turns.add(model.gamePlayerManager.players.get((first + 1) % model.nrPlayers).getTurnHistory().get(0));
				turns.add(model.gamePlayerManager.players.get((first + 1) % model.nrPlayers).getTurnHistory().get(1));
				playerTurnIndex++;
			} else {
				turns.add(model.gamePlayerManager.players.get(first % model.nrPlayers).getTurnHistory().get(0));
				turns.add(model.gamePlayerManager.players.get((first + 1) % model.nrPlayers).getTurnHistory().get(0));
				turns.add(model.gamePlayerManager.players.get((first + 2) % model.nrPlayers).getTurnHistory().get(0));
				turns.add(model.gamePlayerManager.players.get((first + 3) % model.nrPlayers).getTurnHistory().get(0));
			}
			extra = 4;
			playerTurnIndex++;
		}
		
		//Loops through once for each turn. Used i and the playerTurnIndex to get the correct turn of the correct player
		for (int i = first + extra; i < model.turnsTaken + first; i++) {
			if ((i - first) % model.nrPlayers == 0) {
				// increments playerTurnIndex when all players' turn of the current index has been recorded
				playerTurnIndex++;
			}
			//adds the correct turn to the single list
			turns.add(model.gamePlayerManager.players.get(i % model.nrPlayers).getTurnHistory().get(playerTurnIndex));
		}
		SaveGame save = new SaveGame(turns, new saveSettings()); //Makes a SaveGame object from the collected data

		
		if (!export)
			LoadSave.SaveGame(save); // Calls the method that saves the file
		else {
			if (!LoadSave.ExportFile(save, view.manager.stage)) { // Calls the method that actually saves the file, returns true if all goes well
				saveLoadText.setText("Game not saved");
				FadeTransition fader = createFader(saveLoadText); //Fading error message
				saveLoadText.setVisible(true);
				fader.play();
				return; 
			}
		}
		saveLoadText.setText("Game Successfully saved");
		FadeTransition fader = createFader(saveLoadText); //Message conveying the success of the saving
		saveLoadText.setVisible(true);
		fader.play();
	}

	//Loads the game from a SaveGame object
	void LoadGame(SaveGame save) {
		if (save == null) { //error message and returns if theres no save
			saveLoadText.setText("Error while loading the game");
			FadeTransition fader = createFader(saveLoadText);
			saveLoadText.setVisible(true);
			fader.play();
			return;
		}
		Settings.setSettings(save.getSettings()); // Makes Settings mirror the settings of the saved game
		ArrayList<Turn> turns = save.getTurns();

		view.LoadInitialization(); // Makes a new model to recreate the game from a fresh board with the correct new settings
		model.selectStartingPlayer(turns.get(0).playerIndex); //Tells the model of the correct startingplayer

		if(Settings.gameMode == Constants.GAMEMODE_OTHELLO) {
			var m = (OthelloModel) model;
			m.startingMoves();
		}
		
		for (int i = 0; i < turns.size(); i++) {
			try {
				model.step(turns.get(i).coordinates); // Plays the board up to the latest move from the turn list
			} catch (Exception e) {
				System.out.println("Exception: " + e);
				System.out.println("Error while replaying the game");
				e.printStackTrace();

				saveLoadText.setText("Error while loading, save file might be corrupted");
				FadeTransition fader = createFader(saveLoadText); //fading error message
				saveLoadText.setVisible(true);
				fader.play();

				// Makes fresh board
				view.LoadInitialization();
				model.selectStartingPlayer(turns.get(0).playerIndex);
				break;
			}
		}
		//Makes a visual bug dissappear
		view.initializeBoard();
		view.updateBoard(this.model.gameBoard);
		view.updateTurnText(this.model.currentPlayer);
		getGameEndScreen().setVisible(false);

		//If it's the AI's turn this makes it move
		if (model.currentPlayer.isAI()) {
			AIPress();
		}
	}

	//Makes the animation of the error text
	private FadeTransition createFader(Label label) {
		FadeTransition fade = new FadeTransition(Duration.seconds(5), label);
		fade.setFromValue(1);
		fade.setToValue(0);
		return fade;
	}

}