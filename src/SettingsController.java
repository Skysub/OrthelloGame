import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class SettingsController {
    
    private SettingsView view;
    private Model model;        //TODO Create separate model to manage settings?
    private Settings settings;
    private Color[] playerColors = new Color[]{Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
    private int currentPlayer = 0;
    private int colorNum = 0;
    private Player player;

    @FXML TextField customSizeText;
    @FXML TextField playerNameText;
    @FXML Circle playerColor;
    @FXML RadioButton customSizeRadioButton;

    public void setModelAndView(Model model, SettingsView view, Settings settings) {
        this.model = model;
        this.view = view;
        this.settings = settings;
        playerColor.setRadius(50.0); // skal rykkes ind i fxml?
        loadPlayer(0);
    }

    public void radio8(ActionEvent event) {
        settings.setBoardSize(8);
    }

    public void radio16(ActionEvent event) {
        settings.setBoardSize(16);
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
        settings.setBoardSize(newBoardSize);
        return true;
    }

    //GameType Buttons
    public void radio1v1(ActionEvent event) {
        settings.setGameType(GameType.v1);
        numberOfPlayerChange(2);
    }
    public void radio1v1v1v1(ActionEvent event) {
        settings.setGameType(GameType.v4);
        numberOfPlayerChange(4);
    }

    public void radioRandom(ActionEvent event) {
        settings.setGameType(GameType.Random);
        numberOfPlayerChange(1);
    }

    public void radioGreedy(ActionEvent event) {
        settings.setGameType(GameType.Greedy);
        numberOfPlayerChange(1);
    }

    public void radioWeighted(ActionEvent event) {
        settings.setGameType(GameType.Weight);
        numberOfPlayerChange(1);
    }

    public void radioThinking(ActionEvent event) {
        settings.setGameType(GameType.v1); //TODO skal vi lave den her AI?
        numberOfPlayerChange(1);
    }


    //Player stuff
    public void numberOfPlayerChange(int n){
        ArrayList<Player> players = settings.getPlayerList();
        if(n > players.size()){
            for(int i = players.size(); i < n; i++){
                players.add(new Player("Player " + (i+1), playerColors[i])); //TODO find en måde at tilføje spillere
            }
        }
        else{
            for(int i = players.size(); i > n; i--){
                players.remove(i-1);
            }
        }
        currentPlayer = 0;
    }

    private void loadPlayer(int n){
        player = settings.getPlayerAt(Math.abs(n%settings.getPlayerList().size())); //TODO skal laves numerisk
        playerNameText.setText(player.getName());
        colorNum = indexOfColor(player.getColor());
        playerColor.setFill(playerColors[colorNum]);
    }

    public void nextPlayer(){
        currentPlayer++;
        loadPlayer(currentPlayer);
    }

    public void prevPlayer(){
        currentPlayer--;
        loadPlayer(currentPlayer);
    }

    public void playerNameChanged(){
        player.setName(playerNameText.getText());
    }

    public void playerColorChanged(){
        player.setColor(playerColors[colorNum]);
    }

    public void backColor(ActionEvent event){
        colorNum--;
        System.out.println("" + colorNum + "  :  " + playerColors.length + "  :  " + colorNum%playerColors.length);
        colorNum = Math.abs(colorNum%playerColors.length);
        
        playerColor.setFill(playerColors[colorNum]);//skal laves numerisk
        playerColorChanged();
    }

    public void nextColor(ActionEvent event){
        colorNum++;
        colorNum = Math.abs(colorNum%playerColors.length);
        playerColor.setFill(playerColors[colorNum]);//skal laves numerisk
        playerColorChanged();
    }

    private int indexOfColor(Color color){
        for(int i = 0; i < playerColors.length; i++){
            if(color == playerColors[i]) return i;
        }
        return -1;
    }
    
}