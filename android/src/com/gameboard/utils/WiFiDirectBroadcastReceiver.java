package com.gameboard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.gameboard.activities.DevicePairingActivity;

import java.net.InetAddress;
import java.net.ServerSocket;


/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener {

    public static String TAG = "WiFiBroadcastReceiver";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private DevicePairingActivity mActivity;
    private WifiP2pManager.PeerListListener peerListListener;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       DevicePairingActivity activity, WifiP2pManager.PeerListListener peerListListener) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.peerListListener = peerListListener;
    }


    // method fired when an intent is received, we listen for intents in swipeactivity intent filter
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("onreceive","in onreceive");
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG,"WIFI P2P STATE CHANGED");

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "WIFI P2P STATE ENABLED");
            } else {
                Log.d(TAG, "WIFI P2P STATE NOT ENABLED");
            }
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(TAG,"WIFI P2P PEERS CHANGED");

            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // connection changed! This might mean we've connected/disconnected!


            Log.d(TAG,"connection changed");
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);



            if (networkInfo.isConnected()) {
                Log.d(TAG,"WE ARE CONNECTED");
                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, this);
            }



            Log.d(TAG,"WIFI P2P CONNECTION CHANGED");
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG,"WIFI P2P THIS DEVICE CHANGED ");
            // Respond to this device's wifi state changing
        }
    }


    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        Log.d(TAG,"Connect info available");

        // InetAddress from WifiP2pInfo struct.
        // not sure what to do with this for now..
        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();


        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            Log.d("HACKTX2","I'm owner");

            Toast.makeText(mActivity,"I am the owner",Toast.LENGTH_LONG).show();
            // create new server socket
            SocketServer socketServer = SocketServer.getSocketServer(groupOwnerAddress, mActivity);
            socketServer.setOutputString("{test:test}");
            socketServer.setCustomObjectListener(new SocketServer.TempListener() {
                @Override
                public void messageReceived(String message) {
                    Toast.makeText(mActivity,message,Toast.LENGTH_SHORT).show();
                }
            });
            socketServer.execute();
            // I am owner, create a ServerSocket and allow for a person to connect


        } else if (info.groupFormed) {

            Toast.makeText(mActivity,"I am NOT the owner",Toast.LENGTH_SHORT).show();
            Log.d("HACKTX2","group formed" +groupOwnerAddress);
            ClientSocket clientSocket = ClientSocket.getClientSocket(groupOwnerAddress, mActivity);
            clientSocket.setOutputString("{test:test}");
            clientSocket.setCustomObjectListener(new ClientSocket.ClientListener() {
                @Override
                public void messageReceived(String message) {
                    Toast.makeText(mActivity,message,Toast.LENGTH_LONG).show();
                }
            });
            clientSocket.execute();
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }
}
