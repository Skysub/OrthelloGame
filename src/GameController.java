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

    public void setModelAndView(ReversiModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public AnchorPane getGrid() {return grid;}
    public Label getTurnText() {return turnText;}
    public Label getGameEndText() {return gameEndText;}
    public Button getPassButton() {return passButton;}
    public Label getScoreText() {return scoreText;}
    public VBox getGameEndScreen() {return gameEndScreen;}
    public HBox getHorizontalLabels() {return horizontalLabels;}
    public VBox getVerticalLabels() {return verticalLabels;}

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
            if(model.state == Constants.START){
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



}


