public class Model {
    
    private App app;

    private int boardSize = 8;

    public Model(App app) {
        this.app = app;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        //TODO Validate?
        this.boardSize = boardSize;
    }
}
