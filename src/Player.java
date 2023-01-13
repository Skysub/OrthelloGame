import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

class Turn{

    int[] coordinates;
    int timeTaken;
    int gameStateAtTurnTaken = Constants.UNDEFINED;

    Turn(int[] coordinates,int gameStateAtTurnTaken){
        this.coordinates = coordinates;
        this.gameStateAtTurnTaken = gameStateAtTurnTaken;
    }
}

class Player{
    private String playerName;
    private int score;
    private ArrayList<Turn> turnHistory = new ArrayList<Turn>();
    private Color playerColor;
    private int nrCheckers;
    private boolean isPlayer;


    Player(String name,Color playerColor){
        this.playerName = name;
        this.playerColor = playerColor;
    }

    void recordTurn(Turn newTurn){
        turnHistory.add(newTurn);
    }

    void decreaseNumberOfCheckers(){
        this.nrCheckers -= 1;
    }

    void increaseNumberOfCheckers(){
        this.nrCheckers += 1;
    }

    Color getPlayerColor(){
        return this.playerColor;
    }

    int getScore(){
        return this.score;
    }

    int getNrCheckers(){ return this.nrCheckers;}

    String getPlayerName(){ return this.playerName;}

    //Calculates and updates the score
    int calculateScore(){
        this.score = this.getNrCheckers();
        return this.score;
    }

}

class PlayerManager {
    ArrayList<Player> playersArray = new ArrayList<Player>();
    int highScore = Constants.UNDEFINED;
    int currentPlayerIndex;
    int nrPlayers;
    int nrTurnsTaken;

    PlayerManager(int nrPlayers, ArrayList<javafx.scene.paint.Color> playerColors, ArrayList<String> playerNames){
        this.nrPlayers = nrPlayers;
        for(int i = 0; i<nrPlayers; i++){
            Color currentColor = playerColors.get(i);
            String currentName = playerNames.get(i);
            Player newPlayer = new Player(currentName,currentColor);
            this.addPlayer(newPlayer);
        }

        Random randomObject = new Random();
        this.currentPlayerIndex = randomObject.nextInt(nrPlayers);

    }
    void addPlayer(Player newPlayer){
        playersArray.add(newPlayer);
    }

    /*
    Sets the class variable highScore and gets an arrayList, where:
    For all elements in the calculated arrayList, there exists no element in the playersArray, that has a higher score.
    For all elements in the calculated arrayList, all elements in the playersArray have the same or lower score.
     */
    ArrayList<Player> setHighScoreAndGetHighestScoringPlayers(){
        ArrayList<Player> highestScoringPlayersArray = new ArrayList<Player>();

        for (Player currentPlayer : this.playersArray) {

            currentPlayer.calculateScore();
            int currentScore = currentPlayer.getScore();

            //Hvis playeren har samme score som highscoren tilføjer vi det til arrayet
            if (currentScore == this.highScore) {
                highestScoringPlayersArray.add(currentPlayer);


            }

			/*
				Hvis denne playeren har højere score end den nuværende highscore, betyder det, at alle andre i arrayet har lavere score end denne player,
				og vi genstarter derfor arrayet.
				 */
            else if (currentScore > this.highScore) {
                highestScoringPlayersArray = new ArrayList<Player>();
                highestScoringPlayersArray.add(currentPlayer);
                this.highScore = currentPlayer.getScore();
            }

        }
        return highestScoringPlayersArray;
    }

    Player getPlayerAtIndex(int i){
        return this.playersArray.get(i);
    }

    ArrayList<Player> getPlayersArray(){ return this.playersArray;
    }







}