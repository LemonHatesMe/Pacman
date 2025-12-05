package com.zetcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Board extends JPanel implements ActionListener {

    private Maze maze;
    private PacManPlayer pacman;
    private List<Ghost> ghosts;

    private javax.swing.Timer timer;
    private boolean inGame = true;

    private int score = 0;

    // frightened mode
    private int frightenedTimer = 0;
    private final int FRIGHTENED_DURATION = 60; // I changed it so the ghosts stay a shorter time in frightened mode and make the game a little more difficult

    // ghost pacing
    private int ghostStepCounter = 0;

    public Board() {
        initBoard();
    }

    private void initBoard() {

        maze = new Maze();

        int bs = maze.getBlockSize();
        int size = maze.getNBlocks() * bs;

        setPreferredSize(new Dimension(size, size + 40));
        setBackground(Color.BLACK);
        setFocusable(true);

        pacman = new PacManPlayer(7, 13, maze);

        ghosts = new ArrayList<>();
        Blinky blinky = new Blinky (maze, pacman);
        ghosts.add(blinky);
        ghosts.add(new Pinky(maze, pacman));
        ghosts.add(new Inky(maze, pacman, blinky));
        ghosts.add(new Clyde(maze, pacman));

        addKeyListener(new TAdapter());

        timer = new javax.swing.Timer(40, this); // ~25 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) stepGame();
        repaint();
    }

    // ----------------------------------------------------
    // MAIN GAME LOGIC LOOP
    // ----------------------------------------------------
    private void stepGame() {

        pacman.update();

        int px = pacman.getGridX();
        int py = pacman.getGridY();

        // dots
        if (maze.eatDotAt(px, py)) score += 10;

        // power pellets
        checkPowerPellet(px, py);

        // ghost movement pacing
        ghostStepCounter++;
        if (ghostStepCounter % 2 == 0) {

            // ***** FIXED: ONLY CALL update() — NOT tickScared() *****
            for (Ghost g : ghosts) {
                g.update();   // handles chase, scared, and eaten logic
            }
        }

        checkCollisions();
        checkWin();
    }

    // ----------------------------------------------------
    // POWER PELLET HANDLING
    // ----------------------------------------------------
    private void checkPowerPellet(int px, int py) {

        if (maze.eatPowerAt(px, py)) {

            frightenedTimer = FRIGHTENED_DURATION;

            for (Ghost g : ghosts) {
                g.setScared(FRIGHTENED_DURATION);
            }
        }
    }

    // ----------------------------------------------------
    // COLLISIONS WITH GHOSTS
    // ----------------------------------------------------
    private void checkCollisions() {

        int px = pacman.getGridX();
        int py = pacman.getGridY();

        for (Ghost g : ghosts) {

            if (g.getGridX() == px && g.getGridY() == py) {

                // Pac-Man eats ghost
                if (g.isScared() && !g.eaten) {
                    score += 200;
                    g.eatenByPacman();
                    continue;
                }

                // Ghost kills Pac-Man
                if (!g.isScared() && !g.eaten) {
                    inGame = false;
                    return;
                }
            }
        }
    }

    // ----------------------------------------------------
    // YOU WIN CONDITION
    // ----------------------------------------------------
    private void checkWin() {
        for (int y = 0; y < maze.getNBlocks(); y++) {
            for (int x = 0; x < maze.getNBlocks(); x++) {
                if (maze.hasDot(x, y) || maze.hasPower(x, y)) {
                    return; // still dots left
                }
            }
        }

        inGame = false;
        JOptionPane.showMessageDialog(this, "🎉 YOU WIN! 🎉",
                "Victory", JOptionPane.INFORMATION_MESSAGE);
    }

    // ----------------------------------------------------
    // DRAW EVERYTHING
    // ----------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        maze.draw(g2d);
        pacman.draw(g2d);

        for (Ghost ghost : ghosts) ghost.draw(g2d);

        drawScore(g2d);

        if (!inGame) drawGameOver(g2d);
    }

    private void drawScore(Graphics2D g2d) {

        int y = maze.getNBlocks() * maze.getBlockSize() + 30;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20));

        String text = "SCORE: " + score;

        int w = g2d.getFontMetrics().stringWidth(text);
        int x = (getWidth() - w) / 2;

        g2d.drawString(text, x, y);
    }

    private void drawGameOver(Graphics2D g2d) {

        String msg = "Game Over - Press R to Restart";
        g2d.setFont(new Font("Helvetica", Font.BOLD, 22));

        int w = g2d.getFontMetrics().stringWidth(msg);
        int x = (getWidth() - w) / 2;
        int y = getHeight() / 2;

        g2d.setColor(Color.RED);
        g2d.drawString(msg, x, y);
    }

    // ----------------------------------------------------
    // RESTART
    // ----------------------------------------------------
    private void restart() {
        maze.resetDots();
        score = 0;
        pacman.setPosition(7, 13);

        ghosts.clear();

        // MUST recreate Blinky first so Inky can reference it
        Blinky blinky = new Blinky(maze, pacman);
        ghosts.add(blinky);

        ghosts.add(new Pinky(maze, pacman));
        ghosts.add(new Inky(maze, pacman, blinky));  // ✔ FIXED
        ghosts.add(new Clyde(maze, pacman));

        inGame = true;
    }


    // ----------------------------------------------------
    // KEY INPUT
    // ----------------------------------------------------
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT)  pacman.requestDir(-1, 0);
                if (key == KeyEvent.VK_RIGHT) pacman.requestDir(1, 0);
                if (key == KeyEvent.VK_UP)    pacman.requestDir(0, -1);
                if (key == KeyEvent.VK_DOWN)  pacman.requestDir(0, 1);
            }
            else {
                if (key == KeyEvent.VK_R) restart();
            }
        }
    }
}
