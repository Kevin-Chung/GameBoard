package com.gameboard.pong;

/**
 * Created by Cameron on 10/29/17.
 */

public class OnSendGameMessage {
    public interface OnSendGameMessageListener {
        void sendMessage(String data);
    }
}