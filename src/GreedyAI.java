import java.util.ArrayList;

public class GreedyAI implements Ai{

    public int startingScore = 0;

    int getScoreOfPath(Path chosenPath){
        return chosenPath.getSizeOfPath();
    }

    // As this is a greedy algorithm, we calculate the best scores from the nonNullPaths,
    // we then randomly select the coords of one of these paths.
    public int[] AIGetSteppingCoords(ArrayList<Path> nonNullPaths) {
        ArrayList<Path> bestPaths = new ArrayList<Path>();
        int bestScore = startingScore;
        Path chosenPath;
        for (Path currentPath : nonNullPaths) {
            int scoreOfCurrentPath = getScoreOfPath(currentPath);
            if (scoreOfCurrentPath > bestScore) {
                bestScore = scoreOfCurrentPath;
                bestPaths.clear();
                bestPaths.add(currentPath);
            } else if (scoreOfCurrentPath == bestScore) bestPaths.add(currentPath);
        }
        if(bestPaths.size() == 0){
            return new int[] {Constants.UNDEFINED,Constants.UNDEFINED};
        }
        chosenPath = bestPaths.get((int) (Math.random() * bestPaths.size()));
        return chosenPath.coordinates;
    }
}
