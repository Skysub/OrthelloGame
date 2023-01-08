import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TimerView extends Text{
    private static TimerModel model;
    private int turn, turns;
    private Text[] times;

    
    TimerView(int startTime, double font, int players) {
    	turn = 0;
    	turns = players-1;
    	times = new Text[players];
    	for (int i = 0; i < times.length; i++) times[i] = new Text();
        model = new TimerModel(this, startTime*60000, 1, players);//StartTime is in minutes
        this.setFont(new Font(font));
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
