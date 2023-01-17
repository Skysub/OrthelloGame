import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsController {

	private SettingsView view;
	private int currentPlayer = 0;
	private int currentColor = 0;
	private int maxNameLength = 32;
	private Color[] possibleColors = new Color[] { Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE,
			Color.YELLOW };

	@FXML
	Circle playerColor;
	@FXML
	TextField playerNameText;
	@FXML
	Label playerNumber;

	@FXML
	RadioButton humanRadio;
	@FXML
	RadioButton randomRadio;
	@FXML
	RadioButton greedyRadio;
	@FXML
	RadioButton weightedRadio;

	@FXML
	CheckBox showMoveHints;
	@FXML
	CheckBox showAnimations;

	public void setModelAndView(ReversiModel model, SettingsView view) {
		this.view = view;
		loadPlayer();
	}

	public void back(ActionEvent event) {
		playerNameChanged();
		view.toMenu();
	}

	// Checkboxes
	public void onMoveHintsChanged(ActionEvent event) {
		Settings.showMoveHints = showMoveHints.isSelected();
	}

	public void onShowAnimationsChanged(ActionEvent event) {
		Settings.showAnimations = showAnimations.isSelected();
	}

	// Ai Wait Time
	public void aiWaitInstant(ActionEvent event) {
		Settings.aiWaitMs = 1;
	}

	public void aiWaitHalfSecond(ActionEvent event) {
		Settings.aiWaitMs = 500;
	}

	public void aiWaitSecond(ActionEvent event) {
		Settings.aiWaitMs = 1000;
	}

	// BoardSize
	public void setSize4(ActionEvent event) {
		Settings.boardSize = 4;
	}

	public void setSize8(ActionEvent event) {
		Settings.boardSize = 8;
	}

	public void setSize12(ActionEvent event) {
		Settings.boardSize = 12;
	}

	public void setSize64(ActionEvent event) {
		Settings.boardSize = 64;
	}
	
	public void setSize128(ActionEvent event) {
		Settings.boardSize = 128;
	}

	// AI Type
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

	// Game Mode
	public void setReversi(ActionEvent event) {
		Settings.gameMode = Constants.GAMEMODE_REVERSI;
		playerNumberChange(2);
	}

	public void setOthello(ActionEvent event) {
		playerNumberChange(2);
		Settings.gameMode = Constants.GAMEMODE_OTHELLO;
	}

	public void setRolit(ActionEvent event) {
		playerNumberChange(4);
		int tempCurrentPlayer = currentPlayer;
		Settings.gameMode = Constants.GAMEMODE_ROLIT;

		for(int i = 0; i < 2; i++){
			for(int j = 2; j < 4; j++){
				if(Settings.playerColors.get(i) == Settings.playerColors.get(j)){
					currentPlayer = j;
					currentColor = indexOf(Settings.playerColors.get(j));
					nextColor();
				}
			}
		}

		currentPlayer = tempCurrentPlayer;
		loadPlayer();
	}

	private void playerNumberChange(int n){
		Settings.nrPlayers = n;
	}

	// Player UI
	private void loadPlayer() {
		playerNameText.setText(Settings.playerNames.get(currentPlayer));
		playerColor.setFill(Settings.playerColors.get(currentPlayer));
		currentColor = indexOf(Settings.playerColors.get(currentPlayer));
		playerNumber.setText("Player: " + (currentPlayer + 1));

		AIModes loadMode = Settings.playerAIModes[currentPlayer];
		humanRadio.setSelected(AIModes.HumanPlayer == loadMode);
		randomRadio.setSelected(AIModes.AIRandom == loadMode);
		greedyRadio.setSelected(AIModes.AIGreedy == loadMode);
		weightedRadio.setSelected(AIModes.AIWeighted == loadMode);
	}

	public void nextPlayer() {
		playerNameChanged();
		currentPlayer++;
		if (currentPlayer > Settings.nrPlayers - 1)
			currentPlayer = 0;
		loadPlayer();
	}

	public void prevPlayer() {
		playerNameChanged();
		currentPlayer--;
		if (currentPlayer < 0)
			currentPlayer = Settings.nrPlayers - 1;
		loadPlayer();
	}

	public void nextColor() {
		currentColor++;
		if (currentColor > possibleColors.length - 1){
			currentColor = 0;
		}

		for (int i = 0; i < Settings.nrPlayers; i++){
			if(i == currentPlayer) continue;
			if(possibleColors[currentColor] == Settings.playerColors.get(i)){
				nextColor();
			}
		}

		playerColor.setFill(possibleColors[currentColor]);
		Settings.playerColors.set(currentPlayer, possibleColors[currentColor]);
	}

	public void prevColor() {
		currentColor--;
		if (currentColor < 0)
			currentColor = possibleColors.length - 1;
		
		for (int i = 0; i <Settings.nrPlayers; i++){
			if(i == currentPlayer) continue;
			if(possibleColors[currentColor] == Settings.playerColors.get(i)){
				prevColor();
			}
		}
		playerColor.setFill(possibleColors[currentColor]);
		Settings.playerColors.set(currentPlayer, possibleColors[currentColor]);
	}

	public void playerNameChanged() {
		if (playerNameText.getText().length() > maxNameLength) {
			Settings.playerNames.set(currentPlayer, playerNameText.getText().substring(0, maxNameLength - 1));
			playerNameText.setText("max Characters: " + maxNameLength);
		}
		Settings.playerNames.set(currentPlayer, playerNameText.getText());
	}

	private int indexOf(Color color) {
		for (int i = 0; i < possibleColors.length; i++) {
			if (color == possibleColors[i])
				return i;
		}
		return -1;
	}
}

