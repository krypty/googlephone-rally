package ch.hes_so.master.phonerally.game;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

import ch.hes_so.glassrallylibs.bluetooth.BluetoothService;
import ch.hes_so.glassrallylibs.command.Command;
import ch.hes_so.glassrallylibs.command.CommandFactory;
import ch.hes_so.master.phonerally.geolocation.LocationUtils;
import ch.hes_so.master.phonerally.level.Checkpoint;
import ch.hes_so.master.phonerally.level.Level;
import ch.hes_so.master.phonerally.level.LevelLoader;

public class GameService extends Service implements LocationListener, BluetoothService.IBluetoothService {
    private static final String TAG = GameService.class.getSimpleName();
    public static final int POLLING_RATE = 5000;

    // Binder given to clients
    private Binder gameBinder = new Binder();

    private IGameService callback;
    private boolean isRunning = false;
    private Location currentLocation;
    private Location targetLocation;
    private LocationManager locationManager;
    private String levelName;
    private int currentChkpt = 0;

    private BluetoothService btService;
    private boolean bounded;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "service created");

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted by the user, service is shutting down");
            return;
        }

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        setupBluetoothService();

        startGameLoop();
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
        Log.d(TAG, "onDestroy");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    // ==== BLUETOOTH

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            btService = ((BluetoothService.BluetoothBinder) service).getService();
            btService.register(GameService.this);
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            if (btService != null)
                btService.unregister(GameService.this);
            bounded = false;
        }
    };

    private void setupBluetoothService() {
        Log.d(TAG, "setupBluetoothService()");

        Intent intent = new Intent(getApplicationContext(), BluetoothService.class);
        bindService(intent, serviceConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onCommandReceived(Command cmd) {
        Log.d(TAG, "command received");
    }

    @Override
    public void bluetoothStateChanged(int state) {
        // nothing
    }

    @Override
    public void deviceNameChanged(String device) {
        // nothing
    }


    // ==== GAME LOOP
    private void startGameLoop() {
        this.isRunning = true;
        new Thread(this.gameLoop).start();
    }

    private Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    // 1. Load level
                    new LevelLoader(getApplicationContext(), levelName) {
                        @Override
                        protected void onPostExecute(Level level) {
                            // GPS is not fixed yet
                            if (currentLocation == null) {
                                return;
                            }

                            // 2. Check if last checkpoint has been reached
                            List<Checkpoint> checkpoints = level.getCheckpoints();

                            if (checkpoints.size() < currentChkpt) {
                                triggerVictory();
                                isRunning = false;
                                return;
                            }

                            // 3. Load next checkpoint
                            Checkpoint chkpt = checkpoints.get(currentChkpt);

                            // 4. Send current and target position
                            targetLocation = new Location("next checkpoint");
                            targetLocation.setLongitude(chkpt.getLongitude());
                            targetLocation.setLatitude(chkpt.getLatitude());

                            float distance = currentLocation.distanceTo(targetLocation);
                            Log.d(TAG, "distance: " + distance);

                            // notify GUI with new distance
                            triggerDistance(distance);


                            // 5. Send reward or vector
                            if (distance < chkpt.getRange()) {
                                currentChkpt = (currentChkpt + 1) % checkpoints.size();
                                triggerReward(chkpt);
                            } else {
                                // send the current and target location to the Glass
                                sendVector(currentLocation, targetLocation);
                            }

                        }
                    }.execute();

                    // wait between each iteration
                    Thread.sleep(POLLING_RATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    };

    private void triggerDistance(float distance) {
        sendDistance(distance);
        fireNewDistance(distance);
    }

    private void triggerReward(Checkpoint chkpt) {
        sendReward(chkpt.getContent());
        fireReward(chkpt);
    }

    private void triggerVictory() {
        sendVictory();
        fireVictory();
    }

    private void sendReward(String reward) {
        if (!btService.isConnected()) {
            Log.e(TAG, "bluetooth device not connected !");
            return;
        }
        Command cmd = CommandFactory.createRewardCommand(reward);
        btService.sendCommand(cmd);
    }

    private void sendVictory() {
        if (!btService.isConnected()) {
            Log.e(TAG, "bluetooth device not connected !");
            return;
        }
        Command cmd = CommandFactory.createVictoryCommand();
        btService.sendCommand(cmd);
    }

    private void sendVector(Location currentLocation, Location targetLocation) {
        if (!btService.isConnected()) {
            Log.e(TAG, "bluetooth device not connected !");
            return;
        }

        Command cmd = CommandFactory.createVectorCommand(currentLocation, targetLocation);
        btService.sendCommand(cmd);
    }

    private void sendDistance(float distance) {
        if (!btService.isConnected()) {
            Log.e(TAG, "bluetooth device not connected !");
            return;
        }

        Command cmd = CommandFactory.createDistanceCommand(distance);
        btService.sendCommand(cmd);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "new location: " + location.toString());

        if (LocationUtils.isBetterLocation(location, currentLocation)) {
            currentLocation = location;
        }
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
        String lvl = intent.getStringExtra(GameActivity.LEVEL_TO_LOAD_KEY);
        if (lvl != null) {
            this.levelName = lvl;
        }
        return this.gameBinder;
    }

    public interface IGameService {
        void onDistanceChanged(float distance);

        void onVictory();

        void onCheckpointReached(Checkpoint chkpt);
    }

    private void fireNewDistance(float distance) {
        if (this.callback == null)
            return;

        this.callback.onDistanceChanged(distance);
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

    private void fireVictory() {
        if (this.callback == null)
            return;

        this.callback.onVictory();
    }

    private void fireReward(Checkpoint chkpt) {
        if (this.callback == null)
            return;

        this.callback.onCheckpointReached(chkpt);
    }
}
