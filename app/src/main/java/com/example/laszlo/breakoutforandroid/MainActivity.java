package com.example.laszlo.breakoutforandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /** Called when the user taps the Send button */
    public void startBreakoutGame(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, BreakoutGameActivity.class);
        startActivity(intent);
    }

}