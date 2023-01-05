import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class Controller {
    
    private Model model;
    private ViewState view;

    @FXML private Label turnLabel;
    @FXML private GridPane gridPane;
    @FXML private GridPane piecePane;
    @FXML private VBox verticalLabels;
    @FXML private HBox horizontalLabels;

    public void setModelAndView(Model model, ViewState view) {
        this.model = model;
        this.view = view;
    }

    public Label getTurnLabel() {return turnLabel;}
    public GridPane getGridPane() {return gridPane;}
    public GridPane getPiecePane() {return piecePane;}
    public VBox getVerticalLabels() {return verticalLabels;}
    public HBox getHorizontalLabels() {return horizontalLabels;}

    public void squarePress (MouseEvent event) {
        
        // Parse which tile was pressed based on ID: "row,column"
        Rectangle r = (Rectangle) event.getTarget();
        var split = r.getId().split(",");
        int column = Integer.parseInt(split[0]);
        int row = Integer.parseInt(split[1]);

        System.out.println(row + "-" + column); //TODO Remove when done with debuggin

        //TODO Send indexes to model to handle move
    }

    public void QuitGame(ActionEvent event) {
        view.ChangeViewState(ViewType.Menu);
    }
}
