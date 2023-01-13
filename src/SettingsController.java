import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class SettingsController {

    public static Settings settings = new Settings();
    private SettingsView view;

    @FXML TextField customSizeText;
    @FXML RadioButton customSizeRadioButton;

    public void setModelAndView(Model model, SettingsView view) {
        this.view = view;
    }

    public void back(ActionEvent event) {
        view.toMenu();
    }

    public void radio8(ActionEvent event) {
        settings.boardSize = 8;
    }

    public void radio16(ActionEvent event) {
        settings.boardSize = 16;
    }

    public void radioCustom(ActionEvent event) {
        parseCustomSize();
    }
    
    public void boardSizeTextChanged(ActionEvent event) {
        if (parseCustomSize()) {
            customSizeRadioButton.setSelected(true);
        }
    }
    
    public boolean parseCustomSize() {
        int newBoardSize;

        try {
            newBoardSize = Integer.parseInt(customSizeText.getText());
        }
        catch (NumberFormatException e) {
            customSizeText.clear();
            customSizeText.setPromptText("Not an integer");
            customSizeText.getParent().requestFocus();
            return false;
        }
        
        //TODO Validate whether the value makes sense. Set a range eg. 4-26
        settings.boardSize = newBoardSize;
        return true;
    }

    //GameType Buttons
    public void radio1v1(ActionEvent event) {
        System.out.println("1v1");
    }

    public void radioRandom(ActionEvent event) {
        System.out.println("Random");
    }

    public void radioGreedy(ActionEvent event) {
        System.out.println("Greed");
    }

    public void radioWeighted(ActionEvent event) {
        System.out.println("Weight");
    }

    public void radioThinking(ActionEvent event) {
        System.out.println("Think");
    }

    public void setReversi(ActionEvent event) {
        settings.gameType = GameType.Reversi;
    }

    public void setOthello(ActionEvent event) {
        settings.gameType = GameType.Othello;
    }

    public void setRolit(ActionEvent event) {
        settings.gameType = GameType.Rolit;
    }
}

class Settings {

    int boardSize;
    GameType gameType;

    //TODO AI

    public Settings() {
        boardSize = 8;
        gameType = GameType.Reversi;
    }

}