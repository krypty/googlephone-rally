
package ch.hes_so.master.phonerally.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ch.hes_so.master.phonerally.R;

public class BluetoothService extends Service {
    private static final String BT_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // not randomly chosen. It's a RFCOMM requirement

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static int counter = 0;
    private static int bindCounter = 0;

    private final IBinder mBinder = new BluetoothBinder(this);

    private BluetoothAdapter btAdapter;
    private List<BluetoothDevice> listBTDevices;

    private BluetoothListener_I callback;

    private BluetoothDevice btDevice;

    private BluetoothSocket btSocket;
    private OutputStream outStream;

    private boolean isConnected;

    @Override
    public void onCreate() {
        Log.d(getClass().getSimpleName(), "onCreate counter: " + ++counter);

        super.onCreate();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) Log.e(getClass().getSimpleName(), "No Bluetooth");

        // Register for broadcasts on BluetoothAdapter state change
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        setConnected(false);

        // activate bluetooth if not enabled
        if (!btAdapter.isEnabled()) {
            btAdapter.enable();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(getClass().getSimpleName(), "onStartCommand");
        // get bounded devices
        updateListBoundedDevice();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onBind: bindCounter: " + ++bindCounter);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(getClass().getSimpleName(), "onUnbind: bindCounter: " + --bindCounter);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy counter: " + --counter);

        closeBT();

        // Unregister broadcast listeners
        unregisterReceiver(mReceiver);
    }

    public void connect(String address) {
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        connect(device);
    }

    /**
     * Connect to the specified Bluetooth device
     *
     * @param device
     */
    public synchronized void connect(final BluetoothDevice device) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                if (btSocket != null && btSocket.isConnected() && btDevice != null && btDevice == device) {
                    Log.i(getClass().getSimpleName(), "connect: already connected to this device");
                    return true;
                }

                if (btSocket != null && btSocket.isConnected()) {
                    closeBT();
                }

                btDevice = device;

                try {
                    btSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(BT_UUID));
                } catch (IOException e) {
                    fireOnError(e);
                    Log.e(getClass().getSimpleName(), "connect:  and socket create failed: " + e.getMessage() + ".");
                }

                // Discovery is resource intensive.  Make sure it isn't going on
                // when you attempt to connect and pass your message.
                btAdapter.cancelDiscovery();

                try {
                    btSocket.connect();
                    Log.d(getClass().getSimpleName(), "bt connected !");

                    setConnected(true);
                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        fireOnError(e2);
                        Log.e(getClass().getSimpleName(), "connect: unable to close socket during connection failure" + e2.getMessage() + ".");
                    } finally {
                        setConnected(false);
                    }
                }

                try {
                    outStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    fireOnError(e);
                    Log.e(getClass().getSimpleName(), "connect: output stream creation failed:" + e.getMessage() + ".");
                }

                return isConnected;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                if (callback != null) {
                    if (isConnected && btDevice != null) {
                        Context context = getApplicationContext();
                        Toast.makeText(context, context.getString(R.string.bt_connected), Toast.LENGTH_SHORT).show();

                        callback.onDeviceConnected(btDevice);
                    }
                }
            }

        }.execute();
    }

    public void sendCommand(String command) throws IOException {
        outStream.write(command.getBytes(Charset.forName("UTF-8")));
        outStream.flush();
    }


    public List<BluetoothDevice> getListBTDevices() {
        return listBTDevices;
    }

    public void setCallback(BluetoothListener_I callback) {
        this.callback = callback;
    }

    private void fireOnError(Exception e) {
        if (callback != null) {
//            callback.onCameraError(e);
        }
    }

    protected void fireBluetoothEnabled(boolean enabled) {
        if (callback != null) {
            if (enabled) {
                callback.onBluetoothON();
            } else {
                callback.onBluetoothOFF();
            }
        }
    }

    private void closeBT() {
        setConnected(false);

        try {
            btSocket.close();
        } catch (IOException e) {
        } catch (NullPointerException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            outStream.close();
        } catch (IOException e) {
        } catch (NullPointerException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        btSocket = null;
        outStream = null;
    }

    private void updateListBoundedDevice() {
        listBTDevices = new ArrayList<BluetoothDevice>(btAdapter.getBondedDevices());
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(getClass().getSimpleName(), "BT State OFF");
                        setConnected(false);
                        fireBluetoothEnabled(false);
                        break;

                    case BluetoothAdapter.STATE_ON:
                        Log.d(getClass().getSimpleName(), "BT State ON");
                        updateListBoundedDevice();
                        fireBluetoothEnabled(true);
                        break;
                }
            }
        }
    };

    public boolean isConnected() {
        return isConnected;
    }

    private void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
