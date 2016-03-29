
package ch.hes_so.master.phonerally.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface BluetoothListener_I {
    void onBluetoothON();

    void onBluetoothOFF();

    void onDeviceConnected(BluetoothDevice btDevice);
}
