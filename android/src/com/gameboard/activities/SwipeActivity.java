package com.gameboard.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.gameboard.R;
import com.gameboard.gestures.OnSwipeGestureListener;

public class SwipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pairing_acitivty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View root = findViewById(android.R.id.content);
        root.setOnTouchListener(new OnSwipeGestureListener(getApplication()) {
            @Override
            public void onSwipeRight() {
                Toast.makeText(getApplicationContext(), "Swipe right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
                Toast.makeText(getApplicationContext(), "Swipe Left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeTop() {
                Toast.makeText(getApplicationContext(), "Swipe Top", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeBottom() {
                Toast.makeText(getApplicationContext(), "Swipe Bottom", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
