/*
Skrevet af: Frederik Cayré Hede-Andersen
Studienummer: s224807
*/

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.paint.Color;

public class Settings {
	// All settings are initialized to the default 8x8 Reversi, Human vs Human.
	static int boardSize = 8;
	static int nrPlayers = 2;
	static int gameMode = Constants.GAMEMODE_REVERSI;

	//Player settings
	static ArrayList<Color> playerColors = new ArrayList<Color>(Arrays.asList(Color.BLACK, Color.WHITE, Color.RED, Color.BLUE));
	static ArrayList<String> playerNames = new ArrayList<String>(Arrays.asList("Black", "White", "Red", "Blue"));
	static AIModes[] playerAIModes = new AIModes[] {
		AIModes.HumanPlayer, AIModes.HumanPlayer, AIModes.HumanPlayer, AIModes.HumanPlayer
	};

	// Visual settings
	static boolean showMoveHints = true;
	static boolean showAnimations = true;
	static int aiWaitMs = 1000;

	// Used to save who started the previous game, to flip who starts the next game in Reversi
	static int previousStartingIndex = Constants.UNDEFINED;

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
