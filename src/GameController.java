import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
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

    public void tilePress (MouseEvent event) {
        if(Animation.isAnimating() || aiIsMoving || model.state == Constants.TURN_SKIPPED || model.state == Constants.GAME_ENDED) return;

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
        tl = new Timeline(new KeyFrame(Duration.seconds((GameView.ANIMATION_DURATION_MS / 1000) + 0.5), e -> {
            if(model != null && model.state == Constants.START){
                model.step(model.AIStartingMove());
                model.step(model.AIStartingMove());
            } else{
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
        if(model.state == Constants.TURN_SKIPPED)
        {model.step(new int[] {Constants.UNDEFINED,Constants.UNDEFINED});}
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
    }

    public void resetGame(){
        this.model = new ReversiModel(this.view);
        view.setModel(model);
        view.resetBoard();
        view.updateBoard(model.gameBoard);
    }

    public void SaveGame(ActionEvent event) {
		// Constructs a single arraylist of all turns taken, in order, from the Players' turnHistory
		ArrayList<Turn> turns = new ArrayList<Turn>();
		int first = model.gamePlayerManager.getFirstPlayerIndex(); // takes into account the starting player
		int playerTurnIndex = -1;

		// Tilføjer startmoves
		if(model.turnsTaken < 4) {
			saveLoadText.setText("Can't save game under 4 turns");
			FadeTransition fader = createFader(saveLoadText);
			saveLoadText.setVisible(true);
			fader.play();
			return;
		}
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

		saveLoadText.setText("Game Successfully saved");
		FadeTransition fader = createFader(saveLoadText);
		saveLoadText.setVisible(true);
		fader.play();
	}

	public void LoadGame(ActionEvent event) {
		SaveGame save = LoadSave.LoadGame();
		if (save == null) {
			saveLoadText.setText("Error while loading the game");
			FadeTransition fader = createFader(saveLoadText);
			saveLoadText.setVisible(true);
			fader.play();
			return;
		}
		Settings.setSettings(save.getSettings()); // Makes the settings mirror the settings of the saved game
		ArrayList<Turn> turns = save.getTurns();

		view.LoadInitialization(); // Makes a new model to recreate the game from a fresh board with the correct new settings
		model.selectStartingPlayer(turns.get(0).playerIndex);
		
		// Plays the board up to the latest move from the turn list
		for (int i = 0; i < turns.size(); i++) {
			model.step(turns.get(i).coordinates);
		}
		view.initializeBoard();
		view.updateBoard(this.model.gameBoard);
		view.updateTurnText(this.model.currentPlayer);
		getGameEndScreen().setVisible(false);
		
        if (model.currentPlayer.isAI()) {
            AIPress();
        }
	}
	
	private FadeTransition createFader(Label label) {
        FadeTransition fade = new FadeTransition(Duration.seconds(4), label);
        fade.setFromValue(1);
        fade.setToValue(0);
        return fade;
    }
}