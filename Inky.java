package com.zetcode;

import java.awt.*;

public class Inky extends Ghost {

    private final Blinky blinky;

    public Inky(Maze maze, PacManPlayer pacman, Blinky blinky) {
        super(maze, pacman, 8, 7, Color.CYAN);
        this.blinky = blinky;
    }

    //fixed a bit of the logic here(Lailani)
    @Override
    public void chaseMove() {

        int sx = getGridX();
        int sy = getGridY();

        int px = pacman.getGridX();
        int py = pacman.getGridY();
        int pdx = pacman.getDx();
        int pdy = pacman.getDy();

        // --------------------------------------------------------
        // 1. REAL PAC-MAN RULE:
        //     Look 2 tiles ahead of Pac-Man in his movement direction
        // --------------------------------------------------------
        int projX = px + pdx * 4;
        int projY = py + pdy * 4;

        // Clamp inside maze
        int N = maze.getNBlocks();
        projX = Math.max(0, Math.min(N - 1, projX));
        projY = Math.max(0, Math.min(N - 1, projY));

        //Calculates if there is a wall, if there is it will fall back into Blinky's algorithm (Lailani)
        if (maze.isWall(tx, ty)){
            tx = px;
            ty = py;
        }
        
        // --------------------------------------------------------
        // 2. Compute vector from Blinky → projection point
        // --------------------------------------------------------
        int bx = blinky.getGridX();
        int by = blinky.getGridY();

        int vecX = projX - bx;
        int vecY = projY - by;

        // --------------------------------------------------------
        // 3. Double the vector to get Inky's true target
        // --------------------------------------------------------
        int tx = projX + vecX;
        int ty = projY + vecY;

        // Clamp inside maze
        int N = maze.getNBlocks(); //prevent from getting stuck (Lailani)
        tx = Math.max(0, Math.min(N - 1, tx));
        ty = Math.max(0, Math.min(N - 1, ty));

        // --------------------------------------------------------
        // 4. Use BFS to choose the next tile toward the target
        // --------------------------------------------------------
        Node step = bfsNextStep(sx, sy, tx, ty);

        if (step != null) {
            dx = Integer.compare(step.x, sx);
            dy = Integer.compare(step.y, sy);
        }
        if (dx == 0 && dy == 0) {
            chooseAnyLegalDirection();
        }
    }
}
