import javafx.application.Application;
import javafx.stage.Stage;

public class ViewManager extends Application {

    private Stage stage;

    private GameView gameView;
    private MenuView menuView;
    private SettingsView settingsView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        gameView = new GameView(this);
        //TODO Move setupView into contructor;
        gameView.setupView();

        menuView = new MenuView(this);
        settingsView = new SettingsView(this);

        // Default view
        toMenu();
        stage.setTitle("Reversi");
        stage.show();
    }

    public void toGame() {
        gameView.onEnter();
        stage.setScene(gameView.scene);
    }

    public void toMenu() {
        stage.setScene(menuView.scene);
    }

    public void toSettings() {
        stage.setScene(settingsView.scene);
    }
}