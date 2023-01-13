import java.util.ArrayList;

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

public class GameView {

    private static final Color TILE_COLOR = Color.BEIGE;
    private static final Color POSSIBLE_MOVE_COLOR = new Color(0.25, 0.25, 1, 0.25);
    private static final Color POSSIBLE_MOVE_HIGHLIGHET_COLOR = new Color(0.25, 0.25, 1, 0.5);
    
    private static final double STROKE_WIDTH = 2;
    private static final double PIECE_RATIO = 0.85;             // How big a percentage the piece takes up on the tile
    private static final Color STROKE_COLOR = Color.BLACK;

    private static final boolean SHOW_MOVE_HINTS = true;        // Whether possible moves are shown to the player
    private static final boolean SHOW_EDGE = false;             // Whether to show the edge where possible moves are possible. For debugging purposes
    private static final Color EDGE_COLOR = Color.LIGHTPINK;

    // MCV
    private ViewManager manager;
    private Model model;
    private GameController controller;

    // UI Elements
    public Scene scene;
    private AnchorPane grid;
    private Label turnText;
    private HBox horizontalLabels;
    private VBox verticalLabels;
    private Button passButton;
    private Rectangle[][] tiles;
    private Circle[][] pieces;

    private Move lastHighlightedMove;   // A reference to the last highlighted move, used to reset the colors once the move is no longer highlighted


    public GameView(ViewManager manager) {
        this.manager = manager;
        model = new Model(this);
        try {
            // Load UI from FXML and create an instance of the corresponding controller class "Controller"
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
    }

    public void onEnter() {
        // Setup Model and UI
        model.newGame();
        initializeBoard();
        updateBoard();
        updateTurnText();
        controller.getGameEndScreen().setVisible(false);
    }

    public void toMenu() {
        manager.toMenu();
    }
    
    public void initializeBoard() {
        // Clear board
        grid.getChildren().clear();
        verticalLabels.getChildren().clear();
        horizontalLabels.getChildren().clear();

        //TODO Check if prefWidth and prefHeight is the same
        // Right now they are defined to in game.fxml, but if the layout is changed we need to make sure to calculate it correctly
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

                Circle piece = new Circle(pieceSize / 2, Color.TRANSPARENT);
                piece.setStroke(Color.TRANSPARENT);
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

    }
    
    private Label createAxisLabel(String text) {
        Label label = new Label(text);
        label.setFont(new Font(12));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    public void resetBoard() {
        turnText.setVisible(true);
        for (int row = 0; row < model.getBoardSize(); row++) {
            for (int col = 0; col < model.getBoardSize(); col++) {
                pieces[row][col].setFill(Color.TRANSPARENT);
                pieces[row][col].setStroke(Color.TRANSPARENT);
            }
        }
    }

    public void updateBoard() {
        Tile[][] board = model.getBoard();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                Circle c = pieces[row][col];
                Tile t = board[row][col];

                c.setVisible(!t.isEmpty());

                if (c.getFill() != t.getColor() && c.getFill() != POSSIBLE_MOVE_COLOR) {
                    if (c.getFill() == Color.TRANSPARENT || c.getFill() == POSSIBLE_MOVE_HIGHLIGHET_COLOR) {
                        Animation.playSound();
                        Animation.halfFlip(c, 250, t.getColor()); 
                    }
                    else {
                        Animation.flipPiece(c, 500, (Color)c.getFill(), t.getColor());
                    }
                }
                else {
                    c.setFill(t.getColor());
                }

                if (SHOW_EDGE) {
                    // Used for showin the edge, which is checked for possible moves
                    tiles[row][col].setFill(board[row][col].isEdge ? EDGE_COLOR : TILE_COLOR);
                }
            }
        }

        var possibleMoves = model.getPossibleMoves();
        // Only show passButton when the current player has no possible moves
        passButton.setVisible(possibleMoves.size() == 0);
        
        if (!SHOW_MOVE_HINTS) {
            return;
        }

        for (Move move : possibleMoves) {
            var t = move.getMoveTile();
            Circle c = pieces[t.getRow()][t.getCol()];
            c.setFill(POSSIBLE_MOVE_COLOR);
            c.setStroke(Color.TRANSPARENT);
            c.setVisible(true);
        }
    }

    public void updateTurnText() {
        turnText.setText("Current Player: " + model.getCurrentPlayer().getName()); 
    }

    public void showEndGame(ArrayList<Player> winners, int[] scores) {
        turnText.setVisible(false);
        passButton.setVisible(false);

        if (winners.size() == 1) {
            controller.getGameEndText().setText("Winner: " + winners.get(0).getName());
        }
        else {
            controller.getGameEndText().setText("Draw");
        }

        ArrayList<Player> players = model.getPlayers();
        String scoreText = players.get(0).getName() + ": " + scores[0];

        for (int i = 1; i < scores.length; i++) {
            scoreText += " - " + players.get(i).getName() + ": " + scores[i]; 
        } 

        controller.getScoreText().setText(scoreText);
        controller.getGameEndScreen().setVisible(true);
    }

    private void onHover(Rectangle rect) {
        int[] coords = Util.fromId(rect.getId());

        if (lastHighlightedMove != null) {
            for (Tile flipTile : lastHighlightedMove.getFlips()) {
                pieces[flipTile.getRow()][flipTile.getCol()].setFill(flipTile.getColor());
            }
            var moveTile = lastHighlightedMove.getMoveTile();
            if (moveTile.isEmpty()) {
                pieces[moveTile.getRow()][moveTile.getCol()].setFill(POSSIBLE_MOVE_COLOR);
            }
            lastHighlightedMove = null;
        }

        Move move = model.getPossibleMove(coords[0], coords[1]);

        if (move != null) {
            lastHighlightedMove = move;
            for (Tile t : move.getFlips()) {
                //TODO Decide what colors tiles "to be flipped" should be
                Color c = t.getColor();
                c = Color.rgb((int)c.getRed(), (int)c.getGreen(), (int)c.getBlue(), 0.5);
                pieces[t.getRow()][t.getCol()].setFill(c);
            }
            pieces[move.getMoveTile().getRow()][move.getMoveTile().getCol()].setFill(POSSIBLE_MOVE_HIGHLIGHET_COLOR);
        }
    }
}