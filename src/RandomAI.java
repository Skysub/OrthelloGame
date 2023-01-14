import java.util.ArrayList;

public class RandomAI implements Ai{

    public int[] AIGetSteppingCoords(ArrayList<Path> nonNullPaths){
        int moveIndex = (int) (Math.random() * nonNullPaths.size());
        return nonNullPaths.get(moveIndex).coordinates;
    }
    
}
