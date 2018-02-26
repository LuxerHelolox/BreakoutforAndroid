package com.example.laszlo.breakoutforandroid;

/**
 * Created by Laszl on 2017. 08. 20..
 */

import android.graphics.RectF;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.content.Context;

public class Paddle {

    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our paddle will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speed that the paddle will move
    private float paddleSpeed;

    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the paddle moving and in which direction
    private int paddleMoving = STOPPED;

    private Bitmap bitmap;

    private int screenX, screenY, paddleWidth, paddleHeight, paddleRow;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(Context context, int screenX, int screenY, int paddleWidth, int paddleHeight,
                  int paddleRow){


        this.screenX = screenX;
        this.screenY = screenY;

        this.paddleWidth = paddleWidth;
        this.paddleHeight = paddleHeight;
        this.paddleRow = paddleRow;


        rect = new RectF();
        reset();

        // How fast is the paddle in pixels per second
        paddleSpeed = 800;

        // Initialize the bitmap
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.paddle);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (paddleWidth),
                (int) (paddleHeight),
                false);

    }

    // This is a getter method to make the rectangle that
    // defines our paddle available in BreakoutView class
    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }


    // This method will be used to change/set if the paddle is going left, right or nowhere
    public void setMovementState(int state){
        paddleMoving = state;
    }

    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary
    public void update(long fps){
        x = rect.left;
        if(paddleMoving == LEFT && x > 0){
            x = x - paddleSpeed / fps;
            if (x<0) x=0;
        }


        if(paddleMoving == RIGHT && x < screenX-paddleWidth){
            x = x + paddleSpeed / fps;
            if (x > screenX-paddleWidth) x = screenX-paddleWidth;
        }

        rect.left = x;
        rect.right = x + paddleWidth;
    }

    public void reset(){
        rect.left = screenX / 2-paddleWidth/2;
        rect.top = screenY - paddleRow - paddleHeight;;
        rect.right = screenX / 2 + paddleWidth/2;
        rect.bottom = screenY - paddleRow ;
    }

}
