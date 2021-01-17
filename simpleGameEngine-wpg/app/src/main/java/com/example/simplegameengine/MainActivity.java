package com.example.simplegameengine;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = new GameView(this);
        setContentView(gameView);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    class GameView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;

        Canvas canvas;
        Paint paint;

        long fps;
        private long timeThisFrame;
        Bitmap bitmapMario;
        boolean isMoving = false;
        boolean forward = true;
        float walkSpeedPerSecond = 150;
        float marioXPosition = 10;

        public GameView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();

            bitmapMario = BitmapFactory.decodeResource(this.getResources(), R.drawable.bob);
            playing = true;
        }

        @Override
        public void run() {
            while (playing) {
                long startFrameTIme = System.currentTimeMillis();

                update();
                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTIme;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void draw() {if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 26, 128, 0));
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(45);
            canvas.drawText("FPS : " + fps, 20, 40, paint);
            canvas.drawText("Pos : " + getScreenWidth(), 20, 100, paint);
            canvas.drawBitmap(bitmapMario, marioXPosition, 200, paint);
            ourHolder.unlockCanvasAndPost(canvas);
        }
        }

        public void update() {
            if (isMoving) {
                if(forward){
                    if(marioXPosition >= (getScreenWidth()-60)){
                        forward = false;
                    }
                    else{
                        marioXPosition = marioXPosition + (walkSpeedPerSecond / fps);
                    }
                }
                else{
                    if(marioXPosition<=10){
                        forward = true;
                    }
                    else{
                        marioXPosition = marioXPosition- (walkSpeedPerSecond/fps);
                    }
                }

        }
    }

            public void pause() {
                playing = false;
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    Log.e("Error", "Joining Thread");
                }
            }

            public void resume() {
                playing = true;
                gameThread = new Thread(this);
                gameThread.start();
            }



            @Override
            public boolean onTouchEvent (MotionEvent motionEvent){
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        isMoving = true;
                        break;

                    case MotionEvent.ACTION_UP:
                        isMoving = false;
                        break;
                }
                return true;
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            gameView.resume();
        }

        @Override
        protected void onPause() {
            super.onPause();
            gameView.pause();
        }
    }
