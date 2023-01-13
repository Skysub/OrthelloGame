import javafx.application.Application;
import javafx.stage.Stage;

public class ViewManager extends Application {

    GameView gameView;
    Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        gameView = new GameView(this);
        gameView.setupView();

        // Default view
        toGame();
        stage.setTitle("Reversi");
        stage.show();
    }

    public void toGame() {
        gameView.onEnter();
        stage.setScene(gameView.scene);
    }
}
