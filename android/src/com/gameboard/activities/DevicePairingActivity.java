package com.gameboard.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gameboard.R;
import com.gameboard.adapters.DeviceAdapter;
import com.gameboard.models.Device;
import com.gameboard.utils.WiFiDirectBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

public class DevicePairingActivity extends AppCompatActivity  {

    // set up variables. wifi p2p manager is overall manager, channel is used to connect to p2p p2p framework
    // broadcastreceiver will receive and notify activity of events
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    // peer listener, this is used to discover peers
    private PeerListListener myPeerListListener;

    // context
    private Context context;

    // set up intent filter
    private IntentFilter mIntentFilter;

    // broadcast receiver
    private WiFiDirectBroadcastReceiver broadcastReceiver;

    // list of peers
    private List<WifiP2pDevice> peerList = new ArrayList<WifiP2pDevice>();

    // list of devices
    private ArrayList<Device> deviceList = new ArrayList<Device>();

    // device listview
    private ListView listView;

    // device adapter
    private DeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        context = this;

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        // Emulators suck
        if (mManager == null) return;
        mChannel = mManager.initialize(this, getMainLooper(), null);


        mIntentFilter = new IntentFilter();
        // wifi p2p state changed
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // change in list of available peers
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // wifi p2p connectivity has changed
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // device details have changed
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // create wifi p2p manager and initialize a channel
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);



        // set device adapter
        deviceAdapter = new DeviceAdapter(deviceList,context);
        listView = findViewById(R.id.device_list_view);

        listView.setAdapter(deviceAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) findViewById(R.id.device_name);
                String deviceName = tv.getText().toString();
                tv = (TextView) findViewById(R.id.device_address);
                String deviceAddress = tv.getText().toString();

                connect(deviceAddress);

            }
        });


        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                /// don't need anything here...
                Log.d("HackTX","Discovering peers");
            }

            @Override
            public void onFailure(int i) {
                Log.d("HACKTX", "discovery failed for some reason"+i);
            }
        });

    }

    // both of these are not really needed but I added them anyways bite me
    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this, peerListListener);
        registerReceiver(broadcastReceiver, mIntentFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        try{
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        }catch(Exception e){
            Log.d("HACKTX","EXCEPTION HAS OCCURED WHILE TRYING TO UNREGISTER");
            Log.d("hacktx", e.getLocalizedMessage());
        }
    }

    void connect(String deviceAddress) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        Log.d("HACKTX2","Attempting to connect to device");

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("HACKTX2","connection initiated");
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(DevicePairingActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList pList) {
            peerList.clear();
            deviceList.clear();
            peerList.addAll(pList.getDeviceList());

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of
            // available peers, trigger an update.

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
            Log.d("HACKTX","PEER LIST HAS BEEN UPDATED!");
            for(int i = 0 ; i < peerList.size(); i++){
                WifiP2pDevice tempDevice = peerList.get(i);
                Device device = new Device(tempDevice.deviceName,tempDevice.deviceAddress);
                deviceList.add(device);

            }

            if(deviceList.size() >0 ){
                deviceAdapter.notifyDataSetChanged();
            }

            Log.d("HACKTX","PRINTING DEVICE LIST");
            for(Device device : deviceList){
                Log.d("HACKTX",device.toString());
            }

            if (peerList.size() == 0) {
                Log.d("HACKTX", "No devices found");
                return;
            }
        }
    };

}
