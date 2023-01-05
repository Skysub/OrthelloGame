import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TimerView extends Pane{
    private Text playerOneText = new Text();
    private Text playerTwoText = new Text();
    private static TimerModel model;

    TimerView(int startTime, double spacing, double font) {
        model = new TimerModel(startTime*60000, 1);
        playerOneText.setText(model.getPlayerOneTime()); //TODO det er her hvor tiden først bliver hentet
        playerTwoText.setText(model.getPlayerTwoTime());

        //Places text
        playerOneText.setLayoutY(font);
        playerTwoText.setLayoutY(font);
        playerTwoText.setLayoutX(spacing);
        this.getChildren().add(playerOneText);
        this.getChildren().add(playerTwoText);
        playerOneText.setFont(new Font(font));
        playerTwoText.setFont(new Font(font));
    }

    public static void changeTurn(){
        model.swapTurn();
    }

    public void onUpdate(){ //TODO, hvad der basically skal ske
        this.playerOneText.setText(model.getPlayerOneTime());
        this.playerTwoText.setText(model.getPlayerTwoTime());
    }
}
