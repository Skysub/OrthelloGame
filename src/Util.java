/*
Skrevet af: Mads Christian Wrang Nielsen 
Studienummer: s224784 
*/

public class Util {

    private static final String delimiter = ",";
    
    public static String toId(int row, int col) {
        return row + delimiter + col;
    }

    public static int[] fromId(String id) {
        var split = id.split(delimiter);
        int[] result = {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        return result;
    }
}
