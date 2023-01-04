import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameView extends ViewState{
	
    public Label turnLabel;
    public GridPane gridPane;
    public GridPane piecePane;
    public VBox verticalLabels;
    public HBox horizontalLabels;
    
    private double pieceRatio = 0.8;    // How big the piece is compared to the tile;

	public GameView(Model model, App app, Stage stage) {
		super(model, app, stage);
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
        piecePane = controller.getPiecePane();
        verticalLabels = controller.getVerticalLabels();
        horizontalLabels = controller.getHorizontalLabels();
	}
	
	@Override
	public void OnEnter() {
		stage.setScene(scene);
		initializeBoard();
	}
	// TODO Add which color the circle should be
    public void addPiece(int row, int column) {
        Circle c = (Circle) piecePane.getChildren().get(column * model.getBoardSize() + row);
        // Change COLOR!
        c.setVisible(true);
    }

    // Changes the color of the specified piece to the opposite
    public void flipPiece(int row, int column) {
        Circle c = (Circle) piecePane.getChildren().get(column * model.getBoardSize() + row);
        c.setFill(c.getFill() == Color.BLACK ? Color.WHITE : Color.BLACK);
    }

    private void initializeBoard() {

        gridPane.setPadding(new Insets(0));

        // Initialize rows and columns of tiles and pieces GridPanes
        for (int i = 0; i < model.getBoardSize(); i++) {
            var columnConstraint = new ColumnConstraints();
            columnConstraint.setPercentWidth(100 / model.getBoardSize());
            columnConstraint.setHalignment(HPos.CENTER);
            gridPane.getColumnConstraints().add(columnConstraint);
            piecePane.getColumnConstraints().add(columnConstraint);
            
            var rowConstraint = new RowConstraints();
            rowConstraint.setPercentHeight(100 / model.getBoardSize());
            rowConstraint.setValignment(VPos.CENTER);
            gridPane.getRowConstraints().add(rowConstraint);
            piecePane.getRowConstraints().add(rowConstraint);
        }
       
        double tileSize = gridPane.getWidth() > gridPane.getHeight() ? gridPane.getHeight() / model.getBoardSize() : gridPane.getWidth() / model.getBoardSize();
        
        // Setup tiles
        for (int col = 0; col < model.getBoardSize(); col++) {
            for (int row = 0; row < model.getBoardSize(); row++) {
                Rectangle tile = new Rectangle(tileSize, tileSize, Color.BEIGE);
                tile.setStroke(Color.BLACK);
                tile.setStrokeWidth(2);
                tile.setStrokeType(StrokeType.INSIDE);
                tile.setArcHeight(0);
                tile.setArcWidth(0);
                tile.setOnMouseClicked(controller::squarePress);
                tile.setId(col + "," + row);
                gridPane.add(tile, col, row);
            } 
        }
        
        // Calculates the radius of pieces, based on how big a percentage the circle should in relation to the tile
        var radius = tileSize * pieceRatio / 2;
    
        // Loop through all cells and initialize empty pieces
        for (int col = 0; col < model.getBoardSize(); col++) {
            for (int row = 0; row < model.getBoardSize(); row++) {
                Circle piece = new Circle(radius, Color.WHITE);
                piece.setStroke(Color.BLACK);
                piece.setStrokeType(StrokeType.INSIDE);
                piece.setVisible(false);
                piece.setId(col + "," + row);
    
                piecePane.add(piece, col, row);
            }
        }

        // Setup axis labels
        for (int i = 0; i < model.getBoardSize(); i++) {
            // Vertical (1-8)
            var vertical = createAxisLabel(model.getBoardSize() - i + "");
            vertical.setPrefHeight(tileSize);
            verticalLabels.getChildren().add(vertical);
            
            // Horizontal (a-h)
            var horizontal = createAxisLabel((char)(i + 97) + "");
            horizontal.setPrefWidth(tileSize);
            horizontalLabels.getChildren().add(horizontal);

        }
    }

    private Label createAxisLabel(String text) {
        Label l = new Label(text);
        l.setFont(new Font(12));
        l.setTextAlignment(TextAlignment.CENTER);
        l.setAlignment(Pos.CENTER);
        
        //TODO Used for debugging, remove when ready
        //l.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        return l;
    }
	
	@Override
	public void Reset() {

	}
}