import javafx.event.ActionEvent;

public class MenuController {
    
    private Model model;
    private MenuView view;

    public void setModelAndView(Model model, MenuView view) {
        this.model = model;
        this.view = view;
    }

    public void startGame (ActionEvent event) {
        view.ChangeViewState("GameView");
    }

    public void openSettings (ActionEvent event) {
        view.ChangeViewState("SettingsView");
    }
}
