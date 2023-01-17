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
	private Color[] possibleColors = new Color[] { Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };

	@FXML Circle playerColor;
	@FXML TextField playerNameText;
	@FXML Label playerNumber;

	@FXML RadioButton humanRadio;
	@FXML RadioButton randomRadio;
	@FXML RadioButton greedyRadio;
	@FXML RadioButton weightedRadio;
	
	@FXML CheckBox showMoveHints;
	@FXML CheckBox showAnimations;

	public void setModelAndView(ReversiModel model, SettingsView view) {
		this.view = view;
		loadPlayer();
	}

	public void onMoveHintsChanged(ActionEvent event) {
		Settings.showMoveHints = showMoveHints.isSelected();
	}

	public void onShowAnimationsChanged(ActionEvent event) {
		Settings.showAnimations = showAnimations.isSelected();
	}

	public void back(ActionEvent event) {
		view.toMenu();
	}

	public void setSize4(ActionEvent event) {
		Settings.boardSize = 4;
	}

	public void setSize8(ActionEvent event) {
		Settings.boardSize = 8;
	}
	
	public void setSize12(ActionEvent event) {
		Settings.boardSize = 12;
	}

	// GameType Buttons
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
		currentPlayer++;
		if (currentPlayer > Settings.nrPlayers - 1)
			currentPlayer = 0;
		loadPlayer();
	}

	public void prevPlayer() {
		currentPlayer--;
		if (currentPlayer < 0)
			currentPlayer = Settings.nrPlayers - 1;
		loadPlayer();
	}

	public void nextColor() {
		currentColor++;
		if (currentColor > possibleColors.length - 1)
			currentColor = 0;
		playerColor.setFill(possibleColors[currentColor]);
		Settings.playerColors.set(currentPlayer, possibleColors[currentColor]);
	}

	public void prevColor() {
		currentColor--;
		if (currentColor < 0)
			currentColor = possibleColors.length - 1;
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
	static int boardSize = 8;
	static int nrPlayers = 2;
	static int gameMode = Constants.GAMEMODE_REVERSI;
	static ArrayList<Color> playerColors = new ArrayList<Color>(Arrays.asList(Color.WHITE, Color.BLACK, Color.RED, Color.GREEN));
	static ArrayList<String> playerNames = new ArrayList<String>(Arrays.asList("WHITE", "BLACK", "RED", "GREEN"));
	static boolean showMoveHints = true;
	static boolean showAnimations = true; //TODO Should this be added to saveSettings?

	static AIModes[] playerAIModes = new AIModes[] { AIModes.AIGreedy, AIModes.HumanPlayer, AIModes.HumanPlayer,
			AIModes.HumanPlayer };

	static void setSettings(saveSettings s) {
		boardSize = s.boardSize;
		nrPlayers = s.nrPlayers;
		gameMode = s.gameMode;
		//TODO Why is this commented out?
		//playerColors = s.playerColors;
		playerNames = s.playerNames;
		playerAIModes = s.playerAIModes;
		showMoveHints = s.showMoveHints;
	}

	static ReversiModel createModel(GameView view){
		switch (Settings.gameMode){
			case Constants.GAMEMODE_REVERSI -> {
				return new ReversiModel(view);
			}

			case Constants.GAMEMODE_OTHELLO -> {
				return new OthelloModel(view);
			}

			case Constants.GAMEMODE_ROLIT -> {
				System.out.println("Haven't implemented ROLIT yet");
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
	transient ArrayList<Color> playerColors; //Color er ikke serializable, og vi bruger istedet for, default farver
	ArrayList<String> playerNames;
	boolean showMoveHints;
	AIModes[] playerAIModes;

	public saveSettings() {
		boardSize = Settings.boardSize;
		nrPlayers = Settings.nrPlayers;
		gameMode = Settings.gameMode;
		//playerColors = Settings.playerColors;
		playerNames = Settings.playerNames;
		playerAIModes = Settings.playerAIModes;
		showMoveHints = Settings.showMoveHints;
	}
}