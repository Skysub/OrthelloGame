import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsController {

	private SettingsView view;

	@FXML
	TextField customSizeText;
	@FXML
	RadioButton customSizeRadioButton;
	@FXML
	CheckBox showMoveHints;

	public void setModelAndView(ReversiModel model, SettingsView view) {
		this.view = view;
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
		} catch (NumberFormatException e) {
			customSizeText.clear();
			customSizeText.setPromptText("Not an integer");
			customSizeText.getParent().requestFocus();
			return false;
		}

		// TODO Validate whether the value makes sense. Set a range eg. 4-26
		Settings.boardSize = newBoardSize;
		return true;
	}

	// GameType Buttons
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
		Settings.gameMode = Constants.GAMEMODE_REVERSI;
	}

	public void setOthello(ActionEvent event) {
		Settings.gameMode = Constants.GAMEMODE_ORTHELLO;
	}

	public void setRolit(ActionEvent event) {
		Settings.gameMode = Constants.GAMEMODE_ROLIT;
	}

	public void onMoveHintsChanged(ActionEvent event) {
		Settings.showMoveHints = showMoveHints.isSelected();
	} 
}

class Settings implements Serializable {

	private static final long serialVersionUID = 8390467629592650422L;

	// All settings are initialized to the default 8x8 Reversi
	static int boardSize = 8;
	static int nrPlayers = 2;
	static int gameMode = Constants.GAMEMODE_REVERSI;
	static ArrayList<Color> playerColors = new ArrayList<Color>(Arrays.asList(Color.WHITE, Color.BLACK));
	static ArrayList<String> playerNames = new ArrayList<String>(Arrays.asList("WHITE", "BLACK"));
	static boolean showMoveHints = true;

	static AIModes[] playerAIModes = new AIModes[] { AIModes.HumanPlayer, AIModes.HumanPlayer, AIModes.HumanPlayer,
			AIModes.HumanPlayer };

	static void setSettings(Settings s) {
		boardSize = s.boardSize;
		nrPlayers = s.nrPlayers;
		gameMode = s.gameMode;
		playerColors = s.playerColors;
		playerNames = s.playerNames;
		playerAIModes = s.playerAIModes;
		showMoveHints = s.showMoveHints;
	}

	// TODO: Tilføj gametype
	// TODO AI
}