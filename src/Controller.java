import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class Controller {
    
    private Model model;
    private View view;

    @FXML private Label turnLabel;
    @FXML private GridPane gridPane;
    @FXML private GridPane piecePane;
    @FXML private VBox verticalLabels;
    @FXML private HBox horizontalLabels;
    @FXML private VBox gameEndScreen;
    @FXML private Label gameEndText;
    @FXML private Label scoreText;

    public void setModelAndView(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public Label getTurnLabel() {return turnLabel;}
    public GridPane getGridPane() {return gridPane;}
    public GridPane getPiecePane() {return piecePane;}
    public VBox getVerticalLabels() {return verticalLabels;}
    public HBox getHorizontalLabels() {return horizontalLabels;}
    public VBox getGameEndScreen() {return gameEndScreen;}
    public Label getGameEndText() {return gameEndText;}
    public Label getScoreText() {return scoreText;}

    public void squarePress (MouseEvent event) {
        
        // Parse which tile was pressed based on ID: "row,column"
        Rectangle r = (Rectangle) event.getTarget();
        var split = r.getId().split(",");
        int column = Integer.parseInt(split[0]);
        int row = Integer.parseInt(split[1]);

        //Send the tile to the model
        System.out.println(row + "-" + column);
        int[] coords = {row, column};
        model.step(coords);
        int[] array = model.calculateScoreArray();
        System.out.println(model.whichColourTurn);
        System.out.println(model.turnsTaken);
    }

    public void playAgain(MouseEvent event) {
        //TODO Play again
        System.out.println("Play Again");
    }
}
