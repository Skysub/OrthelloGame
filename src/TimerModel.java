import java.util.Timer;
import java.util.TimerTask;

public class TimerModel {
    private Timer timer;
    private TimerView view;
    private int updateTime, turn;
    public int[] times;

    private TimerTask action = new TimerTask() {
        @Override
        public void run() {
            times[turn] -= updateTime;
            view.updateText(formatTime(times[turn]),turn);
        }
    };

    TimerModel(TimerView viewer, int startTime, int updateTime, int players){
        view = viewer;
        view = viewer;
        turn = 0;
        times = new int[players];
        for(int i = 0; i < times.length; i++) {
        	times[i] = startTime;
            view.updateText(formatTime(times[i]),i);
        }
        this.updateTime = updateTime;
        timer = new Timer();
        timer.schedule(action, 0, updateTime);
    }

    public void swapTurn(int index){ //swaps which timer to update
        turn = index;
    }

    private String formatTime(int time){
        String formatted;
        int minutes, seconds, milliSeconds;

        minutes = time/(60000); //Converts milliseconds to minutes
        seconds = time/1000%60; //Converts to seconds
        milliSeconds = time%1000; //Converts to millseconds
       
        formatted = "" + minutes + ":";
        //Formats seconds to always be "00"
        if(seconds < 10) formatted += "0" + seconds + ":";
        else formatted += "" + seconds + ":";
        
        //Formats milliseconds to always be "000"
        if(milliSeconds < 10) formatted += "00" + milliSeconds;
        else if(milliSeconds < 100) formatted += "0" + milliSeconds;
        else formatted += "" + milliSeconds;
        return formatted;
    }
    
    
}
