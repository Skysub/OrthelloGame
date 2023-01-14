import java.util.ArrayList;

/*
public class FutureRoundsWeightedAI extends WeightedAI{

    private int roundsCalced;

    FutureRoundsWeightedAI(ReversiModel model, int difficulty) {
        super(model);
        roundsCalced = difficulty;
        
    }
    
    @Override
    public void placePiece(){
        Tile move = calculateMove();
        model.tryMove(move.getRow(), move.getCol());
    }


    private Tile calculateMove(){
        getMoves();
        if(model.getGameState() == ReversiModel.GameState.Start){ //Random start positions
            return possibleMoves.get((int) (Math.random()*possibleMoves.size())).getTile();
        }

        Tile newMove = new Tile(0, 0, TileType.Empty);
        int weight = 0;
        
        for(int i = 0; i < possibleMoves.size(); i++){
            int currentWeigth = calculateWeight(possibleMoves.get(i).getTile(),possibleMoves.get(i).getToBeFlipped());
            currentWeigth -= calculateWeigthsAhead(possibleMoves.get(i), 0);
            if(i == 0) {
                weight = currentWeigth;
                newMove = possibleMoves.get(i).getTile();
            }

            if(currentWeigth > weight){
                weight = currentWeigth;
                newMove = possibleMoves.get(i).getTile();
            }
        }

        System.out.println(weight);
        return newMove;
    }


    private int calculateWeigthsAhead(PossibleMove move, int calcsDone){

        if(calcsDone < roundsCalced){ //checks if limit is reached

            ReversiModel calcModel = new ReversiModel(null); //New model to do calculations on
            setupCalcModel(move.getTile(), calcModel); //Sets up new model for calculations
            ArrayList<PossibleMove> nextMoves = calcModel.getPossibleMoves();
            int bestWeight = -1000;
            

            
            if(nextMoves.size() == 0) return 1000;
            for(int i = 0; i < nextMoves.size(); i++){
                int nextWeight = calculateWeight(nextMoves.get(i).getTile(), nextMoves.get(i).getToBeFlipped());
                if(calcsDone%2 == 1){
                    nextWeight += calculateWeigthsAhead(nextMoves.get(i), 1+calcsDone);
                }
                else nextWeight -= calculateWeigthsAhead(nextMoves.get(i), 1+calcsDone);
                if(nextWeight > bestWeight){
                    bestWeight = nextWeight;
                }
            }
        }
        return 0;
    }

    private void setupCalcModel(Tile move, ReversiModel calcModel){
        calcModel.setBoard(model.getBoard());
        calcModel.setNoOfMoves(model.getNoOfMoves());
        calcModel.AIMove(move);
    }
}
*/