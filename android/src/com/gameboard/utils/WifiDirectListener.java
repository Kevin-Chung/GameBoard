package com.gameboard.utils;

import android.net.wifi.p2p.WifiP2pInfo;

/**
 * Created by Cameron on 10/29/17.
 */

public class WifiDirectListener {
    public interface OnWifiDirectListener {
        void onConnected(final WifiP2pInfo info);
    }
}
