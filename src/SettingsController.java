/*
Skrevet af: Frederik Hvarregaard Andersen 
Studienummer: s224801
*/

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SettingsController {

	private SettingsView view;
	private int currentPlayer = 0;
	private int currentColor = 0;
	private int maxNameLength = 32;
	private Color[] possibleColors = new Color[] {
		Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW
	};

	// Player customization UI
	@FXML Circle playerColor;
	@FXML TextField playerNameText;
	@FXML Label playerNumber;
	@FXML RadioButton humanRadio;
	@FXML RadioButton randomRadio;
	@FXML RadioButton greedyRadio;
	@FXML RadioButton weightedRadio;

	// Visual options UI
	@FXML CheckBox showMoveHints;
	@FXML CheckBox showAnimations;

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