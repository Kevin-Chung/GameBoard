package com.gameboard.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by kevin on 10/28/2017.
 */

public class ClientSocket extends AsyncTask {

    private static ClientSocket clientSocket;

    Socket socket ;
    String host;
    int port = 8888;
    int timeout = 2000;


    public ClientSocket(String host){
        this.host = host;
    }

    public static ClientSocket getClientSocket(String host) {
        if(clientSocket == null){
            clientSocket = new ClientSocket(host);
        }
        return clientSocket;
    }

    public static ClientSocket getClientSocket() {
        return clientSocket;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("HAcktx2","starting client socket ");

            Log.d("hacktx2","trying to connect to "+host+":"+port);
            socket = new Socket(host,port);

            while(true) {
                OutputStream outputStream = socket.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                PrintWriter pw = new PrintWriter(outputStreamWriter,true);

                Log.d("Hacktx2", "trying to send data");
                JSONObject output = new JSONObject("{test:test}");
                Log.d("hacktx2", "TRYING TO SEND DATA " + outputStream + " " + outputStreamWriter + " " + output);

                pw.println(output);
                Thread.sleep(1000);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return null;
    }
}
