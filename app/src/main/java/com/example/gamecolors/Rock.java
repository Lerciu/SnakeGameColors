package com.example.gamecolors;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.res.Resources;

public class Rock {
    private int x, y;
    private Bitmap image;

    public Rock(int x, int y, Resources res) {
        this.x = x;
        this.y = y;
        image = BitmapFactory.decodeResource(res, R.drawable.rock2); // Przyjmujemy, że obraz kamienia to 'rock.png'
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }

    // Gettery
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }


}
