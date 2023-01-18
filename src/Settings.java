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
