package ch.hes_so.master.phonerally.Vibrator;


import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

public class VibratorManager {

    Vibrator vibrator;
    float distance;
    float maxDistance; //The vibrator vibrates only if distance <= maxDistance
    float minDistance; //The vibrator will vibrate continuously if distance <= minDistance
    long vibrationMaxDuration;

    boolean isRunning;

    public VibratorManager(Context context)
    {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        maxDistance = 100;
        minDistance = 10;
        distance = maxDistance + 1; //We must wait for a distance update before any vibration
        vibrationMaxDuration = 2000;
        isRunning = false;
    }

    public void setDistance(float distance)
    {
        this.distance = distance;
    }

    public void startVibrationLoop()
    {
        isRunning = true;
        new Thread(this.vibrationLoop).start();
    }

    public void stopVibrationLoop()
    {
        isRunning = false;
    }

    private Runnable vibrationLoop = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                try {

                    if(distance <= maxDistance) {
                        long vibrationDuration = 0; //Duration of the vibration in ms
                        long waitDuration = 0; //Time between vibration in ms

                        if(distance <= minDistance) //Make the vibrator vibrates continuously
                        {
                            vibrationDuration = 2000; // vibrate continuously for 2s...
                            waitDuration = vibrationDuration; // ...and directly after the 2s make the vibrator vibrates again
                        }
                        else
                        {
                            vibrationDuration = (long) ((distance / maxDistance) * vibrationMaxDuration);
                            waitDuration = 2 * vibrationDuration; //We wait until the end of the vibration plus the duration of this vibration
                        }

                        vibrator.vibrate(vibrationDuration);
                        Thread.sleep(waitDuration);
                    }
                    else
                        Thread.sleep(1000); //Wait one second before checking again the distance

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            vibrator.cancel();
        }
    };
}
