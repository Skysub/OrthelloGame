/*
Skrevet af: Mads Christian Wrang Nielsen
Studienummer: s224784
*/

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

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
