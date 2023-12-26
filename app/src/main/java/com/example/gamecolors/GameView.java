package com.example.gamecolors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread = null;
    private boolean isPlaying;
    private GameObject gameObject;
    private Paint paint;

    private int speed = 10;

    public GameView(Context context) {
        super(context);
        gameObject = new GameObject();
        paint = new Paint();
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        // Logika aktualizacji obiektu
        gameObject.move();
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.WHITE);

            paint.setColor(gameObject.getColor());
            // Rysowanie koła jako przykład
            canvas.drawCircle(gameObject.getX(), gameObject.getY(), 25, paint); // 25 to promień koła

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17); // około 60 klatek na sekundę
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
        Log.d("GameView", "Game resumed");
    }

    public void pause() {
        try {
            isPlaying = false;
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();

            // Oblicz różnicę między pozycją dotyku a obecną pozycją obiektu
            int dx = touchX - gameObject.getX();
            int dy = touchY - gameObject.getY();

            // Normalizuj różnicę do jednolitego kierunku ruchu
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float directionX = (distance != 0) ? (dx / distance) * speed : 0;
            float directionY = (distance != 0) ? (dy / distance) * speed : 0;

            gameObject.setDirection((int) directionX, (int) directionY);
        }
        return true;

    }
}
