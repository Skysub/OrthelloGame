import java.util.ArrayList;

public class RandomAI implements Ai{

    public int[] AIGetSteppingCoords(ArrayList<Path> nonNullPaths){
        if (nonNullPaths.size() == 0) {
            int[] val = {Constants.UNDEFINED, Constants.UNDEFINED};
            return val;
        }
        int moveIndex = (int) (Math.random() * nonNullPaths.size());
        return nonNullPaths.get(moveIndex).coordinates;
    }
}
