import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class GameState {
	public Scene scene;
	protected Stage stage;
	
	public abstract void Reset();
	public abstract void OnEnter();
}
