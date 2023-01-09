import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TimerView extends HBox{
    private static TimerModel model;
    private int turn, turns;
    private Text[] times;

    
    TimerView(int startTime, double font, int players) {
    	turn = 0;
    	turns = players-1;
    	times = new Text[players];
        
    	for (int i = 0; i < times.length; i++) {
            times[i] = new Text();
            times[i].setFont(new Font(font));
            this.getChildren().add(times[i]);
        }
        this.setSpacing(400/players);
        model = new TimerModel(this, startTime*60000, 30, players);//StartTime is in minutes
    }

    public void updateText(String time, int index) {
    	times[index].setText(time);
    }
    
    public void swapTurn() {
    	if(turn == turns) turn = 0;
    	else turn++;
    	model.swapTurn(turn);
    }
    
}
