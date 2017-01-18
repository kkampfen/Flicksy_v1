package com.bananaroo.flicksy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {


    ImageButton replayButton;
    TextView scoreLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        Bundle bundle = getIntent().getExtras();
        String score = bundle.getString("passScore");

        scoreLabel.setText(score);
        replayButton = (ImageButton) findViewById(R.id.replayButton);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
