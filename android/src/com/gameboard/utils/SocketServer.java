package utils;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kevin on 10/28/2017.
 */

public class SocketServer extends AsyncTask {

    private Context context;

    private SocketServer(Context context){
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);

            Socket client = serverSocket.accept();

            InputStream inputStream = client.getInputStream();

//            while(inputStream.read())



        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
