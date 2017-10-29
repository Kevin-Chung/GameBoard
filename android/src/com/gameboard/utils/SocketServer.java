package com.gameboard.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kevin on 10/28/2017.
 */

public class SocketServer extends AsyncTask {

    private static SocketServer socketServer;

    private SocketServer(){
    }

    public static SocketServer getSocketServer() {
        if(socketServer == null ){
            socketServer = new SocketServer();
        }
        return socketServer;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("HACKTX2","STARTING SOCKET SERVER");

            ServerSocket serverSocket = new ServerSocket(8888);
            Socket socket = serverSocket.accept();
            Log.d("hacktx2","IP ADDRESSS"+serverSocket.getInetAddress());

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //read in json object.
            JSONObject jsonObject;
            while(true) {

                Log.d("Hacktx2","Trying to read data");
                String temp = br.readLine();
                if(temp==null){
                    Thread.sleep(2500);
                }else {

                    jsonObject = new JSONObject(temp);
                    Log.d("HACKTX2", "TEST" + jsonObject.toString());
                    Log.d("HACKTX2", jsonObject.getString("test"));
                    // do game logic with json object
                    Thread.sleep(2500);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
