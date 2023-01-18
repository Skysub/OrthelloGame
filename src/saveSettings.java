/*
Skrevet af: Frederik Cayré Hede-Andersen
Studienummer: s224807
*/
import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.paint.Color;

class saveSettings implements Serializable {
	private static final long serialVersionUID = 8390467629592650422L;
	int boardSize;
	int nrPlayers;
	int gameMode;
	int previousStartingIndex;
	ArrayList<Double[]> playerColorData;
	ArrayList<String> playerNames;
	boolean showMoveHints;
	AIModes[] playerAIModes;

	public saveSettings() {
		boardSize = Settings.boardSize;
		nrPlayers = Settings.nrPlayers;
		gameMode = Settings.gameMode;
		previousStartingIndex = Settings.previousStartingIndex;
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
