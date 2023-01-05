import java.util.Timer;
import java.util.TimerTask;

public class TimerModel {
    private Timer timer;
    private int playerOneTime, playerTwoTime, updateTime;
    private boolean turn =true;

    private TimerTask action = new TimerTask() { //TODO Opdaterer hver spillers tid, det virker som det skal som kan ses i konsollen
        @Override
        public void run() {
            if(turn){ 
                playerOneTime -= updateTime;
                System.out.println(formatTime(playerOneTime));
            } else{
                playerTwoTime -= updateTime;
            }
        }
    };

    TimerModel(int startTime, int updateTime){
        playerOneTime = playerTwoTime = startTime;
        this.updateTime = updateTime;
        timer = new Timer();
        timer.schedule(action, 0, updateTime);
    }

    public void swapTurn(){ //swaps which timer to update
        turn = !turn;
    }

    public String getPlayerOneTime(){ //TODO getter metoderne der bliver brugt til at give informationen videre
        return formatTime(playerOneTime);
    }

    public String getPlayerTwoTime(){
        return formatTime(playerTwoTime);
    }

    private String formatTime(int time){
        String formatted;
        int minutes, seconds, milliSeconds;

        minutes = time/(60000); //Converts milliseconds to minutes
        seconds = time/1000%60; //Converts to seconds
        milliSeconds = time%1000; //Converts to millseconds

        // Formats seconds to always be 2-digits
        if(seconds < 10) formatted = "0" + seconds;
        else formatted = "" + seconds;
       
        formatted = "" + minutes + ":" + formatted + ":" + milliSeconds;
        return formatted;
    }
}
