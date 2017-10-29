package com.gameboard.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by kevin on 10/28/2017.
 */

public class ClientSocket extends AsyncTask {

    Socket socket ;
    String host;
    int port = 8888;
    int timeout = 2000;

    public ClientSocket(String host){
        socket = new Socket();
        this.host = host;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("HAcktx2","starting client socket ");
            socket.bind(null);
            Log.d("hacktx2","trying to connect to "+host+":"+port);
            socket.connect((new InetSocketAddress(host, port)), 2000);
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            Log.d("Hacktx2","trying to send data");
            JSONObject output = new JSONObject("{test:test}");
            Log.d("hacktx2","TRYING TO SEND DATA "+outputStream+" "+outputStreamWriter+" "+output);
            outputStreamWriter.write(output.toString());
            outputStreamWriter.flush();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}
