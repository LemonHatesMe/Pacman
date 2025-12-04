package com.zetcode;

import java.awt.*;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Pinky extends Ghost {

    public Pinky(Maze maze, PacManPlayer pacman) {
        super(maze, pacman, 6, 7, Color.PINK);
    }

    @Override
    public void chaseMove() {

        int sx = getGridX();
        int sy = getGridY();

        int px = pacman.getGridX();
        int py = pacman.getGridY();
        int pdx = pacman.getDx();
        int pdy = pacman.getDy();

        int tx = px + pdx * 2;
        int ty = py + pdy * 2;

        int N = maze.getNBlocks();
        tx = Math.max(0, Math.min(N - 1, tx));
        ty = Math.max(0, Math.min(N - 1, ty));

        Node step = aStarNextStep(sx, sy, tx, ty);

        if (step != null) {
            dx = Integer.compare(step.x, sx);
            dy = Integer.compare(step.y, sy);
        } else {
            dx = dy = 0;
        }
    }

    /** A* search from (sx,sy) to (tx,ty) – returns first step. */
    private Node aStarNextStep(int sx, int sy, int tx, int ty) {

        int N = maze.getNBlocks();
        boolean[][] closed = new boolean[N][N];

        PriorityQueue<Node> open =
                new PriorityQueue<>(Comparator.comparingInt(Node::f));

        int[] dxs = {1,-1,0,0};
        int[] dys = {0,0,1,-1};

        Node start = new Node(sx, sy);
        start.g = 0;
        start.h = Math.abs(tx - sx) + Math.abs(ty - sy);
        open.add(start);

        while (!open.isEmpty()) {
            Node cur = open.poll();

            if (closed[cur.y][cur.x]) continue;
            closed[cur.y][cur.x] = true;

            if (cur.x == tx && cur.y == ty) {
                while (cur.parent != null && cur.parent.parent != null) {
                    cur = cur.parent;
                }
                return cur;
            }

            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dxs[i];
                int ny = cur.y + dys[i];

                if (nx < 0 || ny < 0 || nx >= N || ny >= N)
                    continue;
                if (maze.isWall(nx, ny))
                    continue;
                if (closed[ny][nx])
                    continue;

                Node next = new Node(nx, ny);
                next.parent = cur;
                next.g = cur.g + 1;
                next.h = Math.abs(tx - nx) + Math.abs(ty - ny);
                open.add(next);
            }
        }

        return null;
    }
}
