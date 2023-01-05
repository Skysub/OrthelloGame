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

	private void toGame(ActionEvent event) {
		ChangeViewState(ViewType.Game);
	}

	private void toSettings(ActionEvent event) {
		ChangeViewState(ViewType.Settings);
	}
}