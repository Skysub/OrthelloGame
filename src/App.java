import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

	ViewStateManager viewStateManager;
    private Model model;

	public void start(Stage stage) {
        model = new Model(this);
		viewStateManager = new ViewStateManager();
		
		viewStateManager.AddViewState("MenuView", new MenuView(model, this, stage));
		viewStateManager.AddViewState("GameView", new GameView(model, this, stage));
		viewStateManager.AddViewState("SettingsView", new SettingsView(model, this, stage));
		
		viewStateManager.ChangeViewState("MenuView"); //<--- Du skal ændre den her, for at programmet starter med en specifik skærm

		stage.setTitle("Othello Game");
		stage.setResizable(false); //TODO Determine whether we should make the window responsive (Bind tiles size to percentage of window size)
		stage.getIcons().add(new Image("icon.png"));
		stage.show();
	}

	public static void main(String args[]) {
		launch(args);
	}
}