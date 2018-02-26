package com.example.laszlo.breakoutforandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import java.util.Random;

import static java.lang.Math.abs;

public class Ball {
    RectF rect;
    float xVelocity;
    float yVelocity;
    int ballWidth;
    int ballHeight;

    private Bitmap bitmap;

    public Ball(Context context, int screenX, int screenY, int ballWidth, int ballHeight){

        // Start the ball travelling straight up at 100 pixels per second
        xVelocity = 400;
        yVelocity = -800;

        this.ballHeight = ballHeight;
        this.ballWidth = ballWidth;

        setRandomXVelocity();

        // Place the ball in the centre of the screen at the bottom
        // Make it a 10 pixel x 10 pixel square
        rect = new RectF();

        // Initialize the bitmap
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ball);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (ballWidth),
                (int) (ballHeight),
                false);

    }

    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void negativeXVelocity(){
        xVelocity = - abs(xVelocity);
    }

    public void positveXVelocity(){
        xVelocity =  abs(xVelocity);
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }


    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(int X, int Y){
        rect.left = X;
        rect.top = Y - ballHeight;
        rect.right = X + ballWidth;
        rect.bottom = Y ;
    }




}
