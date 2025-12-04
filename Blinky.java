package com.zetcode;

import java.awt.*;

public class Blinky extends Ghost {

    public Blinky(Maze maze, PacManPlayer pacman) {
        super(maze, pacman, 7, 7, Color.RED);
    }

    @Override
    public void chaseMove() {

        int sx = getGridX();
        int sy = getGridY();
        int tx = pacman.getGridX();
        int ty = pacman.getGridY();

        Node step = bfsNextStep(sx, sy, tx, ty);

        if (step != null) {
            dx = Integer.compare(step.x, sx);
            dy = Integer.compare(step.y, sy);
        } else {
            dx = dy = 0;
        }
    }
}
