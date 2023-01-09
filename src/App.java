import java.util.List;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

	ViewStateManager viewStateManager;
	InstanceTests tests;
	private Model model;
	private Stage theStage;

	public void start(Stage stage) {
		theStage = stage;
		model = new Model(this);
		viewStateManager = new ViewStateManager();

		viewStateManager.AddViewState(ViewType.Menu, new MenuView(model, this, stage));
		viewStateManager.AddViewState(ViewType.Game, new GameView(model, this, stage));
		viewStateManager.AddViewState(ViewType.Settings, new SettingsView(model, this, stage));

		viewStateManager.ChangeViewState(ViewType.Menu); // <--- Du skal ændre den her, for at programmet starter med en
															// specifik skærm

		stage.setTitle("Othello Game");
		stage.setResizable(false); // TODO Determine whether we should make the window responsive (Bind tiles size
									// to percentage of window size)
		stage.getIcons().add(new Image("icon.png"));
		stage.show();

		//Support for unit tests
		tests = new InstanceTests(this);
		List<String> arguments = getParameters().getRaw();
		if (arguments.get(0).equals("RunTests"))
			tests.Run(arguments);
	}

	public static void main(String args[]) {
		launch(args);
	}

	public Stage getStage() {
		return theStage;
	}

}