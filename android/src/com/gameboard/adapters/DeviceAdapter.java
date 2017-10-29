package com.gameboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gameboard.R;
import com.gameboard.models.Device;

import java.util.ArrayList;

/**
 * Created by kevin on 10/28/2017.
 */

public class DeviceAdapter extends BaseAdapter {

    private ArrayList<Device> deviceList;
    private Context context;

    public DeviceAdapter(ArrayList<Device> devices, Context context){
        this.context = context;
        this.deviceList = devices;
    }

    @Override
    public int getCount() {
        return this.deviceList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.deviceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.device_item, viewGroup, false);

        Device device = deviceList.get(i);

        TextView device_name = (TextView) rowView.findViewById(R.id.device_name);
        TextView device_address = (TextView) rowView.findViewById(R.id.device_address);


        device_name.setText(device.getDeviceName());
        device_address.setText(device.getDeviceAddress());

        return rowView;
    }
}
