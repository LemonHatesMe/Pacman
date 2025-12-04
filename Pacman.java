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
