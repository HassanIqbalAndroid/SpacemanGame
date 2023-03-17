package com.example.saveship;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GameOver extends AppCompatActivity {
    TextView tvPoints;
    TextView tvHeighest;
    SharedPreferences sharedPreferences;
    ImageView ivNewHeighest;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        tvPoints = findViewById(R.id.tvPoints);
        tvHeighest = findViewById(R.id.tvHeighest);
        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText(""+points);
        sharedPreferences = getSharedPreferences("my_pref",0);
        int highest = sharedPreferences.getInt("highest",0);
        if (points>highest){
            highest = points;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highest",highest);
            editor.commit();
        }
        tvHeighest.setText(""+highest);
    }
    public void restart(View view){
        Intent intent = new Intent(GameOver.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    public void exit(View view){
        finish();
    }
}
