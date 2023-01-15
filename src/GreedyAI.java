import java.util.ArrayList;

public class GreedyAI implements Ai{

    int getScoreOfPath(Path chosenPath){
        return chosenPath.getSizeOfPath();
    }

    // As this is a greedy algorithm, we calculate the best scores from the nonNullPaths,
    // we then randomly select the coords of one of these paths.
    public int[] AIGetSteppingCoords(ArrayList<Path> nonNullPaths) {
        ArrayList<Path> bestPaths = new ArrayList<Path>();
        int bestScore = 0;
        Path chosenPath;
        for (Path currentPath : nonNullPaths) {
            int scoreOfCurrentPath = getScoreOfPath(currentPath);
            if (scoreOfCurrentPath > bestScore) {
                bestScore = scoreOfCurrentPath;
                bestPaths.clear();
                bestPaths.add(currentPath);
            } else if (scoreOfCurrentPath == bestScore) bestPaths.add(currentPath);
        }
        chosenPath = bestPaths.get((int) (Math.random() * bestPaths.size()));
        return chosenPath.coordinates;
    }
}
