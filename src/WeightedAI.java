public class WeightedAI extends GreedyAI {
    
    int[][] weights4 = new int[][] {
        {99, -8, -8, 99},
        {-8,  0,  0, -8},
        {-8,  0,  0, -8},
        {99, -8, -8, 99}
    };

    int[][] weights8 = new int[][]  {
        {99, -8, 8, 6, 6, 8, -8,99},
        {-8,-24,-4,-3,-3,-4,-24,-8},
        { 8, -4, 7, 4, 4, 7, -4, 8},
        { 6, -3, 4, 0, 0, 4, -3, 6},
        { 6, -3, 4, 0, 0, 4, -3, 6},
        { 8, -4, 7, 4, 4, 7, -4, 8},
        {-8,-24,-4,-3,-3,-4,-24,-8},
        {99, -8, 8, 6, 6, 8, -8,99}
    };

    int[][] weights12 = new int[][] {
        { 99, -12,  12,  8,  6,  4,  4,  6,  8, 12,-12, 99},
        {-12, -30,  -8, -6, -4, -2, -2, -4, -6, -8,-30, -12},
        { 12,  -8,  14, 10,  6,  2,  2,  6, 10, 14, -8,  12},
        {  8,  -6,  10,  5,  3,  1,  1,  3,  5, 10, -6,  8},
        {  6,  -4,   6,  3,  2,  0,  0,  2,  3,  6, -3,  6},
        {  4,  -2,   2,  1,  0,  0,  0,  0,  1,  2, -2,  4},
        {  4,  -2,   2,  1,  0,  0,  0,  0,  1,  2, -2,  4},
        {  6,  -4,   6,  3,  2,  0,  0,  2,  2,  6, -4,  6},
        {  8,  -6,  10,  5,  3,  1,  1,  3,  5, 10, -6,  8},
        { 12,  -8,  14, 10,  6,  2,  2,  6, 10, 14, -8,  12},
        {-12, -30,  -8, -6, -4, -2, -2, -4, -6, -8,-30, -12},
        { 99, -12,  12,  8,  6,  4,  4,  6,  8, 12,-12, 99},
    };

    @Override
    int getScoreOfPath(Path chosenPath) {
        int[] coords = chosenPath.coordinates;
        int score = chosenPath.getSizeOfPath();

        if (Settings.boardSize == 4) {
            score += weights4[coords[0]][coords[1]];
        }
        else if (Settings.boardSize == 8) {
            score += weights8[coords[0]][coords[1]];
        }
        else if (Settings.boardSize == 12) {
            score += weights12[coords[0]][coords[1]];
        }

        return score;
    }
}
