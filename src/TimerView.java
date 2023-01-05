import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

        //Makes sure timer is updated
        setTimeline(); //TODO den her metode fikser det, kan ses længere nede
    }

    public static void changeTurn(){
        model.swapTurn();
    }

    public void onUpdate(ActionEvent event){ //TODO, hvad der basically skal ske
        this.playerOneText.setText(model.getPlayerOneTime());
        this.playerTwoText.setText(model.getPlayerTwoTime());
    }

    public void setTimeline(){ 
        KeyFrame keyFrame = new KeyFrame(new Duration(1), new EventHandler<ActionEvent>() { //Runs the "handle event" (onUpdate) every Duration(1)
            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                onUpdate(event);
            }
        });
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
