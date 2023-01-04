public class Model {
    
    private App view;

    private int boardSize = 8;

    public Model(App view) {
        this.view = view;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        //TODO Validate?
        this.boardSize = boardSize;
    }
}
