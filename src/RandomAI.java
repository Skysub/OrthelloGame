public class RandomAI extends Ai{

    RandomAI(Controller controller, Model model) {
        super(controller, model);
    }

    public void placePiece(){
        int move = (int) (Math.random() * possibleMoves.size());
        System.out.println(move);
        model.tryMove(possibleMoves.get(move).getTile().getRow(),possibleMoves.get(move).getTile().getCol());
    }
    
}
