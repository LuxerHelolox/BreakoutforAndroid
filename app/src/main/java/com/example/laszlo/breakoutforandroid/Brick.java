package com.example.laszlo.breakoutforandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

public class Brick {

    private RectF rect;

    private boolean isVisible;

    private Bitmap bitmap;

    public Brick(Context context, int row, int column, int width, int height){

        isVisible = true;

        int padding = 1;

        int from_top = 100;

        // Initialize the bitmap
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_brick);

        // stretch the bitmap to a size appropriate for the screen resolution
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (width),
                (int) (height),
                false);

        rect = new RectF(column * width + padding,
                row * height + padding + from_top,
                column * width + width - padding,
                row * height + height - padding + from_top);
    }

    public RectF getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}

