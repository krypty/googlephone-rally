package ch.hes_so.master.phonerally;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;

import ch.hes_so.glassrallylibs.bluetooth.BluetoothService;
import ch.hes_so.glassrallylibs.bluetooth.BluetoothThread;
import ch.hes_so.glassrallylibs.command.Command;
import ch.hes_so.glassrallylibs.command.CommandFactory;
import ch.hes_so.master.phonerally.bluetooth.DeviceListActivity;
import ch.hes_so.master.phonerally.select_levels.SelectLevelActivity;

public class MainActivity extends Activity implements BluetoothService.IBluetoothService {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Name of the connected device
     */
    private String deviceName = null;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Bluetooth Service
     */
    private BluetoothService btService = null;
    private boolean bounded;

    //Location permissions
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 666;
    private ArrayList<String> locationPermissions;

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 100;

    private Button mStartGameButton;
    private TextView mBluetoothStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.locationPermissions = new ArrayList<>();
        askLocationPermissions();

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        mStartGameButton = (Button) findViewById(R.id.btnStartGame);
        mStartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 31.03.16 uncomment this
//                if (mChatService.getState() != BluetoothThread.STATE_CONNECTED) {
//                    Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
//                    return;
//                }

                // start game activity
                Intent intent = new Intent(MainActivity.this, SelectLevelActivity.class);
                startActivity(intent);
            }
        });

        mBluetoothStatusView = (TextView) findViewById(R.id.tvBluetoothStatus);
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupBluetoothService() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (btService == null) {
            setupBluetoothService();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (this.bounded) {
            unbindService(serviceConn);
            this.bounded = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (btService == null) {
            setupBluetoothService();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
        }
        return false;
    }

    // ==== BLUETOOTH

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            btService = ((BluetoothService.BluetoothBinder) service).getService();
            btService.register(MainActivity.this);
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            if (btService != null)
                btService.unregister(MainActivity.this);
            bounded = false;
        }
    };

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupBluetoothService() {
        Log.d(TAG, "setupBluetoothService()");

        Intent intent = new Intent(getApplicationContext(), BluetoothService.class);
        bindService(intent, serviceConn, BIND_AUTO_CREATE);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        String status = getString(resId);
        setStatus(status);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
        mBluetoothStatusView.setText(subTitle);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE: {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            }
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupBluetoothService();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
            case REQUEST_CHECK_SETTINGS: {
                switch (resultCode) {
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        Toast.makeText(getApplicationContext(), getString(R.string.must_enable_location), Toast.LENGTH_LONG).show();
                        finish();
                        break;
                }
                break;
            }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        btService.connect(device);
    }

    @Override
    public void onCommandReceived(Command cmd) {
        String msg = cmd.getName() + ", " + cmd.getParameter();
        Toast.makeText(getApplicationContext(), deviceName + ":  " + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void bluetoothStateChanged(int state) {
        switch (state) {
            case BluetoothThread.STATE_CONNECTED:
                setStatus(getString(R.string.title_connected_to, deviceName));
                Command cmd = CommandFactory.createDebugCommand("yolo !!!");
                btService.sendCommand(cmd);
                break;
            case BluetoothThread.STATE_CONNECTING:
                setStatus(R.string.title_connecting);
                break;
            case BluetoothThread.STATE_LISTEN:
            case BluetoothThread.STATE_NONE:
                setStatus(R.string.title_not_connected);
                break;
        }
    }

    @Override
    public void deviceNameChanged(String device) {
        this.deviceName = device;
    }

    // ==== LOCATION
    private void askLocationPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // older android version grant permissions automatically (no popups) --> nothing to do
            return;
        }

        int hasLocationFinePermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        int hasLocationCoarsePermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasLocationFinePermission != PackageManager.PERMISSION_GRANTED) {
            this.locationPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (hasLocationCoarsePermission != PackageManager.PERMISSION_GRANTED) {
            this.locationPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!this.locationPermissions.isEmpty()) {
            requestPermissions(this.locationPermissions.toArray(new String[this.locationPermissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }

        displayLocationSettingsRequest(getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                int grantedPermissions = 0;

                // count how many permissions have been granted
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grantedPermissions++;
                    }
                }

                // all permissions have been accepted by the user
                if (grantedPermissions == this.locationPermissions.size()) {
                    return;
                } else {
                    Log.e(TAG, "permissions not granted by the user");
                    Toast.makeText(getApplicationContext(), getString(R.string.must_accept_permissions), Toast.LENGTH_LONG).show();

                    finish();
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

    }

    //source: http://stackoverflow.com/a/33254073
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
}
