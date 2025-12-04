/*
 * Project: Pac-Man Ghosts — assignment implementation
 * Authors: <Amanda Balta> and <Lailani Garcia>
 *
 * Starting code (base Pac-Man engine): <https://github.com/janbodnar/Java-Pacman-Game/blob/master/src/com/zetcode/Board.java>
 *
 * Paper (algorithm source):
 * Novikov, A.; Yakovlev, S.; Gushchin, I. "Exploring the Possibilities of MADDPG for UAV Swarm Control
 * by Simulating in Pac-Man Environment." Radioelectronic and Computer Systems, 2025. (uploaded PDF).
 * (Use instructor-provided pacman ghosts.pdf). :contentReference[oaicite:2]{index=2}
 *
 * Additional references:
 * - "Understanding Pac-Man Ghost Behavior" (GameInternals) — for classic ghost behaviors.
 *
 */
package com.zetcode;

import javax.swing.*;
import java.awt.*;

public class Pacman extends JFrame {

    public Pacman() {
        initUI();
    }

    private void initUI() {
        add(new Board());
        setTitle("Pac-Man");
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Pacman ex = new Pacman();
            ex.setVisible(true);
        });
    }
}
