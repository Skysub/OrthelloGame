/*
Skrevet af: Mads Christian Wrang Nielsen
Studienummer: s224784
*/

import javafx.event.ActionEvent;

public class MenuController {
    
    private MenuView view;

    public void setView(MenuView view) {
       this.view = view;
    }

    public void startGame (ActionEvent event) {
        view.toGame();
    }

    public void openSettings (ActionEvent event) {
        view.toSettings();
    }

    public void quitGame (ActionEvent event) {
        view.quitGame();
    }
}
