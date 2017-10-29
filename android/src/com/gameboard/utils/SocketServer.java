package com.gameboard.utils;

import android.app.Activity;
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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kevin on 10/28/2017.
 */

public class SocketServer extends AsyncTask {

    public interface TempListener{
        public void messageReceived( String s);
    }

    private TempListener tL;

    private static SocketServer socketServer;

    private static String outputString;
    private static String inputString;

    private Activity activity;

    private String host;

    private SocketServer(String host, Activity activity){
        this.activity = activity;
        this.host = host;
    }



    public static SocketServer getSocketServer(String host, Activity activity) {
        if(socketServer == null ){
            socketServer = new SocketServer(host, activity);
        }
        outputString = null;
        inputString = null;
        return socketServer;
    }

    private static int testCount = 0;

    public void setOutputString( String s ){
        outputString = s;
    }

    public void setInputString( String s ){
        inputString = s;
    }

    public void setCustomObjectListener(TempListener listener) {
        this.tL = listener;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.d("HACKTX2","STARTING SOCKET SERVER");

            ServerSocket serverSocket = new ServerSocket(8888);
            Socket socket = serverSocket.accept();
            Log.d("hacktx2","IP ADDRESSS"+serverSocket.getInetAddress());

            // get input and output streams / reader / writer
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            PrintWriter pw = new PrintWriter(outputStreamWriter, true);

            //read in json object.
            JSONObject jsonObject;
            pw.println("{test:test}");
            while(true) {
                String inputString = br.readLine();
                if (outputString != null) {

                    // send data over
                    JSONObject output = new JSONObject(outputString);
                    pw.println(output);

                    Thread.sleep(1000);
                    // don't do something more than once with the data
                    outputString = null;
                }

                if (inputString!= null) {

                    // THIS IS THE INPUT JSON
                    jsonObject = new JSONObject(inputString);
                    Log.d("HACKTX2", jsonObject.toString());
                    // do game logic with json

                    final String temp = inputString;
                    if(tL != null ){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tL.messageReceived(temp);
                            }
                        });
                    }


                    // RECEIVED INPUT DO SOMETHING HERE CAMERON
                    testCount++;
                    outputString = "{test:server"+testCount +"}";

                    // don't do something more than once with the data
                    inputString = null;
                }
            }

            } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
