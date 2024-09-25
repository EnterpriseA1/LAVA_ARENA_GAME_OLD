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
    private JPanel mainMenuPanel; // แผงสำหรับหน้าเริ่มต้น
    private JPanel gamePanel; // แผงสำหรับเกม

    public GameUI() {
        setTitle("Turn-Based Battle Game");
        setSize(1080, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        mainMenuPanel = new MainMenu(this); // สร้าง MainMenu
        createGamePanel();

        add(mainMenuPanel, "MainMenu");
        add(gamePanel, "GamePanel");

        showMainMenu();
    }

    private void createGamePanel() {
        player = new Character(7, 7, 100);
        player.setDirection("UP");
        enemy = new Character(0, 0, 100);
        enemy.setDirection("DOWN");
        board = new Board(8, player, enemy);
        dice = new Random();

        turnIndicatorLabel = new JLabel("Player's Turn", SwingConstants.CENTER);
        turnIndicatorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        turnIndicatorLabel.setForeground(Color.BLUE);

        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(Color.LIGHT_GRAY);
        gamePanel.add(turnIndicatorLabel, BorderLayout.SOUTH);

        JButton rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(new MoveAction());
        styleButton(rollButton);

        diceResultLabel = new JLabel("Dice Result: ", SwingConstants.CENTER);
        diceResultLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(400, 400));
        boardPanel.setBackground(Color.WHITE);

        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(diceResultLabel, BorderLayout.NORTH);
        gamePanel.add(rollButton, BorderLayout.EAST);

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tileSize = 80;
                int x = e.getY() / tileSize;
                int y = e.getX() / tileSize;

                if (remainingMoves > 0) { // ตรวจสอบว่า remainingMoves มากกว่า 0 ก่อน
                    if (isPlayerTurn) {
                        moveCharacter(player, x, y);
                    } else {
                        moveCharacter(enemy, x, y);
                    }
                } else {
                    System.out.println("No move remain");
                }
                if (remainingMoves == 0) {
                    updateTurnIndicator();
                }
                boardPanel.repaint();
            }
        });
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }

    private void drawBoard(Graphics g) {
        int tileSize = 80;
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
        // Player
        g.setColor(Color.GREEN);
        g.fillOval(player.getY() * tileSize + 5, player.getX() * tileSize + 5, tileSize - 10, tileSize - 10);
        drawDirectionIndicator(g, player.getDirection(), player.getX(), player.getY(), tileSize);
        // Enemy
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
                g.drawLine(centerX, centerY, centerX, centerY - tileSize / 3);
                break;
            case "DOWN":
                g.drawLine(centerX, centerY, centerX, centerY + tileSize / 3);
                break;
            case "LEFT":
                g.drawLine(centerX, centerY, centerX - tileSize / 3, centerY);
                break;
            case "RIGHT":
                g.drawLine(centerX, centerY, centerX + tileSize / 3, centerY);
                break;
        }
    }

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
        if (remainingMoves == 0) {
            isPlayerTurn = !isPlayerTurn; // สลับการเป็นผู้เล่น
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

   public void showMainMenu() {
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "MainMenu");
    }

    public void showGamePanel() {
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "GamePanel");
    }

    public static void main(String[] args) {
        GameUI gameUI = new GameUI();
        gameUI.setVisible(true);
    }
}
//next fix move line only
