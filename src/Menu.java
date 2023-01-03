import javafx.application.Application;
import javafx.stage.Stage;

public class Menu extends Application {

	GameStateManager gameStateManagaer;

	public void start(Stage stage) {
		gameStateManagaer = new GameStateManager();
		
		gameStateManagaer.AddGameState("MenuScreen", new MenuScreen(stage));
		gameStateManagaer.AddGameState("GameScreen", new GameScreen(stage));
		
		gameStateManagaer.ChangeGameState("MenuScreen");

		stage.setTitle("Orthello");
		stage.show();
	}

	public static void main(String args[]) {
		launch(args);
	}
}