import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuView {

	public Scene scene;
	private ViewManager manager;
	private MenuController controller;

	public MenuView(ViewManager manager) {
		this.manager = manager;
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("menu.fxml"));
            scene = loader.load();
            controller = (MenuController) loader.getController();            
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
		//TODO
        controller.setModelAndView(null, this);
	}

	public void toGame() {
		manager.toGame();
	}

	public void toSettings() {
		manager.toSettings();
	}
}