/*
Skrevet af: Mads Christian Wrang Nielsen 
Studienummer: s224784 
*/

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class GameController {

	private ReversiModel model;
	private GameView view;

	public boolean aiIsMoving = false;

	@FXML private AnchorPane grid;
    @FXML private Label turnText;
    @FXML private HBox horizontalLabels;
    @FXML private VBox verticalLabels;
    @FXML private Button passButton;
    @FXML private VBox gameEndScreen;
    @FXML private Label gameEndText;
    @FXML private Label scoreText;

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

	public void tilePress(MouseEvent event) {
		if (model.state == Constants.TURN_SKIPPED || model.state == Constants.GAME_ENDED) {
			return;
		}
		Rectangle tile = (Rectangle) event.getTarget();
		int[] coords = Util.fromId(tile.getId());
		model.step(coords);
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
	}

	public void quitGame(ActionEvent event) {
		view.quitGame();
	}

	public void resetGame() {
		model = new ReversiModel(view);
		view.setModel(model);
		view.resetBoard();
		view.updateBoard(model.gameBoard);
	}
}