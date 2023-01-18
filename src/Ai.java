/*
Skrevet af: Benjamin Mirad Gurini
Studienummer: s214590
*/

import java.util.ArrayList;

// Interface implementet by RandomAI, GreedyAI and WeightedAI
public interface Ai {
     public int[] AIGetSteppingCoords(ArrayList<Path> nonNullPaths);
}
