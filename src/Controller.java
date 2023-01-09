import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Controller {

    private Model model;
    private View view;
    public boolean isAnimating;

    @FXML private AnchorPane grid;
    @FXML private Label turnText;
    @FXML private HBox horizontalLabels;
    @FXML private VBox verticalLabels;

    @FXML private VBox gameEndScreen;
    @FXML private Label gameEndText;
    @FXML private Label scoreText;

    public void setModelAndView(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public AnchorPane getGrid() {return grid;}
    public Label getTurnText() {return turnText;}
    public Label getGameEndText() {return gameEndText;}
    public Label getScoreText() {return scoreText;}
    public VBox getGameEndScreen() {return gameEndScreen;}
    public HBox getHorizontalLabels() {return horizontalLabels;}
    public VBox getVerticalLabels() {return verticalLabels;}

    public void tilePress (MouseEvent event) {

        if(isAnimating) return;
        Node n = (Node) event.getTarget();
        String[] split = n.getId().split(",");

        int row = Integer.parseInt(split[0]);
        int col = Integer.parseInt(split[1]);

        model.tryMove(row, col);
    }

    public void passButton(ActionEvent event) {
        model.pass();
    }

    public void playAgain(ActionEvent event) {
        model.newGame();
        view.initializeBoard();
        view.updateBoard();
        view.updateTurnText(); 
        gameEndScreen.setVisible(false);
    }
}
