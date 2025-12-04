package com.zetcode;

import java.awt.*;

public class PacManPlayer {

    private int x, y;                 // pixel position
    private int dx = 0, dy = 0;       // current direction
    private int requestedDx = 0, requestedDy = 0;  // requested direction
    private final int speed = 8;      // speed in pixels per frame
    private final Maze maze;
    private final int blockSize;

    public PacManPlayer(int startTileX, int startTileY, Maze maze) {
        this.maze = maze;
        this.blockSize = maze.getBlockSize();
        this.x = startTileX * blockSize;
        this.y = startTileY * blockSize;
    }

    /** Main update loop for Pac-Man */
    public void update() {

        // Try to apply requested direction first
        if (canMove(requestedDx, requestedDy)) {
            dx = requestedDx;
            dy = requestedDy;
        }

        // Move in the current direction if possible
        if (canMove(dx, dy)) {
            x += dx * speed;
            y += dy * speed;
        }
    }

    /** Check if Pac-Man can move toward a direction */
    private boolean canMove(int dirX, int dirY) {
        if (dirX == 0 && dirY == 0) return false;

        int nextX = x + dirX * speed;
        int nextY = y + dirY * speed;

        int gx = (nextX + blockSize / 2) / blockSize;
        int gy = (nextY + blockSize / 2) / blockSize;

        return !maze.isWall(gx, gy);
    }

    /** Request a new movement direction */
    public void requestDir(int dirX, int dirY) {
        this.requestedDx = dirX;
        this.requestedDy = dirY;
    }

    /** Draw Pac-Man (simple neon circle, ready for mouth animation later) */
    public void draw(Graphics2D g2d) {
        int size = blockSize;

        // body glow
        g2d.setColor(new Color(255, 255, 0, 120));
        g2d.fillOval(x - 3, y - 3, size + 6, size + 6);

        // solid body
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(x, y, size, size);
    }

    // =============== GETTERS ===============

    /** Current grid X tile */
    public int getGridX() {
        return (x + blockSize / 2) / blockSize;
    }

    /** Current grid Y tile */
    public int getGridY() {
        return (y + blockSize / 2) / blockSize;
    }

    /** Movement direction for ghost prediction */
    public int getDx() { return dx; }
    public int getDy() { return dy; }

    /** Reset position (used on restart) */
    public void setPosition(int tileX, int tileY) {
        this.x = tileX * blockSize;
        this.y = tileY * blockSize;
        dx = dy = 0;
        requestedDx = requestedDy = 0;
    }
}
