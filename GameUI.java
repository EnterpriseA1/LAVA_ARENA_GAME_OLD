/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.event.*;

/**
 *
 * @author THAN
 */
public class GameUI extends JFrame {

    private Board board;
    private Character player;
    private Character enemy;
    private Random dice;
    private JLabel diceResultLabel;
    private JPanel boardPanel;
    private boolean isPlayerTurn = true;
    private int diceValue;
    private int remainingMoves;
    private JLabel turnIndicatorLabel;

    public GameUI() {

        player = new Character(7, 7, 100);
        player.setDirection("UP");
        enemy = new Character(0, 0, 100);
        enemy.setDirection("DOWN");
        board = new Board(8, player, enemy);
        dice = new Random();

        setTitle("Turn-Based Battle Game");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        turnIndicatorLabel = new JLabel("Player's Turn", SwingConstants.CENTER);
        add(turnIndicatorLabel, BorderLayout.SOUTH);
        JButton rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(new MoveAction());

        diceResultLabel = new JLabel("Dice Result: ", SwingConstants.CENTER);

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(300, 300));

        add(boardPanel, BorderLayout.CENTER);
        add(diceResultLabel, BorderLayout.NORTH);
        add(rollButton, BorderLayout.EAST);

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tileSize = 30;
                int x = e.getY() / tileSize;
                int y = e.getX() / tileSize;

                if (remainingMoves > 0) { // ตรวจสอบว่า remainingMoves มากกว่า 0 ก่อน
                    if (isPlayerTurn) {
                        moveCharacter(player, x, y);
                    } else {
                        moveCharacter(enemy, x, y);
                    }
                }
                else{
                    System.out.println("no move remain");
                }
                if (remainingMoves == 0) {
                    
                    updateTurnIndicator();
                }
                boardPanel.repaint();
            }
        });
        
    }

    private void drawBoard(Graphics g) {
        int tileSize = 30;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getTile(i, j).isLava()) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(j * tileSize, i * tileSize, tileSize, tileSize);
                g.setColor(Color.BLACK);
                g.drawRect(j * tileSize, i * tileSize, tileSize, tileSize);
            }

        }
        //player 
        g.setColor(Color.GREEN);
        g.fillOval(player.getY() * tileSize + 5, player.getX() * tileSize + 5, tileSize - 10, tileSize - 10);
        drawDirectionIndicator(g, player.getDirection(), player.getX(), player.getY(), tileSize);
        //enemy
        g.setColor(Color.WHITE);
        g.fillOval(enemy.getY() * tileSize + 5, enemy.getX() * tileSize + 5, tileSize - 10, tileSize - 10);
        drawDirectionIndicator(g, enemy.getDirection(), enemy.getX(), enemy.getY(), tileSize);
    }

    private void drawDirectionIndicator(Graphics g, String direction, int x, int y, int tileSize) {
        g.setColor(Color.BLACK);
        int centerX = (y * tileSize) + tileSize / 2;
        int centerY = (x * tileSize) + tileSize / 2;

        switch (direction) {
            case "UP":
                g.drawLine(centerX, centerY, centerX, centerY - 10);
                break;
            case "DOWN":
                g.drawLine(centerX, centerY, centerX, centerY + 10);
                break;
            case "LEFT":
                g.drawLine(centerX, centerY, centerX - 10, centerY);
                break;
            case "RIGHT":
                g.drawLine(centerX, centerY, centerX + 10, centerY);
                break;
        }

    }

    /*this move still have problem*/
    private void moveCharacter(Character character, int newX, int newY) {
        int dx = newX - character.getX();
        int dy = newY - character.getY();

        if (remainingMoves > 0 && ((Math.abs(dx) + Math.abs(dy)) == 1)) {
            if (newX >= 0 && newX < board.getSize() && newY >= 0 && newY < board.getSize()) {
                if (!board.getTile(newX, newY).isLava()) {
                    character.setX(newX);
                    character.setY(newY);
                    remainingMoves--;
                } else {
                    System.out.println("Character cannot move into lava!");
                }
            }
        } else if (remainingMoves > 0 && Math.abs(dx) + Math.abs(dy) <= remainingMoves) {

            int stepX = (dx != 0) ? (dx / Math.abs(dx)) : 0;
            int stepY = (dy != 0) ? (dy / Math.abs(dy)) : 0;

            int currentX = character.getX();
            int currentY = character.getY();

            while (remainingMoves > 0 && (currentX != newX || currentY != newY)) {
                if (!board.getTile(currentX + stepX, currentY + stepY).isLava()) {
                    currentX += stepX;
                    currentY += stepY;
                    remainingMoves--;
                } else {
                    System.out.println("Cannot move into lava!");
                    break;
                }
            }

            character.setX(currentX);
            character.setY(currentY);
        } else {
            System.out.println("Invalid move! Remaining moves: " + remainingMoves);
        }
        if(remainingMoves == 0 ){
            isPlayerTurn = !isPlayerTurn;
        }
    }

    private int rollDice() {
        return dice.nextInt(6) + 1;
    }

    private class MoveAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            diceValue = rollDice();
            remainingMoves = diceValue;
            diceResultLabel.setText("Dice Result: " + diceValue);

            boardPanel.repaint();

        }

    }

    private void updateTurnIndicator() {
        if (isPlayerTurn) {
            turnIndicatorLabel.setText("Player's Turn");
        } else {
            turnIndicatorLabel.setText("Enemy's Turn");
        }
    }

    public static void main(String[] args) {
        GameUI gameUI = new GameUI();
        gameUI.setVisible(true);
    }
}
