package com.bananaroo.flicksy;

import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameActivity extends AppCompatActivity implements OnTouchListener {

    GameView game;
    RectF touchRect;
    float downX = 0;
    float downY = 0;
    float upX = 0;
    float upY = 0;
    float maxYSpeed = -25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = new GameView(this);
        game.setOnTouchListener(this);
        setContentView(game);
    }

    @Override
    protected void onPause() {
        super.onPause();
        game.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        game.resume();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        event.getSource();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = event.getRawY();
                touchRect = new RectF(downX - 90, downY - 90, downX + 90, downY + 90);
                break;

            case MotionEvent.ACTION_UP:
                upX = event.getRawX();
                upY = event.getRawY();
                float yFactor = (upY - downY) / maxYSpeed;
                float flickedXSpeed = (upX - downX) / yFactor;
                float flickedYSpeed = maxYSpeed;
                game.checkTouch(touchRect, flickedXSpeed, flickedYSpeed);
                break;
        }

        return true;
    }
}
