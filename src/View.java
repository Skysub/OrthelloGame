import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class View extends Application {

    private Model model;
    private Controller controller;

    private AnchorPane grid;
    private Label turnText;
    private HBox horizontalLabels;
    private VBox verticalLabels;

    private Rectangle[][] tiles;
    private Circle[][] pieces;

    private double strokeWidth = 2;         // The width of the stroke on the tiles
    private double pieceTileRatio = 0.85;   // How big a percentage the piece takes up on the tile

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        model = new Model(this);
        Scene scene;
        try {
            // Load UI from FXML and create Controller
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("game.fxml"));
            scene = loader.load();
            controller = loader.getController();
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        controller.setModelAndView(model, this);

        // Retrieve objects from Controller FXML
        grid = controller.getGrid();
        turnText = controller.getTurnText();
        horizontalLabels = controller.getHorizontalLabels();
        verticalLabels = controller.getVerticalLabels();

        // Setup Model and UI
        model.newGame();
        initializeBoard();

        stage.setTitle("Reversi");
        stage.setScene(scene);
        stage.show();
    }
    
    public void initializeBoard() {
        //TODO Check if prefWidth and prefHeight is the same
        double boardWidth = grid.getPrefWidth();
        double tileSize = (boardWidth - strokeWidth * (model.getBoardSize() + 1)) / model.getBoardSize();
        double pieceSize = tileSize * pieceTileRatio;

        tiles = new Rectangle[model.getBoardSize()][model.getBoardSize()];
        pieces = new Circle[model.getBoardSize()][model.getBoardSize()];
        
        for (int row = 0; row < model.getBoardSize(); row++) {
            for (int col = 0; col < model.getBoardSize(); col++) {
                Rectangle tile = new Rectangle(tileSize, tileSize, Color.BEIGE);
                tile.setStroke(Color.BLACK);
                tile.setStrokeWidth(strokeWidth);
                tile.setStrokeType(StrokeType.OUTSIDE);
                tile.setOnMouseClicked(controller::tilePress);
                tile.setId(row + "," + col);
                
                AnchorPane.setTopAnchor(tile, row * (tileSize + strokeWidth));
                AnchorPane.setLeftAnchor(tile, col * (tileSize + strokeWidth));
                
                Circle piece = new Circle(pieceSize / 2, Color.TRANSPARENT);
                piece.setStroke(Color.TRANSPARENT);
                piece.setStrokeWidth(1);
                piece.setStrokeType(StrokeType.INSIDE);
                piece.setMouseTransparent(true);
                piece.setVisible(false);
                
                AnchorPane.setTopAnchor(piece, strokeWidth + (tileSize - pieceSize) / 2 + (tileSize + strokeWidth) * row);
                AnchorPane.setLeftAnchor(piece, strokeWidth + (tileSize - pieceSize) / 2 + (tileSize + strokeWidth) * col);
                
                tiles[row][col] = tile;
                pieces[row][col] = piece;
                grid.getChildren().addAll(tile, piece);
            }
        }
        
        // Create axislabels
        for (int i = 0; i < model.getBoardSize(); i++) {
            var vLabel = createAxisLabel(model.getBoardSize() - i + "");
            vLabel.setPrefSize(verticalLabels.getPrefWidth(), tileSize + strokeWidth);
            verticalLabels.getChildren().add(vLabel);
            
            var hLabel = createAxisLabel((char)(i + 97) + "");
            hLabel.setPrefSize(tileSize + strokeWidth, horizontalLabels.getPrefHeight());
            horizontalLabels.getChildren().add(hLabel);
        }

        controller.getGameEndScreen().setVisible(false);
    }
    
    private Label createAxisLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font(12));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    public void updateBoard() {
        Tile[][] board = model.getBoard();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                Circle c = pieces[row][col];
                Tile t = board[row][col];

                c.setVisible(!t.isEmpty());

                if (c.getFill() != getColorFromTile(t.getType())) {
                    controller.isAnimating = true;
                    if (c.getFill() == Color.TRANSPARENT) {
                        Animation.playSound();
                        Animation.halfFlip(c, 250, getColorFromTile(t.getType()), () -> controller.isAnimating = false);
                    }
                    else {
                        Animation.flipPiece(c, 500, (Color)c.getFill(), getColorFromTile(t.getType()), () -> controller.isAnimating = false);
                    }
                }
                else {
                    c.setFill(getColorFromTile(t.getType()));
                }
            }
        }
    }

    public void updateTurnText() {
        turnText.setText("Current Player: " + model.getCurrentPlayer().toString()); 
    }

    public void showEndGame(TileType winner, int whiteTiles, int blackTiles) {
        controller.getGameEndText().setText((winner == TileType.Empty) ? "Draw" : "Winner: " + winner.toString());
        controller.getScoreText().setText("W: " + whiteTiles + " - B: " + blackTiles);
        controller.getGameEndScreen().setVisible(true);
    }

    private Color getColorFromTile(TileType t) {
        switch (t) {
            case White:
                return Color.WHITE;
            case Black:
                return Color.BLACK;
            case Empty:
            default:
                return Color.TRANSPARENT;
        }
    }
}