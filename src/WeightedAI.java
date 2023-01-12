import java.util.ArrayList;

public class WeightedAI extends Ai {
    int[][] weights;
    int count = 0;
    
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
        int weight = -101;
        Tile move = new Tile(0, 0, TileType.Empty);
        getMoves();
        System.out.println("Turn:" + count++);
        for(int i = 0; i < possibleMoves.size(); i++){
            int currentWeigth = calculateWeight(possibleMoves.get(i).getTile(),possibleMoves.get(i).getToBeFlipped());
            System.out.println(currentWeigth);
            if( currentWeigth > weight){
                weight = currentWeigth;
                move = possibleMoves.get(i).getTile();
            }
            else if(currentWeigth == weight){
                if(weights[possibleMoves.get(i).getTile().getRow()][possibleMoves.get(i).getTile().getCol()] > weight){
                    move = possibleMoves.get(i).getTile();
                }
            }
        }
        System.out.println(weight);
        model.tryMove(move.getRow(), move.getCol());
    }

    private int calculateWeight(Tile move, ArrayList<Tile> flips){
        int weight = weights[move.getRow()][move.getCol()];
        for(int i = 0; i < flips.size(); i++){
            weight += weights[flips.get(i).getRow()][flips.get(i).getCol()];
        }
        return weight;
    }

    
}
