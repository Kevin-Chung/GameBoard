package com.gameboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gameboard.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameConfigActivity extends AppCompatActivity {

    public static final String TAG = "GAME_CONFIG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_config);

        Log.d(TAG, "LOADED");

        try {
            Socket socket = IO.socket("http://172.25.252.234:8080");

            socket.on("broad", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String data = args[0].toString();
                    try {
                        JSONObject json = new JSONObject(data);
                        if (json.get("event") == "start_game") {
                            Intent i = new Intent(getApplicationContext(), SwipeActivity.class);
                            startActivity(i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("NEW_SOCKET_EVENT", data);
                }
            });

            socket.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
