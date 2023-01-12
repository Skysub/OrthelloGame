import java.util.ArrayList;

public abstract class Ai {
    Model model;
    public ArrayList<PossibleMove> possibleMoves;

    Ai(Model model){
        this.model = model;
    }

    public abstract void placePiece();

    public void getMoves(){
        this.possibleMoves = model.getPossibleMoves();
        if (possibleMoves.size() == 0) model.pass();
    }
}
