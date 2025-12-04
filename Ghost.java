package com.zetcode;

import java.awt.*;
import java.util.Queue;
import java.util.LinkedList;

public abstract class Ghost {

    protected double x, y;          // pixel position
    protected int dx = 0, dy = 0;   // tile movement direction
    protected final Maze maze;
    protected final PacManPlayer pacman;
    protected final Color baseColor;

    protected boolean scared = false;
    public boolean eaten = false;
    protected int scaredTimer = 0;

    // speeds
    protected int speedNormal = 8;
    protected int speedScared = 4;
    protected int speedEyes   = 10;

    protected final int blockSize;

    public Ghost(Maze maze, PacManPlayer pacman, int startTileX, int startTileY, Color color) {
        this.maze = maze;
        this.pacman = pacman;
        this.baseColor = color;
        this.blockSize = maze.getBlockSize();

        this.x = startTileX * blockSize;
        this.y = startTileY * blockSize;
    }

    // ==========================================================
    // MAIN UPDATE
    // ==========================================================
    public void update() {

        if (eaten) {
            moveEyesToHouse();
            return;
        }

        // -------------------------
        // FRIGHTENED MODE
        // -------------------------
        if (scared) {

            tickScared();  // ↓ this now handles "frightened end" correctly

            // If frightened just ended → resume chase immediately
            if (!scared) {
                dx = dy = 0;   // reset previous random direction
                if (isCentered()) {
                    chaseMove();  // FORCE first chase direction
                }
                moveIfCan(speedNormal);
                return;
            }

            // still frightened → random movement
            if (isCentered()) {
                randomScaredMove();
            }

            moveIfCan(speedScared);
            return;
        }

        // -------------------------
        // NORMAL CHASE MODE
        // -------------------------
        if (isCentered()) {
            chaseMove();
        }

        moveIfCan(speedNormal);
    }

    // implemented in Blinky / Pinky / Inky / Clyde
    public abstract void chaseMove();

    protected boolean isCentered() {
        return ((int)x % blockSize == 0) && ((int)y % blockSize == 0);
    }

    // ==========================================================
    // MOVEMENT
    // ==========================================================
    private boolean canMove(int dirX, int dirY, int speed) {
        int nextX = (int)x + dirX * speed;
        int nextY = (int)y + dirY * speed;

        int gx = (nextX + blockSize/2) / blockSize;
        int gy = (nextY + blockSize/2) / blockSize;

        return !maze.isWall(gx, gy);
    }

    protected void moveIfCan(int speed) {
        if (canMove(dx, dy, speed)) {
            x += dx * speed;
            y += dy * speed;
        } else {
            dx = dy = 0; // hit wall → stop
        }
    }

    // ==========================================================
    // FRIGHTENED MODE
    // ==========================================================
    public void setScared(int duration) {
        if (eaten) return;

        scared = true;
        scaredTimer = duration;

        // instant turn-around
        dx = -dx;
        dy = -dy;
    }

    public void tickScared() {

        if (!scared) return;

        scaredTimer--;

        // frightened still active
        if (scaredTimer > 0) return;

        // scared just ENDED → return to NORMAL behavior
        scared = false;

        // snap ghost to tile grid (critical!)
        x = getGridX() * blockSize;
        y = getGridY() * blockSize;

        // IMMEDIATELY pick a new chase direction
        chaseMove();

        // if chaseMove failed (dx,dy = 0), pick ANY legal direction
        if (dx == 0 && dy == 0) {
            chooseAnyLegalDirection();
        }
    }


    private void randomScaredMove() {
        int gx = getGridX();
        int gy = getGridY();

        int[] dxs = {1,-1,0,0};
        int[] dys = {0,0,1,-1};

        java.util.List<int[]> dirs = new java.util.ArrayList<>();

        for (int i = 0; i < 4; i++) {
            int nx = gx + dxs[i];
            int ny = gy + dys[i];

            if (!maze.isWall(nx, ny)) {
                dirs.add(new int[]{dxs[i], dys[i]});
            }
        }

        if (!dirs.isEmpty()) {
            int[] pick = dirs.get((int)(Math.random() * dirs.size()));
            dx = pick[0];
            dy = pick[1];
        }
    }

