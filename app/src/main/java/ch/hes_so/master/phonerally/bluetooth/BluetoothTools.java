
package ch.hes_so.master.phonerally.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ch.hes_so.master.phonerally.R;

public class BluetoothTools {
    public static String getSavedBTDeviceAddress(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_bt_device_address), null);
    }
}
