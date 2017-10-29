package com.gameboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gameboard.pong.OnSendGameMessage;
import com.gameboard.pong.PongGame;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class GameActivity extends AndroidApplication {

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        final boolean isHost = i.getBooleanExtra("IS_HOST", false);

        final PongGame game = new PongGame();

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        initialize(game, config);

        try {
            socket = IO.socket("http://172.25.252.234:8080");

            socket.on("broad", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String data = args[0].toString();
                    Log.d("NEW_SOCKET_EVENT", data);
                    game.sendEvent(data);
                }
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setHost(isHost);

                game.setOnUpdateListener(new OnSendGameMessage.OnSendGameMessageListener() {
                    @Override
                    public void sendMessage(String data) {
                        Log.d("UHHH", data);
                        socket.emit("game_update", data);
                    }
                });
            }
        });
    }

}
