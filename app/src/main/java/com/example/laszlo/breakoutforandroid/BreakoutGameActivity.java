package com.example.laszlo.breakoutforandroid;

import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;

public class BreakoutGameActivity extends AppCompatActivity {

    BreakoutView breakoutView;

    static final int ballWidth = 40;
    static final int ballHeight = 40;
    static final int paddleWidth = 300;
    static final int paddleHeight = 80;
    static final int paddleRow = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // you can't directly set a background image to a SurfaceView, you can overlap an ImageView
        // (displaying your background image) and your SurfaceView on top of this, making
        // it transparent.

        breakoutView = new BreakoutView(this);
        breakoutView.setZOrderOnTop(true);
        breakoutView.getHolder().setFormat(PixelFormat.TRANSPARENT);

        // Setup your ImageView
        ImageView bgImagePanel = new ImageView(this);
        bgImagePanel.setBackgroundResource(R.drawable.bkgnd1); // use any Bitmap or BitmapDrawable you want

        // Use a RelativeLayout to overlap both SurfaceView and ImageView
        RelativeLayout.LayoutParams fillParentLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        RelativeLayout rootPanel = new RelativeLayout(this);
        rootPanel.setLayoutParams(fillParentLayout);
        rootPanel.addView(breakoutView, fillParentLayout);
        rootPanel.addView(bgImagePanel, fillParentLayout);

        // setContentView(breakoutView);
        setContentView(rootPanel);

    }


   @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}}


    class BreakoutView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // Game is paused at the start
        boolean paused = true;

        boolean won = false;


        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;

        // The size of the screen in pixels
        int screenX;
        int screenY;

        float touchX;

        Context context;

        // The players paddle
        Paddle paddle;

        // A ball
        Ball ball;

        // Up to 200 bricks
        Brick[] bricks = new Brick[200];
        int numBricks = 0;
        int clearedBricks = 0;

        // For sound FX
        SoundPool soundPool;
        int beep1ID = -1;
        int beep2ID = -1;
        int beep3ID = -1;
        int loseLifeID = -1;
        int explodeID = -1;
        int applauseID = -1;

        // The score
        int score = 0;

        // Lives
        int lives = 3;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public BreakoutView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);
            this.context = context;

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();


            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            paddle = new Paddle(context, screenX, screenY, paddleWidth, paddleHeight, paddleRow);

            // Create a ball
            ball = new Ball(context, screenX, screenY, ballWidth, ballHeight);

            // Load the sounds

            // This SoundPool is deprecated but don't worry
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            try {
                // Create objects of the 2 required classes
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                // Load our fx in memory ready for use
                descriptor = assetManager.openFd("Robot_blip_0.wav");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("Robot_blip_1.wav");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("Crowd Boo.wav");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("Balloon Popping.wav");
                explodeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("Audience_Applause.wav");
                applauseID= soundPool.load(descriptor, 0);

            } catch (IOException e) {
                // Print an error message to the console
                Log.e("error", "failed to load sound files");
            }

            createBricksAndRestart();

        }

        public void createBricksAndRestart() {

            // Put the ball back to the start
            ball.reset(screenX/2-ballWidth/2, screenY - paddleRow - paddleHeight);
            paddle.reset ();

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 20;

            // Build a wall of bricks
            numBricks = 0;
            clearedBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 6; row++) {
                    bricks[numBricks] = new Brick(context, row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }
            // if game over reset scores and lives
            if (lives == 0 ) {
                score = 0;
                lives = 3;
                won = false;
            }

            if ( won) {
                lives = 3;
                won = false;
            }
        }

        @Override
        public void run() {
            while (playing) {
                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();
                // Update the frame
                if (!paused)  update();
                draw();
                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {

            // Move the paddle if required
            paddle.update(fps);

            ball.update(fps);

            // Check for ball colliding with a brick
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        clearedBricks ++;
                        ball.reverseYVelocity();
                        score = score + 10;
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }
            // Check for ball colliding with paddle
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {

                float paddle_segment = (paddle.getRect().right - paddle.getRect().left)/5;
                if (ball.getRect().left < paddle.getRect().left + 2 * paddle_segment){
                    ball.negativeXVelocity();}
                else if (ball.getRect().left > paddle.getRect().left + 3 * paddle_segment) {
                    ball.positveXVelocity();}
                else {ball.setRandomXVelocity();};

                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }
            // Bounce the ball back when it hits the bottom of screen
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);

                // Lose a life
                lives--;
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);
                paused = true;
                ball.reset(screenX/2-ballWidth/2, screenY - paddleRow - paddleHeight);
                paddle.reset();

                if (lives == 0) {
                    paused = true;
                    //createBricksAndRestart();
                }
            }

            // Bounce the ball back when it hits the top of screen
            if (ball.getRect().top < 0)

            {
                ball.reverseYVelocity();
                ball.clearObstacleY(ballHeight + 2);
                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits left wall bounce
            if (ball.getRect().left < 0)

            {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // If the ball hits right wall bounce
            if (ball.getRect().right > screenX) {

                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - ballWidth - 2);

                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            // Pause if cleared screen
            if (clearedBricks == numBricks )

            {   soundPool.play(applauseID, 1, 1, 0, 0, 1);
                won =true;
                paused = true;

            }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                //canvas.drawColor(Color.argb(255, 26, 128, 182));

                //"flush" the previous drawn image in SurfaceView's buffer
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);

                // Choose the brush color for drawing
                // paint.setColor(Color.argb(255, 255, 255, 255));


                // Draw the ball
                // canvas.drawRect(ball.getRect(), paint);
                canvas.drawBitmap(ball.getBitmap(), ball.getRect().left,ball.getRect().top, paint);

                // Draw the paddle
                //canvas.drawRect(paddle.getRect(), paint);
                canvas.drawBitmap(paddle.getBitmap(), paddle.getRect().left,paddle.getRect().top, paint);

                // Change the brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));

                // Draw the bricks if visible
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        //canvas.drawRect(bricks[i].getRect(), paint);
                        canvas.drawBitmap(bricks[i].getBitmap(), bricks[i].getRect().left,bricks[i].getRect().top, paint);
                    }
                }

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // Draw the score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

                // Has the player cleared the screen?
                if (won) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE WON!", 10, screenY / 2, paint);


                }

                // Has the player lost?
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenY / 2, paint);
                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    if (lives > 0 && !won)
                    {   paused = false;
                        touchX = motionEvent.getX();
                        if (touchX > (paddle.getRect().left) + paddleWidth/2) {

                            paddle.setMovementState(paddle.RIGHT);
                        } else

                        {
                            paddle.setMovementState(paddle.LEFT);
                        }}

                    break;

                case MotionEvent.ACTION_MOVE:
                    if (lives > 0 && !won)
                    {   paused = false;
                        float newtouchX = motionEvent.getX();
                        if (motionEvent.getX() > touchX) {

                            paddle.setMovementState(paddle.RIGHT);
                        } else
                        {
                            paddle.setMovementState(paddle.LEFT);
                        }
                        touchX = newtouchX;
                    }

                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:
                    if (lives == 0 || won)
                        {createBricksAndRestart();}

                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }

            return true;
        }

    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        breakoutView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        breakoutView.pause();
    }


}