class Settings {

	private static final long serialVersionUID = 8390467629592650422L;

	// All settings are initialized to the default 8x8 Reversi
	static int boardSize = 64;
	static int nrPlayers = 2;
	static int gameMode = Constants.GAMEMODE_REVERSI;
	static ArrayList<Color> playerColors = new ArrayList<Color>(Arrays.asList(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN));
	static ArrayList<String> playerNames = new ArrayList<String>(Arrays.asList("Greedy_RED", "Greedy_BLUE", "Greedy_YELLOW", "Greedy_Green"));
	static boolean showMoveHints = false;
	static boolean showAnimations = false;
	static int aiWaitMs = 1;

	static int previousStartingIndex = Constants.UNDEFINED;

	static AIModes[] playerAIModes = new AIModes[] { AIModes.AIGreedy, AIModes.AIGreedy, AIModes.AIGreedy,AIModes.AIGreedy };

	static void setSettings(saveSettings s) {
		boardSize = s.boardSize;
		nrPlayers = s.nrPlayers;
		gameMode = s.gameMode;
		playerColors = s.GenerateColorList();
		playerNames = s.playerNames;
		playerAIModes = s.playerAIModes;
		showMoveHints = s.showMoveHints;
	}

	static ReversiModel createModel(GameView view) {
		switch (Settings.gameMode) {
		case Constants.GAMEMODE_REVERSI -> {
			return new ReversiModel(view);
		}

		case Constants.GAMEMODE_OTHELLO -> {
			return new OthelloModel(view);
		}

		case Constants.GAMEMODE_ROLIT -> {
			return new RolitModel(view);
		}

		}
		return new ReversiModel(view);
	}
}

//There were problems when we saved a static settings objects
class saveSettings implements Serializable {
	private static final long serialVersionUID = 8390467629592650422L;
	int boardSize;
	int nrPlayers;
	int gameMode;
	ArrayList<Double[]> playerColorData;
	ArrayList<String> playerNames;
	boolean showMoveHints;
	AIModes[] playerAIModes;

	public saveSettings() {
		boardSize = Settings.boardSize;
		nrPlayers = Settings.nrPlayers;
		gameMode = Settings.gameMode;
		SaveColor();
		playerNames = Settings.playerNames;
		playerAIModes = Settings.playerAIModes;
		showMoveHints = Settings.showMoveHints;
	}

	void SaveColor() {
		ArrayList<Color> colors = Settings.playerColors;
		playerColorData = new ArrayList<Double[]>();
		for (int i = 0; i < colors.size(); i++) {
			Double[] t = new Double[4];
			t[0] = colors.get(i).getRed();
			t[1] = colors.get(i).getGreen();
			t[2] = colors.get(i).getBlue();
			t[3] = colors.get(i).getOpacity();
			playerColorData.add(t);
		}
	}

	ArrayList<Color> GenerateColorList() {
		ArrayList<Color> colors = new ArrayList<Color>();
		for (int i = 0; i < playerColorData.size(); i++) {
			colors.add(new Color(playerColorData.get(i)[0], playerColorData.get(i)[1], playerColorData.get(i)[2], playerColorData.get(i)[3]));
		}
		return colors;
	}
}