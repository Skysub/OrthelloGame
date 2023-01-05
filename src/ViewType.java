public enum ViewType {
    Menu(0),
    Settings(1),
    Game(2);

    private int value;

    private ViewType(int value) {
        this.value = value;
    }

    public int getIndex() {
        return value;
    }
}