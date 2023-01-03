import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MenuScreen extends GameState {
	String name = "Othello";

	Button startButton = new Button();
	Button settingsButton = new Button();
	Text text = new Text();

	public MenuScreen(Stage stage) {
		this.stage = stage;
		
		// Basis for Scene
		Pane root = new Pane();
		VBox forText = new VBox();
		scene = new Scene(root, 800, 800, Color.WHITE);

		// Adds notes to roots
		forText.getChildren().add(text);
		forText.setAlignment(Pos.CENTER);
		root.getChildren().add(forText);
		root.getChildren().add(startButton);
		root.getChildren().add(settingsButton);

		// Places text in VBox and VBox on Pane
		text.setStyle("-fx-font: 150 arial;");
		text.setText(name);
		forText.setMinWidth(800);
		forText.setAlignment(Pos.CENTER);
		forText.setLayoutY(150);

		// places buttons - Made to functions
		placeButtons(scene, 200, 70, 100);
	}
	
	@Override
	public void OnEnter() {
		stage.setScene(scene);		
	}

	private void placeButtons(Scene scene, int buttonWidth, int buttonHeight, int buttonSpacing) { // ButtonSpacing is a bit wack
// Start-Game button
		startButton.setText("Start Game");
		Font font = new Font(30);
		startButton.setFont(font);
		buttonSize(startButton, buttonWidth, buttonHeight);
		buttonPos(startButton, (scene.getWidth() - buttonWidth) / 2, (scene.getHeight() - buttonHeight) / 2);
		this.startButton.setOnAction(this::toGame);

// settings button
		settingsButton.setText("Settings");
		font = new Font(15);
		settingsButton.setFont(font);
		buttonSize(settingsButton, buttonWidth, buttonHeight / 2); // Er lavere end start game, fjern /2.
		buttonPos(settingsButton, (scene.getWidth() - buttonWidth) / 2,
				((scene.getHeight() - buttonHeight) / 2) + buttonSpacing);
		this.settingsButton.setOnAction(this::toSettings);
	}

	private void buttonPos(Button button, Double x, Double y) {
		button.setLayoutX(x);
		button.setLayoutY(y);
	}

	private void buttonSize(Button button, double width, double Height) {
		button.setMinWidth(width);
		button.setMinHeight(Height);
	}

	private void toGame(ActionEvent event) {
		System.out.println("GAME TIME!!");
	}

	private void toSettings(ActionEvent event) {
		System.out.println("Settings");
	}

	@Override
	public void Reset() {
				
	}
}
