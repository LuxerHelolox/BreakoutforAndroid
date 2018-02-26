package com.example.laszlo.breakoutforandroid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;


public class MemoryGameActivity extends AppCompatActivity {
    private static TextView greetingView;
    private static GridLayout mylayout;
    private ImageButton myImageButton;

    static Stone [] table;
    static int row_size = 6;
    static int column_size = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        greetingView = new TextView(this);

        mylayout = (GridLayout) findViewById(R.id.MemoryGridLayout);
        //mylayout.addView(greetingView);

        //Button myButton = new Button(this);
        //myButton.setText("Push Me");

        //mylayout.addView(myButton);

       // myImageButton = new ImageButton(this);
        //myImageButton.setImageResource(R.drawable.buttonback);

        //final int buttonid = this.getResources().getIdentifier("button"+"1", "drawable","com.example.laszlo.breakoutforandroid");

        //myImageButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        //myImageButton.setImageResource(R.drawable.button1);
        //        myImageButton.setImageResource(buttonid);
        //    }
        //});

        //mylayout.addView(myImageButton);

        table = initialize (row_size * column_size);

        for (int i=0; i<row_size * column_size; i++){
            mylayout.addView (table[i].getButton() );
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_memory_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bye) {
            mylayout.removeView(greetingView);
            for (int i=0; i<row_size * column_size; i++){
                mylayout.removeView (table[i].getButton() );}
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_restart) {
            mylayout.removeView(greetingView);
            for (int i=0; i<row_size * column_size; i++){
                mylayout.removeView (table[i].getButton() );}

            table = initialize (row_size * column_size);

            for (int i=0; i<row_size * column_size; i++){
                mylayout.addView (table[i].getButton() );
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Stone [] initialize (int num_of_position) /* setup the table */
    {

        int picture;
        int position;
        int num_of_picture = num_of_position / 2;

        int[] usedposition = new int [num_of_position]; /*inicializalas */
        int[] usedpicture  = new int [32];

        Stone [] table = new Stone [num_of_position];

        int offset = (int) (Math.random() * (32));

        for (int i=0; i < num_of_picture; i++)
        {
            do {
                picture = (int) (Math.random() * (32));
            }
            while (usedpicture[picture] == 1 );
            usedpicture[picture++] = 1;


            do {
                position = (int) (Math.random() * (num_of_position));
            }
            while (usedposition[position] == 1 );
            usedposition[position] = 1;

            Stone stone1 = new Stone (this, picture );
            table [ position ] = stone1;

            do {
                position = (int) (Math.random() * (num_of_position));
            }
            while (usedposition[position] == 1 );
            usedposition[position] = 1;

            Stone stone2 = new Stone (this, picture);
            table [ position ] = stone2;

            stone1.setPair(stone2);
            stone2.setPair(stone1);
        }

        return table;
    }

    public static void gameOver(){
        for (int i=0; i<row_size * column_size; i++){
            mylayout.removeView (table[i].getButton() );
        }
        mylayout.addView(greetingView);
        greetingView.setText("You've finished it...");
        greetingView.setTextSize(40);
    }

}

