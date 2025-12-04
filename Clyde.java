package com.zetcode;

import java.awt.*;

public class Clyde extends Ghost {

    //corner left where Clyde usually runs to (Lailani)
    private final int conerX = 1;
    private final int conerY = 13;

    public Clyde(Maze maze, PacManPlayer pacman) {
        super(maze, pacman, 7, 9, new Color(255,165,0));
    }

    @Override
    public void chaseMove() {

        int sx = getGridX();
        int sy = getGridY();
        int px = pacman.getGridX();
        int py = pacman.getGridY();

        int dist = Math.abs(px - sx) + Math.abs(py - sy);

        int tx, ty;

        if (dist <= 8) { //changed the threshold(Lailani)
            // run away
            tx = cornerX;
            ty = cornerY;
        } else {
            // chase
            tx = px;
            ty = py;
        }

        Node step = bfsNextStep(sx, sy, tx, ty);

        if (step != null) {
            dx = Integer.compare(step.x, sx);
            dy = Integer.compare(step.y, sy);
        } else {
            chooseAnyLegalDirection();
        }
    }
}
