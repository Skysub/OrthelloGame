import java.util.ArrayList;

public class GreedyAI extends Ai{

    ArrayList<Integer> bestMoves;
    GreedyAI(Model model) {
        super(model);
        bestMoves = new ArrayList<Integer>();
    }

    
    public void placePiece() {
        bestMoves.clear();
        int best = 0;
        int move;
        for(int i = 0; i < possibleMoves.size(); i++){
            if (possibleMoves.get(i).toBeFlipped.size() > best){
                best = possibleMoves.get(i).toBeFlipped.size();
                bestMoves.clear();
                bestMoves.add(i);
            }
            else if(possibleMoves.get(i).toBeFlipped.size() == best) bestMoves.add(i);
        }
        move = bestMoves.get((int) (Math.random() * bestMoves.size()));
        model.tryMove(possibleMoves.get(move).getTile().getRow(),possibleMoves.get(move).getTile().getCol());
    }
    
}
