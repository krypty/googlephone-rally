package ch.hes_so.master.phonerally.game;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class GameService extends Service implements LocationListener {
    private static final String TAG = GameService.class.getSimpleName();

    // Binder given to clients
    private Binder binder = new Binder();

    private IGameService callback;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "service created");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted by the user, service is shutting down");
            return;
        }

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        startGameLoop();
    }

    private void startGameLoop() {
        this.isRunning = true;
        new Thread(this.gameLoop).start();
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    String msg = "I'm alive !";
                    fireNewVector(msg);

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "new location: " + location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + provider + ", status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);
    }


    // ==== Activity communication
    public class Binder extends android.os.Binder {
        GameService getService() {
            return GameService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    public interface IGameService {
        void onNextVectorComputed(String msg);
    }

    private void fireNewVector(String msg) {
        if (this.callback == null)
            return;

        this.callback.onNextVectorComputed(msg);
    }

    public void register(IGameService callback) {
        this.callback = callback;
    }

    public void unregister(IGameService callback) {
        if (this.callback == callback)
            this.callback = null;
        else
            Log.w(TAG, "no previous callback was registered");
    }
}
