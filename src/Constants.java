/*
Skrevet af: Benjamin Mirad Gurini
Studienummer: s214590
*/

public class Constants {
    // Checker-states
    public static final int UNDEFINED = -1;
    public static final int EMPTY = -1;

    // Game-states
    public static final int START = 0;
    public static final int PLACEMENT = 1;
    public static final int TURN_SKIPPED = 2;
    public static final int GAME_ENDED = 3;

    // Gamemodes
    public static final int GAMEMODE_REVERSI = 0;
    public static final int GAMEMODE_OTHELLO = 1;
    public static final int GAMEMODE_ROLIT = 2;
}

enum AIModes{
    HumanPlayer,
    AIRandom,
    AIWeighted,
    AIGreedy
}