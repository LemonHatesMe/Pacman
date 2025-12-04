package com.zetcode;

import java.awt.*;

public class Maze {

    // Big tiles → big window
    private final int blockSize = 48;
    private final int nBlocks   = 15;

    // 0 = empty, 1 = wall, 2 = dot, 3 = power pellet
    // 15x15 grid
    private final int[][] levelData = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,3,2,2,2,1,2,2,2,1,2,2,2,3,1},
            {1,2,1,2,2,1,2,1,2,1,2,2,1,2,1},
            {1,2,1,2,2,2,2,1,2,2,2,2,1,2,1},
            {1,2,1,1,1,1,2,1,2,1,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,1,2,1,1,1,2,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,1,2,1,1,1,2,1,1,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,1,1,1,2,1,2,1,1,1,1,2,1},
            {1,2,1,2,2,2,2,1,2,2,2,2,1,2,1},
            {1,2,1,2,2,1,2,1,2,1,2,2,1,2,1},
            {1,3,2,2,2,1,2,2,2,1,2,2,2,3,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // mutable copy for dots & pellets
    private final int[][] screenData = new int[nBlocks][nBlocks];

    public Maze() {
        resetDots();
    }

    public void resetDots() {
        for (int y = 0; y < nBlocks; y++) {
            for (int x = 0; x < nBlocks; x++) {
                screenData[y][x] = levelData[y][x];
            }
        }
    }

    // ---- game logic helpers ----

    public boolean isWall(int gx, int gy) {
        if (gx < 0 || gy < 0 || gx >= nBlocks || gy >= nBlocks) return true;
        return levelData[gy][gx] == 1;
    }

    public boolean eatDotAt(int gx, int gy) {
        if (gx < 0 || gy < 0 || gx >= nBlocks || gy >= nBlocks) return false;
        if (screenData[gy][gx] == 2) {
            screenData[gy][gx] = 0;
            return true;
        }
        return false;
    }

    public boolean eatPowerAt(int gx, int gy) {
        if (gx < 0 || gy < 0 || gx >= nBlocks || gy >= nBlocks) return false;
        if (screenData[gy][gx] == 3) {
            screenData[gy][gx] = 0;
            return true;
        }
        return false;
    }
    public boolean hasDot(int x, int y){
        return screenData[y][x] == 2;
    }
    public boolean hasPower(int x, int y){
        return screenData[y][x] == 3;
    }

    public int getBlockSize() { return blockSize; }
    public int getNBlocks()   { return nBlocks;   }

    // ---- neon drawing ----

    public void draw(Graphics2D g2d) {

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int bs = blockSize;

        for (int y = 0; y < nBlocks; y++) {
            for (int x = 0; x < nBlocks; x++) {

                int cell = levelData[y][x];
                int screenCell = screenData[y][x];

                int px = x * bs;
                int py = y * bs;

                // background tile
                g2d.setColor(new Color(8, 8, 20));
                g2d.fillRect(px, py, bs, bs);

                // walls = neon rounded rectangles
                if (cell == 1) {
                    // outer glow
                    g2d.setColor(new Color(0, 200, 255, 80));
                    g2d.fillRoundRect(px + 4, py + 4, bs - 8, bs - 8, 24, 24);

                    // inner bright stroke
                    g2d.setColor(new Color(0, 255, 255));
                    g2d.setStroke(new BasicStroke(4));
                    g2d.drawRoundRect(px + 6, py + 6, bs - 12, bs - 12, 20, 20);
                }

                // dots
                if (screenCell == 2) {
                    int r = 8;
                    g2d.setColor(new Color(255, 255, 150, 140));
                    g2d.fillOval(px + bs/2 - r, py + bs/2 - r, r*2, r*2);
                    g2d.setColor(new Color(255, 255, 230));
                    g2d.fillOval(px + bs/2 - r/2, py + bs/2 - r/2, r, r);
                }

                // power pellets
                if (screenCell == 3) {
                    int r = 14;
                    g2d.setColor(new Color(255, 120, 255, 150));
                    g2d.fillOval(px + bs/2 - r, py + bs/2 - r, r*2, r*2);
                    g2d.setColor(new Color(255, 220, 255));
                    g2d.fillOval(px + bs/2 - r/2, py + bs/2 - r/2, r, r);
                }
            }
        }
    }
}
