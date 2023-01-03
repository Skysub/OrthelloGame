public class Model {
    
    private View view;

    private int boardSize = 8;

    public Model(View view) {
        this.view = view;
    }

    public int getBoardSize() {
        return boardSize;
    }
}
