package com.example.gamecolors;

import android.graphics.Color;

public class GameObject {
    private int x, y, color;
    private int dx, dy; // Kierunki ruchu
    private int speed = 10;

    public GameObject() {
        x = 0;
        y = 0;
        dx = speed;
        dy = speed;
        color = Color.BLUE;
    }

    public void move() {
        x += dx;
        y += dy;
        // Dodaj logikę sprawdzającą granice ekranu, jeśli to konieczne
    }

    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // Gettery i Settery
    public int getX() { return x; }
    public int getY() { return y; }
    public int getColor() { return color; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setColor(int color) { this.color = color; }
}