    // ==========================================================
    // EYES → RETURN HOME
    // ==========================================================
    public void eatenByPacman() {
        eaten = true;
        scared = false;
        dx = dy = 0;
    }

    private void moveEyesToHouse() {
        int homeX = 7, homeY = 7;

        int gx = getGridX();
        int gy = getGridY();

        if (gx == homeX && gy == homeY) {
            eaten = false;
            scared = false;
            dx = dy = 0;
            return;
        }

        if (gx < homeX) x += speedEyes;
        if (gx > homeX) x -= speedEyes;
        if (gy < homeY) y += speedEyes;
        if (gy > homeY) y -= speedEyes;
    }

    // ==========================================================
    // BFS (Blinky & Inky use this)
    // ==========================================================
    protected Node bfsNextStep(int sx, int sy, int tx, int ty) {

        int N = maze.getNBlocks();
        boolean[][] visited = new boolean[N][N];
        Queue<Node> q = new LinkedList<>();

        tx = Math.max(0, Math.min(N-1, tx));
        ty = Math.max(0, Math.min(N-1, ty));

        q.add(new Node(sx, sy));
        visited[sy][sx] = true;

        int[] dxs = {1,-1,0,0};
        int[] dys = {0,0,1,-1};

        while (!q.isEmpty()) {

            Node cur = q.poll();

            if (cur.x == tx && cur.y == ty) {
                while (cur.parent != null && cur.parent.parent != null)
                    cur = cur.parent;
                return cur;
            }

            for (int i = 0; i < 4; i++) {

                int nx = cur.x + dxs[i];
                int ny = cur.y + dys[i];

                if (nx < 0 || ny < 0 || nx >= N || ny >= N) continue;
                if (maze.isWall(nx, ny)) continue;
                if (visited[ny][nx]) continue;

                Node next = new Node(nx, ny);
                next.parent = cur;

                visited[ny][nx] = true;
                q.add(next);
            }
        }

        return null;
    }

    // ==========================================================
    // DRAWING
    // ==========================================================
    public void draw(Graphics2D g) {
        int bs = blockSize;
        int px = (int)x;
        int py = (int)y;

        if (eaten) {
            g.setColor(Color.WHITE);
            g.fillOval(px + 10, py + 10, bs - 20, bs - 20);
            return;
        }

        Color ghostColor = scared ? Color.CYAN : baseColor;

        // flicker effect
        if (scared && scaredTimer < 30 && scaredTimer % 6 < 3) {
            ghostColor = Color.WHITE;
        }

        g.setColor(ghostColor);
        g.fillOval(px + 2, py + 2, bs - 4, bs - 4);

        drawEyes(g);
    }
    protected void chooseAnyLegalDirection() {
        int gx = getGridX();
        int gy = getGridY();

        int[] dxs = {1,-1,0,0};
        int[] dys = {0,0,1,-1};

        for (int i = 0; i < 4; i++) {
            int nx = gx + dxs[i];
            int ny = gy + dys[i];
            if (!maze.isWall(nx, ny)) {
                dx = dxs[i];
                dy = dys[i];
                return;
            }
        }

        // absolute fallback
        dx = dy = 0;
    }


    private void drawEyes(Graphics2D g) {
        int bs = blockSize;
        int px = (int)x;
        int py = (int)y;

        g.setColor(Color.WHITE);
        g.fillOval(px + bs/3, py + bs/3, 12, 12);
        g.fillOval(px + bs/2, py + bs/3, 12, 12);

        g.setColor(Color.BLUE);
        g.fillOval(px + bs/3 + 4, py + bs/3 + 4, 6, 6);
        g.fillOval(px + bs/2 + 4, py + bs/3 + 4, 6, 6);
    }

    // helpers
    public int getGridX() { return (int)(x + blockSize/2) / blockSize; }
    public int getGridY() { return (int)(y + blockSize/2) / blockSize; }

    public boolean isScared() { return scared; }
}
