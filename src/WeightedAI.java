import java.util.ArrayList;

public class WeightedAI extends Ai {
    int[][] weights;
    int count = 0;
    private Tile move;
    
    WeightedAI(Model model) {
        super(model);
        weights = placeWeights();
    }

    private int[][] placeWeights(){
        return new int[][]  {{99, -8, 8, 6, 6, 8, -8,99}
                            ,{-8,-24,-4,-3,-3,-4,-24,-8}
                            ,{ 8, -4, 7, 4, 4, 7, -4, 8}
                            ,{ 6, -3, 4, 0, 0, 4, -3, 6}
                            ,{ 6, -3, 4, 0, 0, 4, -3, 6}
                            ,{ 8, -4, 7, 4, 4, 7, -4, 8}
                            ,{-8,-24,-4,-3,-3,-4,-24,-8}
                            ,{99, -8, 8, 6, 6, 8, -8,99}};
    }

    @Override
    public void placePiece() {
        move = calculateMove();
        model.tryMove(move.getRow(), move.getCol());
    }

    private Tile calculateMove(){
        int weight = -10000;
        Tile newMove = new Tile(0, 0, TileType.Empty);
        getMoves();
        for(int i = 0; i < possibleMoves.size(); i++){
            int currentWeigth = calculateWeight(possibleMoves.get(i).getTile(),possibleMoves.get(i).getToBeFlipped());
            System.out.println(currentWeigth);
            if( currentWeigth > weight){
                weight = currentWeigth;
                newMove = possibleMoves.get(i).getTile();
            }
            else if(currentWeigth == weight){
                if(weights[possibleMoves.get(i).getTile().getRow()][possibleMoves.get(i).getTile().getCol()] > weight){
                    newMove = possibleMoves.get(i).getTile();
                }
            }
        }
        return newMove;
    }

    protected int calculateWeight(Tile move, ArrayList<Tile> flips){
        int weight = weights[move.getRow()][move.getCol()];
        for(int i = 0; i < flips.size(); i++){
            weight += weights[flips.get(i).getRow()][flips.get(i).getCol()];
        }
        return weight;
    }

    
}
