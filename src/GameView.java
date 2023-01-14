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

    private Path lastHighlightedMove;   // A reference to the last highlighted move, used to reset the colors once the move is no longer highlighted


    public GameView(ViewManager manager) {
        this.manager = manager;
        //TODO: REMOVE THIS
        ArrayList<String> nameArrayList = new ArrayList<String>();
        nameArrayList.add("WHITE");
        nameArrayList.add("BLACK");

        ArrayList<Color> colorArrayList = new ArrayList<Color>();
        colorArrayList.add(Color.PALETURQUOISE);
        colorArrayList.add(Color.BLACK);
        model = new Model(this, 8, 2, colorArrayList, nameArrayList);
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
        initializeBoard();
        updateBoard(this.model.gameBoard);
        updateTurnText(this.model.currentPlayer);
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

    public void updateBoard(Board gameBoard) {

        for (int row = 0; row < gameBoard.gridSize; row++) {
            for (int col = 0; col < gameBoard.gridSize; col++) {
                Circle c = pieces[row][col];
                Checker t = gameBoard.getElementAt(new int[] {row,col});

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

            }
        }

        var possibleMoves = model.gamePathGrid.getNonNullPaths();
        // Only show passButton when the current player has no possible moves
        passButton.setVisible(possibleMoves.size() == 0);
        
        if (!SHOW_MOVE_HINTS) {
            return;
        }

        for (Path move : possibleMoves) {
            var coords = move.coordinates;
            Circle c = pieces[coords[0]][coords[1]];
            c.setFill(POSSIBLE_MOVE_COLOR);
            c.setStroke(Color.TRANSPARENT);
            c.setVisible(true);
        }
    }

    public void updateTurnText(Player currentPlayer) {
        turnText.setText("Current Player: " + currentPlayer.getPlayerName());
    }

    public void showEndGame(ArrayList<Player> winners) {
        turnText.setVisible(false);
        passButton.setVisible(false);

        if (winners.size() == 1) {
            controller.getGameEndText().setText("Winner: " + winners.get(0).getPlayerName());
        }
        else {
            controller.getGameEndText().setText("Draw");
        }

        ArrayList<Player> players = model.gamePlayerManager.playersArray;
        String scoreText = players.get(0).getPlayerName() + ": " + players.get(0).getScore();

        for (int i = 1; i < players.size(); i++) {
            scoreText += " - " + players.get(i).getPlayerName() + ": " + players.get(i).getScore();
        } 

        controller.getScoreText().setText(scoreText);
        controller.getGameEndScreen().setVisible(true);
    }

    private void onHover(Rectangle rect) {
        int[] coords = Util.fromId(rect.getId());

        if (lastHighlightedMove != null) {
            for (Checker checkerToFlip : lastHighlightedMove.checkersInPath) {
                pieces[checkerToFlip.coordinates[0]][checkerToFlip.coordinates[1]].setFill(checkerToFlip.getColor());
            }
            var moveStartingChecker = model.gameBoard.getElementAt(lastHighlightedMove.coordinates);

            if (moveStartingChecker.isEmpty()) {
                pieces[moveStartingChecker.coordinates[0]][moveStartingChecker.coordinates[1]].setFill(POSSIBLE_MOVE_COLOR);
            }
            lastHighlightedMove = null;
        }

        Path move = model.gamePathGrid.getElementAt(coords);

        if (move != null) {
            lastHighlightedMove = move;
            for (Checker checkerFromPath : move.checkersInPath) {
                //TODO Decide what colors tiles "to be flipped" should be
                Color c = checkerFromPath.getColor();
                c = Color.rgb((int)c.getRed(), (int)c.getGreen(), (int)c.getBlue(), 0.5);
                pieces[checkerFromPath.coordinates[0]][checkerFromPath.coordinates[1]].setFill(c);
            }
            pieces[move.coordinates[0]][move.coordinates[1]].setFill(POSSIBLE_MOVE_HIGHLIGHET_COLOR);
        }
    }
}