/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game_project;

/**
 *
 * @author THAN
 */
public class Tile {
    private boolean isLava;

    public Tile(boolean isLava) {
        this.isLava = isLava;
    }

    public boolean isLava() {
        return isLava;
    }
}

class Board {
    private Tile[][] tiles;
    private int size;

    public Board(int size, Character player, Character enemy) {
        this.size = size;
        tiles = new Tile[size][size];

        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                
                if ((i == player.getX() && j == player.getY()) || (i == enemy.getX() && j == enemy.getY())) {
                    tiles[i][j] = new Tile(false); 
                } else {
                    tiles[i][j] = new Tile(Math.random() < 0.1); 
                }
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }
}