import java.util.ArrayList;
import javafx.scene.paint.Color;

public class Player {
    
    String name;
    Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {return name;}
    public Color getColor() {return color;}
    
    public static ArrayList<Player> getDefaultPlayers(GameType type) {
        ArrayList<Player> players = new ArrayList<Player>();
        switch (type) {
            case Reversi:
            case Othello:
                players.add(new Player("Black", Color.BLACK));
                players.add(new Player("White", Color.WHITE));
                return players;
            case Rolit:
                players.add(new Player("Red", Color.RED));
                players.add(new Player("Blue", Color.BLUE));
                players.add(new Player("Green", Color.GREEN));
                players.add(new Player("Yellow", Color.YELLOW));
                return players;
            default:
                return null;
        }
    }
}
