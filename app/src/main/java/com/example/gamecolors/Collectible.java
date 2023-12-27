package com.example.gamecolors;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.example.gamecolors.R;

public class Collectible {
    private int x, y;
    private Bitmap image;
    private static final int RADIUS = 100; // Rozmiar obiektu zbieranego

    private int color;

    public Collectible(int x, int y, int color, Resources res) {
        this.x = x;
        this.y = y;
        this.color = color;
        setImageForColor(color, res);
    }

    private void setImageForColor(int color, Resources res) {
        switch (color) {
            case Color.RED:
                image = BitmapFactory.decodeResource(res, R.drawable.apple);
                break;
            case Color.YELLOW:
                image = BitmapFactory.decodeResource(res, R.drawable.banana);
                break;
            case Color.MAGENTA:
                image = BitmapFactory.decodeResource(res, R.drawable.grape);
                break;
        }
    }
    public int getColor() {
        return color;
    }
    public void draw(Canvas canvas) {
        canvas.drawBitmap(Bitmap.createScaledBitmap(image, 2 * RADIUS, 2 * RADIUS, false), x - RADIUS, y - RADIUS, null);
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
}
