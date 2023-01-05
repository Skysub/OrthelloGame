import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TimerView extends Pane{
    private Text playerOneText = new Text();
    private Text playerTwoText = new Text();
    private static TimerControl controller;

    TimerView(int startTime, double spacing, double font) {
        controller = new TimerControl(startTime*60000, 1);
        playerOneText.setText(controller.getPlayerOneTime());
        playerTwoText.setText(controller.getPlayerTwoTime());
        playerOneText.setLayoutY(font);
        playerTwoText.setLayoutY(font);
        playerTwoText.setLayoutX(spacing);
        this.getChildren().add(playerOneText);
        this.getChildren().add(playerTwoText);
        playerOneText.setFont(new Font(font));
        playerTwoText.setFont(new Font(font));
    }

    public static void changeTurn(){
        controller.swapTurn();
    }

    public void onUpdate(){
        this.playerOneText.setText(controller.getPlayerOneTime());
        this.playerTwoText.setText(controller.getPlayerTwoTime());
    }
}
