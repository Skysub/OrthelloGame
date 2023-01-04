import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class SettingsView extends ViewState {

    private SettingsController controller;
    
    public SettingsView(Model model, App view, Stage stage) {
		super(model, view, stage);
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("settings.fxml"));
            scene = loader.load();
            controller = (SettingsController) loader.getController();            
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        controller.setModelAndView(model, this);
    }

	@Override
	public void OnEnter() {
		stage.setScene(scene);
        
        // Load settings
	}
    
    @Override
	public void Reset() {
				
	}    
}
