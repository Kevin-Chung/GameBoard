package com.gameboard.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by kevin on 10/28/2017.
 */

public class ClientSocket extends AsyncTask {

    public interface ClientListener{
        public void messageReceived(String s);
    }

    private static ClientSocket clientSocket;

    private static String outputString;
    private static String inputString;

    private static int testCount= 0;

    public ClientListener cListener ;

    Socket socket ;
    String host;
    int port = 8888;
    int timeout = 2000;

    Activity activity;

    public ClientSocket(String host, Activity activity){
        this.host = host;
        this.activity = activity;
    }

    public static ClientSocket getClientSocket(String host, Activity activity) {
        Log.d("hacktx2","inside of client socket "+clientSocket);
        if(clientSocket == null){
            clientSocket = new ClientSocket(host, activity);
        }
        outputString = null;
        inputString = null;
        return clientSocket;
    }

    public void setOutputString(String s) {
        outputString = s;
    }

    public void setInputString(String s){
        inputString = s ;
    }

    public void setCustomObjectListener(ClientSocket.ClientListener listener) {
        this.cListener = listener;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("HAcktx2","starting client socket ");

            Log.d("hacktx2","trying to connect to "+host+":"+port);
            if (socket == null) socket = new Socket(host,port);

            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(outputStreamWriter, true);

            JSONObject jsonObject;
            pw.println("{\"event_type\":\"pair\"}");
            while(true) {
//                Log.d("clientSocket",inputString);
                if(outputString != null) {
                    Log.d("Hacktx2", "trying to send data client");
                    JSONObject output = new JSONObject(outputString);

                    pw.println(output);
                    Thread.sleep(3000);
                    // don't do something more than once
                    outputString = null;
                }
                if(inputString != null){
                    // got input do something here
                    jsonObject = new JSONObject(inputString);
                    Log.d("HACKTX",jsonObject.toString());
                    // don't do something more than once
//                    outputString = "{test:CLient"+testCount+"}";
//                    testCount++;
                    final String temp = inputString;

                    if(cListener != null ){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cListener.messageReceived(temp);
                            }
                        });
                    }


                    inputString = null;
                }
                String inputString = br.readLine();

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
