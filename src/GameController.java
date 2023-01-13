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

    private Model model;
    private GameView view;

    @FXML private AnchorPane grid;
    @FXML private Label turnText;
    @FXML private HBox horizontalLabels;
    @FXML private VBox verticalLabels;
    @FXML private Button passButton;
    @FXML private VBox gameEndScreen;
    @FXML private Label gameEndText;
    @FXML private Label scoreText;

    public void setModelAndView(Model model, GameView view) {
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
        if(Animation.isAnimating()) return;

        Rectangle tile = (Rectangle) event.getTarget();
        int[] coords = Util.fromId(tile.getId());
        model.tryMove(coords[0], coords[1]);
    }

    public void passButton(ActionEvent event) {
        model.pass();
    }

    public void playAgain(ActionEvent event) {
        // Create a new game with the same settings as the previous game
        model.newGame();
        view.resetBoard();
        view.updateBoard();
        view.updateTurnText(); 
        gameEndScreen.setVisible(false);
    }
}
