package com.example.gamecolors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread gameThread;
    private boolean isPlaying;
    private GameObject gameObject;
    private Paint paint;
    private int screenWidth, screenHeight;
    private Bitmap background;
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        paint = new Paint();
        // Ustawienie początkowej pozycji obiektu w centrum ekranu
        gameObject = new GameObject(screenHeight/2,screenWidth/2);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.grass03);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;

        // Inicjalizacja GameObject z odpowiednimi wartościami startowymi
        gameObject = new GameObject(screenWidth/2,screenHeight/2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
        gameObject.setX(screenWidth / 2);
        gameObject.setY(screenHeight / 2);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (gameObject.isActive()) {
                update();
            }
            draw();
            sleep();
        }
    }

    private void update() {
        gameObject.move(screenWidth, screenHeight);
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false), 0, 0, null);

            // Draw the body
            paint.setColor(Color.BLUE);
            for (Point segment : gameObject.getTail()) {
                canvas.drawCircle(segment.x, segment.y, GameObject.OBJECT_SIZE / 2, paint);
            }

            // Draw the head
            paint.setColor(Color.BLUE); // Head color
            canvas.drawCircle(gameObject.getX(), gameObject.getY(), GameObject.OBJECT_SIZE / 2, paint);

            // Draw the eyes
            paint.setColor(Color.WHITE); // Eye color
            float eyeRadius = GameObject.OBJECT_SIZE / 8; // Size of the eyes
            float eyeOffsetX = GameObject.OBJECT_SIZE / 4; // Horizontal offset for eyes
            float eyeOffsetY = GameObject.OBJECT_SIZE / 6; // Vertical offset for eyes

            canvas.drawCircle(gameObject.getX() + eyeOffsetX, gameObject.getY() - eyeOffsetY, eyeRadius, paint);
            canvas.drawCircle(gameObject.getX() - eyeOffsetX, gameObject.getY() - eyeOffsetY, eyeRadius, paint);

            paint.setColor(Color.BLACK); // Eye color
            float eyeRadius1 = GameObject.OBJECT_SIZE / 20; // Size of the eyes
            float eyeOffsetX1 = GameObject.OBJECT_SIZE / 4; // Horizontal offset for eyes
            float eyeOffsetY1 = GameObject.OBJECT_SIZE / 6; // Vertical offset for eyes

            canvas.drawCircle(gameObject.getX() + eyeOffsetX1, gameObject.getY() - eyeOffsetY1, eyeRadius1, paint);
            canvas.drawCircle(gameObject.getX() - eyeOffsetX1, gameObject.getY() - eyeOffsetY1, eyeRadius1, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }



    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            gameObject.setDirection(touchX, touchY);
        }
        return true;
    }
}
