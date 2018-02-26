package com.example.laszlo.breakoutforandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;

/**
 * Created by Laszl on 2018. 02. 25..
 */

public class Stone {/******************* The memory stone **********************************/

    private Stone pair;
    private boolean up;     /* face up */
    private ImageButton button;
    static private int turned_buttons = 0;
    static private int removed_pairs = 0;
    static private Stone selected1;
    static private Stone selected2;
    private Context context;
    private Bitmap buttonbitmap;
    static private Bitmap buttonbackbitmap, buttonemptybitmap;


    public Stone(Context context, int p )
        {
        int buttonid = context.getResources().getIdentifier("button"+ p, "drawable","com.example.laszlo.breakoutforandroid");
        buttonbitmap = BitmapFactory.decodeResource(context.getResources(), buttonid);
        // stretch the bitmap to a size appropriate for the screen resolution
        buttonbitmap = Bitmap.createScaledBitmap(buttonbitmap,
                    (int) (200),
                    (int) (200),
                    false);

        buttonbackbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.buttonback);
        // stretch the bitmap to a size appropriate for the screen resolution
        buttonbackbitmap = Bitmap.createScaledBitmap(buttonbackbitmap,
                    (int) (200),
                    (int) (200),
                    false);

        buttonemptybitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.buttonempty);
        // stretch the bitmap to a size appropriate for the screen resolution
        buttonemptybitmap = Bitmap.createScaledBitmap(buttonemptybitmap,
                    (int) (200),
                    (int) (200),
                    false);


        this.context = context;
        up = false;
        button = new ImageButton(context);
        button.setImageBitmap(buttonbackbitmap);
        button.setEnabled(true);
        button.setClickable(true);

        button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (button.isEnabled()) {
                        if (turned_buttons == 0) {
                            turned_buttons++;
                            selected1 = Stone.this;
                            turn();
                        } else if (turned_buttons == 1) {
                            if (!up) {
                                turned_buttons++;
                                selected2 = Stone.this;
                                turn();
                                if (selected1 == pair) {
                                    removed_pairs++;
                                    deActivate();
                                    selected1.deActivate();
                                    turned_buttons = 0;

                                    if (removed_pairs == 12) {
                                        MemoryGameActivity.gameOver();
                                    }
                                                                }
                            }
                            ;
                        } else {
                            if (!up) {
                                selected1.turn();
                                selected2.turn();
                                turned_buttons = 0;
                                turned_buttons++;
                                selected1 = Stone.this;
                                turn();
                            }
                        }
                    }
                }



            });

     }

    public void setPair(Stone p) {pair = p;}

    public void deActivate () {
        button.setEnabled(false);
        button.setSelected(false);
        button.setClickable(false);
        button.setImageBitmap(buttonemptybitmap);
        button.setBackgroundColor(Color.WHITE);
        }

    public void turn () {
            up = !up;
            if (up){
                button.setSelected(true);
                button.setImageBitmap(buttonbitmap);
            }
            else {button.setSelected(false);
                button.setImageBitmap(buttonbackbitmap);}
        }


    public ImageButton getButton (){return button;};
}




