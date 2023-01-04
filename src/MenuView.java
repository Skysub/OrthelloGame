import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class MenuView extends ViewState {

	private MenuController controller;

	public MenuView(Model model, App view, Stage stage) {
		super(model, view, stage);

        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("menu.fxml"));
            scene = loader.load();
            controller = (MenuController) loader.getController();            
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
	}

	@Override
	public void Reset() {

	}
}
