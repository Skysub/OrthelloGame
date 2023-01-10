import java.util.ArrayList;

public abstract class Ai {
    Model model;
    Controller controller;
    public ArrayList<PossibleMove> possibleMoves;

    Ai(Controller controller, Model model){
        this.model = model;
        this.controller = controller;
    }

    public void placePiece(){
        
    }

    public void getMoves(){
        this.possibleMoves = model.getPossibleMoves();
        if (possibleMoves.size() == 0) model.pass();
    }
}
