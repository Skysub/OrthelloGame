public class RandomAI extends Ai{

    RandomAI(Model model) {
        super(model);
    }

    public void placePiece(){
        int move = (int) (Math.random() * possibleMoves.size());
        model.tryMove(possibleMoves.get(move).getTile().getRow(),possibleMoves.get(move).getTile().getCol());
    }
    
}
