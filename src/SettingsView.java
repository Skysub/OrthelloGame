import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SettingsView {

    public Scene scene;
    private ViewManager manager;
    private SettingsController controller;
    
    public SettingsView(ViewManager manager) {
        this.manager = manager;
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("settings.fxml"));
            scene = loader.load();
            controller = (SettingsController) loader.getController();            
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        controller.setModelAndView(null, this);
    }

    public void toMenu() {
        manager.toMenu();
    }
}