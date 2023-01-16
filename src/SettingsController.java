import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsController {

    private SettingsView view;
    private int currentPlayer = 0;
    private int currentColor = 0;
    private int maxNameLength = 32;
    private Color[] possibleColors = new Color[]{Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

    @FXML TextField customSizeText;
    @FXML RadioButton customSizeRadioButton;
    @FXML Circle playerColor;
    @FXML TextField playerNameText;
    @FXML Label playerNumber;

    @FXML RadioButton humanRadio;
    @FXML RadioButton randomRadio;
    @FXML RadioButton greedyRadio;
    @FXML RadioButton weightedRadio;

    public void setModelAndView(ReversiModel model, SettingsView view) {
        this.view = view;
        playerColor.setRadius(25);
        loadPlayer();
    }

    public void back(ActionEvent event) {
        view.toMenu();
    }

    public void radio8(ActionEvent event) {
        Settings.boardSize = 8;
    }

    public void radio16(ActionEvent event) {
        Settings.boardSize = 16;
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
        Settings.boardSize = newBoardSize;
        return true;
    }

    //GameType Buttons
    public void radioHuman(ActionEvent event) {
        Settings.playerAIModes[currentPlayer] = AIModes.HumanPlayer;
    }

    public void radioRandom(ActionEvent event) {
        Settings.playerAIModes[currentPlayer] = AIModes.AIRandom;
    }

    public void radioGreedy(ActionEvent event) {
        Settings.playerAIModes[currentPlayer] = AIModes.AIGreedy;
    }

    public void radioWeighted(ActionEvent event) {
        Settings.playerAIModes[currentPlayer] = AIModes.AIWeighted;
    }


    public void setReversi(ActionEvent event) {
        Settings.gameMode = Constants.GAMEMODE_REVERSI;
        Settings.nrPlayers = 2;
    }

    public void setOthello(ActionEvent event) {
        Settings.nrPlayers = 2;
        Settings.gameMode = Constants.GAMEMODE_OTHELLO;
    }

    public void setRolit(ActionEvent event) {
        Settings.nrPlayers = 4;
        Settings.gameMode = Constants.GAMEMODE_ROLIT;
    }

    private void loadPlayer(){
        playerNameText.setText(Settings.playerNames.get(currentPlayer));
        playerColor.setFill(Settings.playerColors.get(currentPlayer));
        currentColor = indexOf(Settings.playerColors.get(currentPlayer));
        playerNumber.setText("Player: " + (currentPlayer+1));


        AIModes loadMode = Settings.playerAIModes[currentPlayer];
        humanRadio.setSelected(AIModes.HumanPlayer == loadMode);
        randomRadio.setSelected(AIModes.AIRandom == loadMode);
        greedyRadio.setSelected(AIModes.AIGreedy == loadMode);
        weightedRadio.setSelected(AIModes.AIWeighted == loadMode);
    }

    public void nextPlayer(){
        currentPlayer++;
        if(currentPlayer > Settings.nrPlayers-1) currentPlayer = 0;
        loadPlayer();
    }

    public void prevPlayer(){
        currentPlayer--;
        if(currentPlayer < 0) currentPlayer = Settings.nrPlayers-1;
        loadPlayer();
    }

    public void nextColor(){
        currentColor++;
        if(currentColor > possibleColors.length-1) currentColor = 0;
        playerColor.setFill(possibleColors[currentColor]);
        Settings.playerColors.set(currentPlayer, possibleColors[currentColor]);
    }

    public void prevColor(){
        currentColor--;
        if(currentColor < 0) currentColor = possibleColors.length-1;
        playerColor.setFill(possibleColors[currentColor]);
        Settings.playerColors.set(currentPlayer, possibleColors[currentColor]);
    }

    public void playerNameChanged(){
        if(playerNameText.getText().length() > maxNameLength){
            Settings.playerNames.set(currentPlayer, playerNameText.getText().substring(0, maxNameLength-1));
            playerNameText.setText("max Characters: " + maxNameLength);
        }
        Settings.playerNames.set(currentPlayer, playerNameText.getText());
    }

    private int indexOf(Color color){
        for(int i = 0; i < possibleColors.length; i++){
            if(color == possibleColors[i]) return i;
        }
        return -1;
    }

}

class Settings {
    //All settings are initialized to the default 8x8 Reversi
    static int boardSize = 8;
    static int nrPlayers = 2;
    static int gameMode = Constants.GAMEMODE_REVERSI;
    static ArrayList<Color> playerColors = new ArrayList<Color>(Arrays.asList(Color.WHITE,Color.BLACK,Color.RED,Color.GREEN));
    static ArrayList<String> playerNames = new ArrayList<String>(Arrays.asList("WHITE","BLACK", "RED", "GREEN"));

    static AIModes[] playerAIModes = new AIModes[] {AIModes.AIWeighted,AIModes.AIGreedy,AIModes.HumanPlayer,AIModes.HumanPlayer};

    //TODO: Tilføj gametype
    //TODO AI
}