import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class ViewState {
	public Scene scene;
	protected Stage stage;
	protected Controller controller;
	protected Model model;
	protected App app;
    
    public ViewState(Model model, App app, Stage stage) {
    	this.model = model;
    	this.app = app;
    	this.stage = stage;
    }
	
	public abstract void Reset();
	public abstract void OnEnter();
	
	void ChangeViewState(String name)
	  {
		app.viewStateManager.ChangeViewState(name);
	  }
}
