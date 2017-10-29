package com.gameboard.activities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gameboard.R;
import com.gameboard.gestures.OnSwipeGestureListener;

public class SwipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pairing_acitivty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
