import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Settings {
    private ArrayList<Player> players;
    private int boardSize;
    private GameType gameType;

    public GameType getGameType() {return gameType;}
    public void setGameType(GameType gameType) {this.gameType = gameType;}
    public ArrayList<Player> getPlayerList() {return this.players;}
    public int getBoardSize(){return this.boardSize;}
    public void setBoardSize(int boardSize){this.boardSize = boardSize;}
    
    public Player getPlayerAt(int i) {return players.get(i);}

    public void setBaseSettings(){
        players = new ArrayList<Player>();
        gameType = GameType.v1;
        players.add(new Player("Player 1", Color.BLACK));
        players.add(new Player("Player 2", Color.WHITE));
        boardSize = 8;
    }
    
    /*
    public int numberOfPlayers(){return players.size();}
    public ArrayList<Color> getPlayerColors(){
        ArrayList<Color> colors = new ArrayList<Color>();
        colors.clear();
        for(int i = 0; i < players.size(); i++){
            colors.add(players.get(i).getColor());
            return colors;
        }
    }
    public ArrayList<String> getPlayerNames(){
        ArrayList<String> names = new ArrayList<String>();
        names.clear();
        for(int i = 0; i < players.size(); i++){
            names.add(players.get(i).getName());
            return names;
        }
    }*/

}

enum GameType{
    v1,
    v4,
    Random,
    Greedy,
    Weight;
}
