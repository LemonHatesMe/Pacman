package com.zetcode;

import java.awt.*;
import java.util.Random;

public class Fruit {

    private int gridX, gridY;
    private boolean active = false;
    private final Maze maze;
    private final Random rand = new Random();

    public Fruit(Maze maze) {
        this.maze = maze;
    }

    public void spawnRandom() {
        int n = maze.getNBlocks();
        for (int tries = 0; tries < 100; tries++) {
            int x = rand.nextInt(n);
            int y = rand.nextInt(n);
            if (!maze.isWall(x, y)) {
                gridX = x;
                gridY = y;
                active = true;
                return;
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public boolean tryEat(int px, int py) {
        if (active && px == gridX && py == gridY) {
            active = false;
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g2d) {
        if (!active) return;

        int bs = maze.getBlockSize();
        int px = gridX * bs;
        int py = gridY * bs;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // glow
        g2d.setColor(new Color(255, 80, 80, 150));
        g2d.fillOval(px - 4, py - 4, bs + 8, bs + 8);

        // fruit body (neon pink)
        g2d.setColor(new Color(255, 120, 200));
        g2d.fillOval(px + 4, py + 4, bs - 8, bs - 8);

        // little leaf
        g2d.setColor(new Color(80, 255, 120));
        g2d.fillOval(px + bs/2 - 4, py + 2, 8, 8);
    }
}
