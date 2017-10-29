package com.gameboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Open screens
        ArrayList<Pair<Integer, Class<?>>> buttons = new ArrayList<>();
        buttons.add(new Pair<Integer, Class<?>>(R.id.button_open_game, GameActivity.class));
        buttons.add(new Pair<Integer, Class<?>>(R.id.button_open_pariring, DevicePairingActivity.class));
        buttons.add(new Pair<Integer, Class<?>>(R.id.button_open_swipe, SwipeActivity.class));
        buttons.add(new Pair<Integer, Class<?>>(R.id.button_open_config, GameConfigActivity.class));


        for (Pair t : buttons) {
            Button b = findViewById((int) t.first);
            final Class<?> dest = (Class<?>) t.second;
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), dest);
                    startActivity(i);
                }
            });
        }
    }

}
