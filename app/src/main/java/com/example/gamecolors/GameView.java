package com.example.gamecolors;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int SOME_MINIMUM_DISTANCE = 500;
    private static final int MIN_DISTANCE_BETWEEN_ROCKS = 500; // Minimalna odległość między kamieniami

    private Thread gameThread;
    private boolean isPlaying;
    private GameObject gameObject;
    private Paint paint;
    private int screenWidth, screenHeight;
    private Bitmap background;
    private List<Rock> rocks;
    private int score = 0;
    private List<Collectible> collectibles;
    private boolean gameEnded = false;
    private boolean gameOver = false;
    private ExecutorService executorService;
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        paint = new Paint();
        // Ustawienie początkowej pozycji obiektu w centrum ekranu
        gameObject = new GameObject(screenHeight/2,screenWidth/2);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.grass03);

        collectibles = new ArrayList<>();
        rocks = new ArrayList<>();

        executorService = Executors.newSingleThreadExecutor();




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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;

        // Inicjalizacja GameObject z odpowiednimi wartościami startowymi
        gameObject = new GameObject(screenWidth/2,screenHeight/2);

        collectibles.clear(); // Wyczyść obecną listę, jeśli istnieje

        rocks.clear();
        initializeRocks(10);

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    private void initializeRocks(int count) {
        int gridSize = 4; // Rozmiar siatki, np. 4x4
        int cellWidth = screenWidth / gridSize;
        int cellHeight = screenHeight / gridSize;

        while (rocks.size() < count) {
            int x = (int) (Math.random() * gridSize);
            int y = (int) (Math.random() * gridSize);
            int rockX = x * cellWidth + (int) (Math.random() * cellWidth);
            int rockY = y * cellHeight + (int) (Math.random() * cellHeight);

            // Sprawdzanie, czy kamień nie nakłada się na inny
            boolean canPlaceRock = true;
            for (Rock rock : rocks) {
                if (isTooClose(rock.getX(), rock.getY(), rockX, rockY, MIN_DISTANCE_BETWEEN_ROCKS)) {
                    canPlaceRock = false;
                    break;
                }
            }

            if (canPlaceRock) {
                rocks.add(new Rock(rockX, rockY, getResources()));
            }
        }
    }





    private boolean isTooClose(int x1, int y1, int x2, int y2, int minimumDistance) {
        int distanceX = Math.abs(x1 - x2);
        int distanceY = Math.abs(y1 - y2);
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance < minimumDistance;
    }


    @Override
    public void run() {
        while (isPlaying && !gameOver) {
            if (gameObject.isActive()) {
                update();
            }
            draw();
            sleep();

            if (Math.random() < 0.01) {
                generateRandomCollectible();
            }
        }
    }

    private void update() {
        gameObject.move(screenWidth, screenHeight);

        if (gameObject.checkCollisionWithRock(rocks)) {
            resetGame();
            return; // Zatrzymaj dalsze aktualizacje, ponieważ gra została zresetowana
        }

        Iterator<Collectible> iterator = collectibles.iterator();
        while (iterator.hasNext()) {
            Collectible collectible = iterator.next();
            if (gameObject.checkCollisionWith(collectible)) {
                gameObject.grow(1);
                gameObject.increaseSpeed(1);
                gameObject.changeColor(collectible.getColor());
                iterator.remove();
                score += 1; //  zwiększ wynik o 1 punktów
            }
        }
    }
    private void resetGame() {
        gameOver = true;
        Context context = getContext();
        Intent intent = new Intent(context, MainMenuActivity.class);
        intent.putExtra("score",score);
        context.startActivity(intent);
        saveScore();

        gameObject = new GameObject(screenWidth / 2, screenHeight / 2);
        collectibles.clear();
        rocks.clear();
        initializeRocks(10);
    }


    private void drawScore(Canvas canvas) {
        int textSize = 100;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); // Możesz wybrać inną czcionkę
        paint.setAntiAlias(true);

        String scoreText = "Wynik: " + score;
        float textWidth = paint.measureText(scoreText);
        int x = (screenWidth - (int) textWidth) / 2; // Centrowanie tekstu
        int y = screenHeight - 50; // Umieszczenie tekstu na dole ekranu

        // Opcjonalnie: Dodaj tło dla tekstu
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK); // lub inny kolor
        backgroundPaint.setAlpha(80); // Lekka przezroczystość
        canvas.drawRect(x - 10, y - textSize, x + textWidth + 10, y + 20, backgroundPaint);

        // Rysowanie tekstu
        canvas.drawText(scoreText, x, y, paint);
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();

            canvas.drawBitmap(Bitmap.createScaledBitmap(background, screenWidth, screenHeight, false), 0, 0, null);

            // Draw the body
            paint.setColor(gameObject.getBodyColor());
            paint.setColor(gameObject.getBodyColor());
            for (Point segment : gameObject.getTail()) {
                float left = segment.x - GameObject.OBJECT_SIZE / 2;
                float top = segment.y - GameObject.OBJECT_SIZE / 2;
                float right = segment.x + GameObject.OBJECT_SIZE / 2;
                float bottom = segment.y + GameObject.OBJECT_SIZE / 2;

                canvas.drawRect(left, top, right, bottom, paint);
            }

            // Draw the head
            paint.setColor(gameObject.getBodyColor()); // Head color
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

            for (Collectible collectible : collectibles) {
                collectible.draw(canvas);
            }

            for(Rock rock : rocks){
                rock.draw(canvas);
            }


            drawScore(canvas);
            drawGameOver(canvas);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void drawGameOver(Canvas canvas) {
        if (gameOver) {
            String gameOverText = "Gra zakończona";
            String scoreText = "Wynik: " + score;
            int textSize = 50;
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);

            // Wyświetlanie tekstu na górze ekranu
            int yPosText = textSize + 20;
            canvas.drawText(gameOverText, screenWidth / 2, yPosText, paint);
            canvas.drawText(scoreText, screenWidth / 2, yPosText + textSize + 10, paint);

            // Tu możesz dodać przycisk lub obszar dotykowy do powrotu do menu głównego
        }
    }
    private void saveScore() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getContext().getSharedPreferences("game_scores", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                // Uzyskanie aktualnej daty i czasu
                String currentDateTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                // Pobranie aktualnej historii wyników
                String scores = prefs.getString("scores", "");

                // Dodanie nowego wyniku z datą i czasem
                scores = currentDateTime + ": " + score + ";" + scores;

                // Przechowywanie tylko ostatnich X wyników
                String[] savedScores = scores.split(";");
                if (savedScores.length > 10) {
                    scores = TextUtils.join(";", Arrays.copyOfRange(savedScores, 0, 10));
                }

                editor.putString("scores", scores);
                editor.apply();
            }
        });
    }







    private void generateRandomCollectible() {
        int margin = Math.min(screenWidth, screenHeight) / 20; // Na przykład 10% marginesu
        int maxAttempts = 50; // Maksymalna liczba prób znalezienia odpowiedniej lokalizacji
        boolean positionOK;
        int attempts = 0;

        do {
            positionOK = true;
            int randomX = margin + (int) (Math.random() * (screenWidth - 2 * margin));
            int randomY = margin + (int) (Math.random() * (screenHeight - 2 * margin));
            Collectible newCollectible = new Collectible(randomX, randomY, getRandomColor(), getResources());

            // Sprawdź odległość od innych collectibles
            for (Collectible collectible : collectibles) {
                if (isTooClose(newCollectible.getX(), newCollectible.getY(), collectible.getX(), collectible.getY(), SOME_MINIMUM_DISTANCE)) {
                    positionOK = false;
                    break;
                }
            }

            // Sprawdź odległość od kamieni
            for (Rock rock : rocks) {
                if (isTooClose(newCollectible.getX(), newCollectible.getY(), rock.getX(), rock.getY(), MIN_DISTANCE_BETWEEN_ROCKS)) {
                    positionOK = false;
                    break;
                }
            }

            if (positionOK) {
                collectibles.add(newCollectible);
            }

            attempts++;
        } while (!positionOK && attempts < maxAttempts);
    }



    private int getRandomColor() {
        int[] colors = {Color.RED, Color.YELLOW, Color.MAGENTA}; // Fioletowy jako Color.MAGENTA
        return colors[(int) (Math.random() * colors.length)];
    }


    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver && event.getAction() == MotionEvent.ACTION_DOWN ) {
            Context context = getContext();
            Intent intent = new Intent(context, MainMenuActivity.class);
            context.startActivity(intent);
            return true;
        }

            int touchX = (int) event.getX();
            int touchY = (int) event.getY();

            // Pobierz obecną pozycję obiektu
            int objectX = gameObject.getX();
            int objectY = gameObject.getY();

            // Oblicz różnice między pozycją dotknięcia a obecną pozycją obiektu
            int deltaX = touchX - objectX;
            int deltaY = touchY - objectY;

            // Ustal kierunek na podstawie większej różnicy
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                // Poruszanie się w poziomie
                if (deltaX > 0) {
                    gameObject.setDirection(screenWidth, objectY); // Prawo
                } else {
                    gameObject.setDirection(0, objectY); // Lewo
                }
            } else {
                // Poruszanie się w pionie
                if (deltaY > 0) {
                    gameObject.setDirection(objectX, screenHeight); // Dół
                } else {
                    gameObject.setDirection(objectX, 0); // Góra
                }
            }

        return true;
    }
}