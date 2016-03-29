
package ch.hes_so.master.phonerally.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.hes_so.master.phonerally.R;

public class BluetoothDevicesListAdapter extends ArrayAdapter<BluetoothDevice> {
    private List<BluetoothDevice> listDevices;
    private Context context;

    public BluetoothDevicesListAdapter(Context context, List<BluetoothDevice> listDevices) {
        super(context, R.layout.bluetooth_device_row, listDevices);
        this.listDevices = listDevices;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.bluetooth_device_row, parent, false);
        TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
        tvTitle.setTextColor(Color.BLACK);
        TextView tvSummary = (TextView) rowView.findViewById(R.id.tvSummary);
        tvSummary.setTextColor(Color.BLACK);

        BluetoothDevice btDevice = listDevices.get(position);
        tvTitle.setText(btDevice.getName());
        tvSummary.setText(btDevice.getAddress());

        return rowView;
    }
}
