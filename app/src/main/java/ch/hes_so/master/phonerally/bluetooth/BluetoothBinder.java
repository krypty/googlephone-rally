
package ch.hes_so.master.phonerally.bluetooth;

import android.os.Binder;

public class BluetoothBinder extends Binder {
    private BluetoothService btService;

    public BluetoothBinder(BluetoothService bluetoothService) {
        this.btService = bluetoothService;
    }

    public BluetoothService getService() {
        return this.btService;
    }
}
