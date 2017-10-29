package com.gameboard.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gameboard.R;
import com.gameboard.gestures.OnSwipeGestureListener;
import com.gameboard.utils.ClientSocket;
import com.gameboard.utils.SocketServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SwipeActivity extends AppCompatActivity {

    boolean isHost = false;
    SocketServer server;
    ClientSocket client;

    int clientDirection = -1;
    int serverDirection = -1;

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pairing_acitivty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        isHost = i.getBooleanExtra("IS_HOST", false);

//        try {
//            socket = IO.socket("https://gameboard-socketio.herokuapp.com/");
//            socket.on("broad", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    String data = args[0].toString();
//                    Log.d("NEW_SOCKET_EVENT", data);
//
//
//                    try {
//                        JSONObject json = new JSONObject(data);
//                        String type = json.getString("event_type");
//                        if (type.equals("swipe")) {
//                            int d = json.getInt("direction");
//
//                            clientDirection = d;
//                            tryNextPage();
////                            Toast.makeText(getApplicationContext(), "CLIENT DIRECTION " + d, Toast.LENGTH_SHORT).show();
//                        }
//
//                        else if (type.equals("start_game")) {
//                            int cd = json.getInt("client_direction");
//                            int hd = json.getInt("host_direction");
//
//                            Intent i = new Intent(getApplicationContext(), GameActivity.class);
//                            i.putExtra("IS_HOST", isHost);
//                            i.putExtra("CLIENT_DIRECTION", cd);
//                            i.putExtra("HOST_DIRECTION", hd);
//                            startActivity(i);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("NEW_SOCKET_EVENT", data);
//                }
//            });
//
//            socket.connect();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }


        // More server stuff
        if (isHost) {
            this.server = SocketServer.getSocketServer(null, this);
            if (server.getStatus() != AsyncTask.Status.RUNNING) {
                server.execute();
            }
//            server.setOutputString("{test:test}");
            server.setCustomObjectListener(new SocketServer.TempListener() {
                @Override
                public void messageReceived(String message) {
                    dataReceived(message);
                }
            });


        } else {
            this.client = ClientSocket.getClientSocket(null, this);
            if (client.getStatus() != AsyncTask.Status.RUNNING) {
                client.execute();
            }
//            client.setOutputString("{test:test}");
            client.setCustomObjectListener(new ClientSocket.ClientListener() {
                @Override
                public void messageReceived(String message) {
                    dataReceived(message);
                }
            });


        }

        // Fade Arrows
        int arrows[] = {
                R.id.imageview_arrow_1,
                R.id.imageview_arrow_2,
                R.id.imageview_arrow_3,
                R.id.imageview_arrow_4,
                R.id.imageview_arrow_5,
                R.id.imageview_arrow_6
        };

        for (int arrowId : arrows) {
            ImageView image = findViewById(arrowId);

            ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(image, "alpha",  1f, 0.05f);
            alphaAnimation.setDuration(1000);
            alphaAnimation.setRepeatMode(ValueAnimator.REVERSE);
            alphaAnimation.setRepeatCount(ValueAnimator.INFINITE);
            alphaAnimation.start();
        }

        // Swipe listener on root

        View root = findViewById(android.R.id.content);
        root.setOnTouchListener(new OnSwipeGestureListener(getApplication()) {
            @Override
            public void onSwipeRight() {
                sendSwipeToDevice(1);
            }

            @Override
            public void onSwipeLeft() {
                sendSwipeToDevice(3);
            }

            @Override
            public void onSwipeTop() {
                sendSwipeToDevice(0);
            }

            @Override
            public void onSwipeBottom() {
                sendSwipeToDevice(2);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

//        if(socket.connected()) socket.disconnect();
    }

    private void dataReceived(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String type = json.getString("event_type");

            if (type.equals("swipe")) {
                int d = json.getInt("direction");

                clientDirection = d;
                tryNextPage();
//                            Toast.makeText(getApplicationContext(), "CLIENT DIRECTION " + d, Toast.LENGTH_SHORT).show();
            }

            else if (type.equals("start_game")) {
                int cd = json.getInt("client_direction");
                int hd = json.getInt("host_direction");

                Intent i = new Intent(getApplicationContext(), GameActivity.class);
                i.putExtra("IS_HOST", isHost);
                i.putExtra("CLIENT_DIRECTION", cd);
                i.putExtra("HOST_DIRECTION", hd);
                startActivity(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void tryNextPage() {
        if (this.serverDirection != -1 && this.clientDirection != -1) {
//            socket.emit("game_update", "{\"event_type\": \"start_game\", \"host_direction\": " + this.serverDirection + ", \"client_direction\": " + this.clientDirection + "}");
            server.setOutputString("{\"event_type\": \"start_game\", \"host_direction\": " + this.serverDirection + ", \"client_direction\": " + this.clientDirection + "}");

            if (isHost) {
                Intent i = new Intent(getApplicationContext(), GameActivity.class);
                i.putExtra("IS_HOST", isHost);
                i.putExtra("CLIENT_DIRECTION", this.clientDirection);
                i.putExtra("HOST_DIRECTION", this.serverDirection);
                startActivity(i);
            }
        }
    }


    private void sendSwipeToDevice(int direction) {
        Log.d("TEST", "Got swipe " + direction + " " + isHost);
        if (isHost) {
            serverDirection = direction;
//            Toast.makeText(this, "SERVER DIRECTION " + direction, Toast.LENGTH_SHORT).show();
            tryNextPage();
//            server.setOutputString("{\"event_type\": \"swipe\", \"direction\": " + direction + "}");
            Toast.makeText(this, "SERVER DIRECTION " + direction, Toast.LENGTH_SHORT).show();
            server.setOutputString("{\"event_type\": \"swipe\", \"direction\": " + direction + "}");
        } else {

            client.setOutputString("{\"event_type\": \"swipe\", \"direction\": " + direction + "}");
//            socket.emit("game_update", "{\"event_type\": \"swipe\", \"direction\": " + direction + "}");
        }
    }
}
