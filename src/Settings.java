/*
Skrevet af: Frederik Cayré Hede-Andersen
Studienummer: s224807
*/

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.paint.Color;

class Settings {
	static int boardSize = 8;
	static int nrPlayers = 2;
	static ArrayList<Color> playerColors = new ArrayList<Color>(Arrays.asList(Color.BLACK, Color.WHITE));
	static ArrayList<String> playerNames = new ArrayList<String>(Arrays.asList("BLACK", "WHITE"));
	static int previousStartingIndex = Constants.UNDEFINED;
}