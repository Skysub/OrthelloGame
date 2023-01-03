import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

public class View extends Application {

    private Controller controller;
    private Model model;

    public Label turnLabel;
    public GridPane gridPane;
    public StackPane stackPane;
    public VBox verticalLabels;
    public HBox horizontalLabels;

    @Override
    public void start(Stage primaryStage) {

        model = new Model(this);
        Scene scene;
       
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("game.fxml"));
            scene = loader.load();
            controller = (Controller) loader.getController();            
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        controller.setModelAndView(model, this);
        turnLabel = controller.getTurnLabel();
        gridPane = controller.getGridPane();
        verticalLabels = controller.getVerticalLabels();
        horizontalLabels = controller.getHorizontalLabels();
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Othello Game");
        primaryStage.show();
        
        initializeBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void initializeBoard() {

        double tileSize = gridPane.getHeight() / model.getBoardSize();

        // Setup tiles
        for (int col = 0; col < model.getBoardSize(); col++) {
            for (int row = 0; row < model.getBoardSize(); row++) {
                Rectangle r = new Rectangle(tileSize, tileSize, Color.BEIGE);
                r.setStroke(Color.BLACK);
                r.setStrokeWidth(2);
                r.setStrokeType(StrokeType.INSIDE);
                r.setOnMouseClicked(controller::squarePress);
                r.setId(col + "," + row);

                gridPane.add(r, col, row);
            } 
        }

        // Setup axis labels
        for (int i = 0; i < model.getBoardSize(); i++) {
            // Vertical
            var vertical = createAxisLabel(model.getBoardSize() - i + "");
            vertical.setPrefSize(verticalLabels.getWidth(), verticalLabels.getHeight() / model.getBoardSize());
            verticalLabels.getChildren().add(vertical);

            // Horizontal
            var horizontal = createAxisLabel((char)(i + 97) + "");
            horizontal.setPrefSize(horizontalLabels.getWidth() / model.getBoardSize(), horizontalLabels.getHeight());
            horizontalLabels.getChildren().add(horizontal);
        }
    }

    public Label createAxisLabel(String text) {
        Label l = new Label(text);
        l.setFont(new Font(16));
        l.setAlignment(Pos.CENTER);
        return l;
    }
}