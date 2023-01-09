import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private static final Color TILE_COLOR = Color.BEIGE;
    private static final Color WHITE_COLOR = Color.WHITE;
    private static final Color BLACK_COLOR = Color.BLACK;
    private static final Color ERROR_COLOR = Color.PURPLE;

    private static final Color POSSIBLE_MOVE_COLOR = new Color(0.25, 0.25, 1, 0.25);
    private static final Color POSSIBLE_MOVE_HIGHLIGHET_COLOR = new Color(0.25, 0.25, 1, 0.5);

    private static final Color STROKE_COLOR = Color.BLACK;
    private static final double STROKE_WIDTH = 2;
    private static final double PIECE_RATIO = 0.85;         // How big a percentage the piece takes up on the tile

    private static final boolean SHOW_MOVE_HINTS = true;    // Whether possible moves are shown to the player

    private Model model;
    private Controller controller;

    private AnchorPane grid;
    private Label turnText;
    private HBox horizontalLabels;
    private VBox verticalLabels;
    private Button passButton;

    private Rectangle[][] tiles;
    private Circle[][] pieces;

    private PossibleMove lastHighlightedMove;

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
        passButton = controller.getPassButton();

        // Setup Model and UI
        model.newGame();
        initializeBoard();
        updateBoard();
        updateTurnText();

        stage.setTitle("Reversi");
        stage.setScene(scene);
        stage.show();
    }
    
    private void initializeBoard() {
        //TODO Check if prefWidth and prefHeight is the same
        double boardWidth = grid.getPrefWidth();
        double tileSize = (boardWidth - STROKE_WIDTH * (model.getBoardSize() + 1)) / model.getBoardSize();
        double pieceSize = tileSize * PIECE_RATIO;

        tiles = new Rectangle[model.getBoardSize()][model.getBoardSize()];
        pieces = new Circle[model.getBoardSize()][model.getBoardSize()];
        
        for (int row = 0; row < model.getBoardSize(); row++) {
            for (int col = 0; col < model.getBoardSize(); col++) {
                Rectangle tile = new Rectangle(tileSize, tileSize, TILE_COLOR);
                tile.setStroke(STROKE_COLOR);
                tile.setStrokeWidth(STROKE_WIDTH);
                tile.setStrokeType(StrokeType.OUTSIDE);
                tile.setOnMousePressed(controller::tilePress);
                if (SHOW_MOVE_HINTS) {
                    tile.setOnMouseEntered(event -> onHover(tile));
                }
                tile.setId(Util.toId(row, col));
                
                AnchorPane.setTopAnchor(tile, row * (tileSize + STROKE_WIDTH));
                AnchorPane.setLeftAnchor(tile, col * (tileSize + STROKE_WIDTH));
                
                Circle piece = new Circle(pieceSize / 2);
                piece.setStroke(STROKE_COLOR);
                piece.setStrokeWidth(STROKE_WIDTH / 2);
                piece.setStrokeType(StrokeType.INSIDE);
                piece.setMouseTransparent(true);
                piece.setVisible(false);
                
                AnchorPane.setTopAnchor(piece, STROKE_WIDTH + (tileSize - pieceSize) / 2 + (tileSize + STROKE_WIDTH) * row);
                AnchorPane.setLeftAnchor(piece, STROKE_WIDTH + (tileSize - pieceSize) / 2 + (tileSize + STROKE_WIDTH) * col);
                
                tiles[row][col] = tile;
                pieces[row][col] = piece;
                grid.getChildren().addAll(tile, piece);
            }
        }
        
        // Create axislabels
        for (int i = 0; i < model.getBoardSize(); i++) {
            var vLabel = createAxisLabel(model.getBoardSize() - i + "");
            vLabel.setPrefSize(verticalLabels.getPrefWidth(), tileSize + STROKE_WIDTH);
            verticalLabels.getChildren().add(vLabel);
            
            var hLabel = createAxisLabel((char)(i + 97) + "");
            hLabel.setPrefSize(tileSize + STROKE_WIDTH, horizontalLabels.getPrefHeight());
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
                // Used for showin the edge, which is checked for possible moves
                //tiles[row][col].setFill(board[row][col].isEdge() ? Color.RED : TILE_COLOR);
                pieces[row][col].setVisible(!board[row][col].isEmpty());
                pieces[row][col].setFill(getColorFromTile(board[row][col].getType()));
                pieces[row][col].setStroke(STROKE_COLOR);
            }
        }

        var moves = model.getPossibleMoves();
        // Only show passButton when the current player has no possible moves
        passButton.setVisible(moves.size() == 0);
        
        if (!SHOW_MOVE_HINTS) {
            return;
        }

        for (PossibleMove move : model.getPossibleMoves()) {
            var t = move.getTile();
            Circle c = pieces[t.getRow()][t.getCol()];
            c.setFill(POSSIBLE_MOVE_COLOR);
            c.setVisible(true);
        }
    }

    public void updateTurnText() {
        turnText.setText("Current Player: " + model.getCurrentPlayer().toString()); 
    }

    public void showEndGame(TileType winner, int whiteTiles, int blackTiles) {
        if (winner == TileType.Empty) {
            controller.getGameEndText().setText("Draw");
        }
        else {
            controller.getGameEndText().setText("Winner: " + winner.toString());
        }
        controller.getScoreText().setText("W: " + whiteTiles + " - B: " + blackTiles);
        controller.getGameEndScreen().setVisible(true);
    }

    private Color getColorFromTile(TileType t) {
        if (t == TileType.White) {
            return WHITE_COLOR;
        }
        else if (t == TileType.Black) {
            return BLACK_COLOR;
        }
        else {
            return ERROR_COLOR;
        }
    }

    private void onHover(Rectangle rect) {
        int[] coords = Util.fromId(rect.getId());

        if (lastHighlightedMove != null) {
            for (Tile flipTile : lastHighlightedMove.getToBeFlipped()) {
                pieces[flipTile.getRow()][flipTile.getCol()].setFill(getColorFromTile(flipTile.getType()));
            }
            var moveTile = lastHighlightedMove.getTile();
            if (moveTile.isEmpty()) {
                pieces[moveTile.getRow()][moveTile.getCol()].setFill(POSSIBLE_MOVE_COLOR);
            }
            lastHighlightedMove = null;
        }

        PossibleMove move = model.getPossibleMove(coords[0], coords[1]);

        if (move != null) {
            lastHighlightedMove = move;
            for (Tile t : move.getToBeFlipped()) {
                //TODO Decide what colors tiles "to be flipped" should be
                Color c = getColorFromTile(model.getCurrentPlayer());
                c = Color.rgb((int)c.getRed(), (int)c.getGreen(), (int)c.getBlue(), 0.5);
                pieces[t.getRow()][t.getCol()].setFill(c);
            }
            pieces[move.getTile().getRow()][move.getTile().getCol()].setFill(POSSIBLE_MOVE_HIGHLIGHET_COLOR);
        }
    }
}