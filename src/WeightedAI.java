public class WeightedAI extends GreedyAI {

    int[][] weights = new int[][]  {
        {99, -8, 8, 6, 6, 8, -8,99},
        {-8,-24,-4,-3,-3,-4,-24,-8},
        { 8, -4, 7, 4, 4, 7, -4, 8},
        { 6, -3, 4, 0, 0, 4, -3, 6},
        { 6, -3, 4, 0, 0, 4, -3, 6},
        { 8, -4, 7, 4, 4, 7, -4, 8},
        {-8,-24,-4,-3,-3,-4,-24,-8},
        {99, -8, 8, 6, 6, 8, -8,99}
    };

    @Override
    int getScoreOfPath(Path chosenPath) {
        int[] coordsOfPath = chosenPath.coordinates;
        int score = chosenPath.getSizeOfPath() + this.weights[coordsOfPath[0]][coordsOfPath[1]];
        return score;
    }
}
