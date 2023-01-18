/*
Skrevet af: Mads Christian Wrang Nielsen
Studienummer: s224784
*/

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ViewManager extends Application {

    Stage stage;

    private GameView gameView;
    private MenuView menuView;
    private SettingsView settingsView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Load icon
        stage.getIcons().add(new Image(ViewManager.class.getResourceAsStream("resources/icon.png")));

        // Setup views
        gameView = new GameView(this);
        menuView = new MenuView(this);
        settingsView = new SettingsView(this);
        
        // Create folder structure to allow game saving, if it doesn't already exists
        FileHandler.CreateFolderStructure();

        // Default view is menu
        toMenu();
        stage.setResizable(false);
        stage.show();
    }

    public void toGame() {
        gameView.onEnter();
        stage.setScene(gameView.scene);
        stage.setTitle("Game");
    }

    public void toMenu() {
        stage.setScene(menuView.scene);
        stage.setTitle("Menu");
    }

    public void toSettings() {
        stage.setScene(settingsView.scene);
        stage.setTitle("Settings");
    }

    public void quitGame() {
        stage.close();
    }
}
