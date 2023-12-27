package com.example.gamecolors;

import android.graphics.Color;
import android.graphics.Point;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private int x, y;
    private int dx, dy;
    private int speed = 20;
    private boolean isActive = false;
    private List<Point> tail;
    public static final int OBJECT_SIZE = 100; // Rozmiar głównego obiektu
    private int bodyColor;

    public GameObject(int startX, int startY) {
        x = startX;
        y = startY;
        tail = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tail.add(new Point(x, y)); // Ustawienie segmentów ogona w pozycji początkowej
        }
        bodyColor = Color.BLUE;
    }

    public void move(int screenWidth, int screenHeight) {
        if (!isActive) {
            return;
        }

        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float normDx = (distance != 0) ? (dx / distance) : 0;
        float normDy = (distance != 0) ? (dy / distance) : 0;

        x += normDx * speed;
        y += normDy * speed;

        updateTail(screenWidth,screenHeight);
        checkBounds(screenWidth, screenHeight);
    }

    private void updateTail(int screenWidth, int screenHeight) {
        // Aktualizacja reszty segmentów ogona
        for (int i = tail.size() - 1; i > 0; i--) {
            Point prevSegment = tail.get(i - 1);
            Point segment = tail.get(i);
            segment.set(prevSegment.x, prevSegment.y);
        }

        // Aktualizacja pierwszego segmentu, aby pasował do pozycji głowy
        if (!tail.isEmpty()) {
            tail.get(0).set(x, y);
        }
    }




    private void checkBounds(int screenWidth, int screenHeight) {
        if (x > screenWidth) x = 0;
        else if (x < 0) x = screenWidth;
        if (y > screenHeight) y = 0;
        else if (y < 0) y = screenHeight;
    }

    public void setDirection(int targetX, int targetY) {
        isActive = true;
        float distance = (float) Math.sqrt((targetX - x) * (targetX - x) + (targetY - y) * (targetY - y));
        dx = (distance != 0) ? (int) ((targetX - x) / distance * speed) : 0;
        dy = (distance != 0) ? (int) ((targetY - y) / distance * speed) : 0;

    }

    public boolean checkCollisionWith(Collectible collectible) {
        int distance = (int) Math.sqrt(Math.pow(x - collectible.getX(), 2) + Math.pow(y - collectible.getY(), 2));
        return distance < OBJECT_SIZE / 2 + 20; // 20 to promień obiektu do zebrania
    }


    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public List<Point> getTail() { return tail; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public void grow(int additionalSegments) {
        for (int i = 0; i < additionalSegments; i++) {
            // Dodajemy nowe segmenty na końcu ogona
            tail.add(new Point(x, y));
        }
    }

    public void increaseSpeed(float increment) {
        speed += increment;
    }

    public void changeColor(int newColor) {
            bodyColor = newColor;
        }

    public int getBodyColor() {
        return bodyColor;
    }

    public boolean checkCollisionWithRock(List<Rock> rocks) {
        for (Rock rock : rocks) {
            int rockCenterX = rock.getX() + rock.getWidth() / 2;
            int rockCenterY = rock.getY() + rock.getHeight() / 2;
            int distance = (int) Math.sqrt(Math.pow(x - rockCenterX, 2) + Math.pow(y - rockCenterY, 2));

            if (distance < OBJECT_SIZE / 2 + Math.max(rock.getWidth(), rock.getHeight()) / 2) {
                return true;
            }
        }
        return false;
    }





}
