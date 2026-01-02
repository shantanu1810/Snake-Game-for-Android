package com.example.snakegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    private ImageButton replay;
    private TextView score;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game_over);
        score=findViewById(R.id.scoreView);
        replay=findViewById(R.id.replay_btn);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            int sc=bundle.getInt("SCORE",0);
            score.setText("Score - "+String.valueOf(sc));
        }
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(GameOverActivity.this,GameActivity.class);
                startActivity(intent);
            }
        });
    }
}
