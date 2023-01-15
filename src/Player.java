import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

class Turn{

    int[] coordinates;
    int timeTaken; //TODO Unit?
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
    private Ai AIObject;

    Player(String name,Color playerColor,AIModes aiMode){
        this.playerName = name;
        this.playerColor = playerColor;
        this.decideAIMode(aiMode);

        if(isAI()){
            this.playerName += " (AI)";
        }
    }

    // Public Getters
    Color getPlayerColor(){return this.playerColor;}
    int getScore(){return this.score;}
    int getNrCheckers(){ return this.nrCheckers;}
    String getPlayerName(){ return this.playerName;}

    int[] getAICalculatedCoords(ArrayList<Path> nonNullPaths){
        return this.AIObject.AIGetSteppingCoords(nonNullPaths);
    }

    void decideAIMode(AIModes aiMode){
        switch (aiMode){
            case HumanPlayer -> {
                break;
            }
            case AIGreedy -> {
                this.AIObject = new GreedyAI();
                break;
            }
            case AIRandom -> {
                this.AIObject = new RandomAI();
                break;
            }
            case AIWeighted -> {
                this.AIObject = new WeightedAI();
                break;
            }
        }
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

    //Calculates and updates the score
    int calculateScore(){
        this.score = this.getNrCheckers();
        return this.score;
    }

    boolean isAI(){
        return Objects.nonNull(this.AIObject);
    }
}

class PlayerManager {
    ArrayList<Player> players = new ArrayList<Player>();
    int highScore = Constants.UNDEFINED;
    int currentPlayerIndex;
    int nrPlayers;
    int nrTurnsTaken;

    PlayerManager(int nrPlayers, ArrayList<javafx.scene.paint.Color> playerColors, ArrayList<String> playerNames,AIModes[] playerAIModes){
        this.nrPlayers = nrPlayers;
        for(int i = 0; i<nrPlayers; i++){
            Color currentColor = playerColors.get(i);
            String currentName = playerNames.get(i);
            AIModes currentAIMode = playerAIModes[i];
            Player newPlayer = new Player(currentName,currentColor,currentAIMode);
            this.addPlayer(newPlayer);
        }

        Random randomObject = new Random();
        this.currentPlayerIndex = randomObject.nextInt(nrPlayers);

    }
    void addPlayer(Player newPlayer){
        players.add(newPlayer);
    }

    /*
    Sets the class variable highScore and gets an arrayList, where:
    For all elements in the calculated arrayList, there exists no element in the playersArray, that has a higher score.
    For all elements in the calculated arrayList, all elements in the playersArray have the same or lower score.
     */
    ArrayList<Player> setHighScoreAndGetHighestScoringPlayers(){
        ArrayList<Player> highestScoringPlayersArray = new ArrayList<Player>();

        for (Player currentPlayer : this.players) {

            currentPlayer.calculateScore();
            int currentScore = currentPlayer.getScore();

            //Hvis playeren har samme score som highscoren tilføjer vi det til arrayet
            if (currentScore == this.highScore) {
                highestScoringPlayersArray.add(currentPlayer);
            }
			//Hvis denne playeren har højere score end den nuværende highscore, betyder det, at alle andre i arrayet har lavere score end denne player,
			//og vi genstarter derfor arrayet.
            else if (currentScore > this.highScore) {
                highestScoringPlayersArray = new ArrayList<Player>();
                highestScoringPlayersArray.add(currentPlayer);
                this.highScore = currentPlayer.getScore();
            }
        }
        return highestScoringPlayersArray;
    }

    Player getPlayerAtIndex(int i){
        return players.get(i);
    }

    ArrayList<Player> getPlayers(){
        return players;
    }
}