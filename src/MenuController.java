import javafx.event.ActionEvent;

public class MenuController {
    
    private MenuView view;

    //TODO
    public void setModelAndView(ReversiModel model, MenuView view) {
       //this.model = model;
       this.view = view;
    }

    public void startGame (ActionEvent event) {
        view.toGame();
    }

    public void openSettings (ActionEvent event) {
        view.toSettings();
    }
}
