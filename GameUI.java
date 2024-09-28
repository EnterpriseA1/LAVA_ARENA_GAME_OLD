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
    private CollisionManager collisionManager;
    private boolean canRollDice = true;
    private HealthBar playerHealthBar;
    private HealthBar enemyHealthBar;
    private AttackAction attackAction;
    private boolean hasAttacked = false;

    public GameUI() {
        setTitle("Turn-Based Battle Game");
        setSize(890, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());
        setResizable(false);
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

        collisionManager = new CollisionManager(player, enemy);

        turnIndicatorLabel = new JLabel("Player's Turn", SwingConstants.CENTER);
        turnIndicatorLabel.setFont(new Font("Arial", Font.BOLD, 24));
        turnIndicatorLabel.setForeground(Color.BLUE);

        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(240, 240, 240)); // สีพื้นหลัง
        gamePanel.add(turnIndicatorLabel, BorderLayout.NORTH);

        JButton rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(new MoveAction());
        styleButton(rollButton);

        diceResultLabel = new JLabel("Dice Result: ", SwingConstants.CENTER);
        diceResultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        diceResultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding
        diceResultLabel.setBackground(Color.GRAY); // ตั้งค่าสีพื้นหลังเป็นสีน้ำเงิน
        diceResultLabel.setOpaque(true);

        // สร้าง HealthBar สำหรับผู้เล่นและศัตรู
        playerHealthBar = new HealthBar("Player HP");
        enemyHealthBar = new HealthBar("Enemy HP");

        JPanel healthPanel = new JPanel(new GridLayout(1, 2)); // จัดให้แสดงผลในแนวตั้ง
        healthPanel.add(playerHealthBar);
        healthPanel.add(enemyHealthBar);

        // เพิ่ม healthPanel ที่ด้านขวาของ gamePanel
        gamePanel.add(healthPanel, BorderLayout.EAST);

        //debug healthbar
        //player.takeDamage(50);
        //updateHealthBars();
        //debug healthbar
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };

        boardPanel.setPreferredSize(new Dimension(400, 400));
        boardPanel.setBackground(Color.WHITE);
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5)); // Border

        gamePanel.add(boardPanel, BorderLayout.CENTER); // เพิ่ม boardPanel ที่กลาง
        gamePanel.add(diceResultLabel, BorderLayout.WEST); // ย้าย diceResultLabel ไปที่ด้านซ้าย
        gamePanel.add(rollButton, BorderLayout.SOUTH); // เพิ่ม rollButton ที่ด้านล่าง
        gamePanel.add(healthPanel, BorderLayout.EAST);

        // เพิ่มให้ปุ่มมีระยะห่าง
        rollButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tileSize = 80;
                int x = e.getY() / tileSize;
                int y = e.getX() / tileSize;

                if (remainingMoves > 0) { // ตรวจสอบว่า remainingMoves มากกว่า 0 ก่อน
                    if (isPlayerTurn) {
                        moveCharacter(player, x, y);

                        // เช็คว่าตำแหน่งที่คลิกคือศัตรูหรือไม่
                        if (x == enemy.getX() && y == enemy.getY() && !hasAttacked) {
                            // เปลี่ยนทิศทางของผู้เล่นไปยังศัตรู
                            setDirectionToEnemy(player, enemy);

                            attackAction = new AttackAction(player, enemy, board, boardPanel);
                            attackAction.attackEnemy();
                            updateHealthBars(); // อัปเดต HealthBar หลังจากโจมตี
                            hasAttacked = true; // ตั้งค่า hasAttacked เป็น true หลังจากโจมตี
                        }
                    } else {
                        moveCharacter(enemy, x, y);
                        if (x == player.getX() && y == player.getY() && !hasAttacked) {
                            // เปลี่ยนทิศทางของศัตรูไปยังผู้เล่น
                            setDirectionToEnemy(enemy, player);

                            attackAction = new AttackAction(enemy, player, board, boardPanel);
                            attackAction.attackEnemy();
                            updateHealthBars(); // อัปเดต HealthBar หลังจากโจมตี
                            hasAttacked = true; // ตั้งค่า hasAttacked เป็น true หลังจากโจมตี
                        }
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

    private void updateHealthBars() {
        // เพิ่มข้อความดีบั๊กเพื่อดูค่าของพลังชีวิต
        System.out.println("Player HP: " + player.getHp());
        System.out.println("Enemy HP: " + enemy.getHp());

        // อัปเดตหลอดพลังชีวิตของผู้เล่นและศัตรู
        playerHealthBar.setHealth(player.getHp());
        enemyHealthBar.setHealth(enemy.getHp());

        // ตรวจสอบการเรียก repaint เพื่อให้ UI อัปเดตใหม่
        playerHealthBar.repaint();
        enemyHealthBar.repaint();
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

        // ตรวจสอบว่าเหลือการเดิน (remainingMoves) หรือไม่
        if (remainingMoves > 0 && (Math.abs(dx) == 1 && dy == 0 || Math.abs(dy) == 1 && dx == 0)) {
            // ตรวจสอบว่ามีการชนหรือไม่
            if (collisionManager.willCollide(character, newX, newY)) {
                System.out.println("Cannot move! Another character is blocking the way.");
                return; // ออกจากฟังก์ชันหากเกิดการชน
            }

            // อัปเดตทิศทางตามพิกัดที่เคลื่อนที่ไป
            if (dx == 1) {
                character.setDirection("DOWN");
            } else if (dx == -1) {
                character.setDirection("UP");
            } else if (dy == 1) {
                character.setDirection("RIGHT");
            } else if (dy == -1) {
                character.setDirection("LEFT");
            }

            // อัปเดตตำแหน่งของตัวละคร (ทีละ 1 ช่อง)
            character.setX(newX);
            character.setY(newY);
            remainingMoves--;

            boardPanel.repaint(); // วาดกระดานใหม่เมื่อเคลื่อนที่เสร็จ
        } else {
            System.out.println("Invalid move or no moves left! Remaining moves: " + remainingMoves);
        }

        if (remainingMoves == 0) {
            isPlayerTurn = !isPlayerTurn; // สลับผู้เล่น
        }
    }

    private int rollDice() {
        return dice.nextInt(6) + 1;
    }

    private class MoveAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (canRollDice) { // ตรวจสอบว่าสามารถทอยเต๋าได้หรือไม่
                diceValue = rollDice();
                remainingMoves = diceValue;
                diceResultLabel.setText("Dice Result: " + diceValue);
                canRollDice = false; // ตั้งค่าให้ทอยเต๋าไม่ได้อีกในเทิร์นนี้
                boardPanel.repaint();
            } else {
                System.out.println("You can only roll the dice once per turn!");
            }
        }
    }

    private void updateTurnIndicator() {
        if (isPlayerTurn) {
            turnIndicatorLabel.setText("Player's Turn");
            hasAttacked = false; // รีเซ็ต hasAttacked เมื่อเปลี่ยนเป็นเทิร์นของผู้เล่น
        } else {
            turnIndicatorLabel.setText("Enemy's Turn");
            hasAttacked = false;
        }
        canRollDice = true; // เมื่อเปลี่ยนเทิร์น ให้สามารถทอยเต๋าได้อีกครั้ง
    }

    public void showMainMenu() {
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "MainMenu");
    }

    public void showGamePanel() {
        CardLayout cl = (CardLayout) (getContentPane().getLayout());
        cl.show(getContentPane(), "GamePanel");
    }

    //set direction to enemy before attack
    private void setDirectionToEnemy(Character attacker, Character target) {
        int dx = target.getX() - attacker.getX();
        int dy = target.getY() - attacker.getY();

        if (Math.abs(dx) > Math.abs(dy)) {
            // ถ้าความแตกต่างในแนวนอนมากกว่าในแนวตั้ง
            if (dx > 0) {
                attacker.setDirection("DOWN");
            } else {
                attacker.setDirection("UP");
            }
        } else {
            // ถ้าความแตกต่างในแนวตั้งมากกว่าในแนวนอน
            if (dy > 0) {
                attacker.setDirection("RIGHT");
            } else {
                attacker.setDirection("LEFT");
            }
        }
    }

    public static void main(String[] args) {
        GameUI gameUI = new GameUI();
        gameUI.setVisible(true);

    }
}
//next fix move line only
