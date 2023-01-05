import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class SettingsController {
    
    private SettingsView view;
    private Model model;        //TODO Create separate model to manage settings?

    @FXML TextField customSizeText;
    @FXML RadioButton customSizeRadioButton;

    public void setModelAndView(Model model, SettingsView view) {
        this.model = model;
        this.view = view;
    }

    public void radio8(ActionEvent event) {
        model.setBoardSize(8);
    }

    public void radio16(ActionEvent event) {
        model.setBoardSize(16);
    }

    public void radioCustom(ActionEvent event) {
        parseCustomSize();
    }
    
    public void back(ActionEvent event) {
        view.ChangeViewState(ViewType.Menu);
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
        model.setBoardSize(newBoardSize);
        return true;
    }
}