package com.zetcode;

public class Node {
    public int x, y;
    public Node parent;
    public int g, h; // for A*

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int f() { // A* total cost
        return g + h;
    }
}
