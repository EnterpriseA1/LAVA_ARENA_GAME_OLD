/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game_project;

import javax.swing.JPanel;

/**
 *
 * @author THAN
 */
public class AttackAction {

    private Character player;
    private Character enemy;
    private Board board;
    private JPanel boardPanel;
    private HealthBar healthbar;

    public AttackAction(Character player, Character enemy, Board board, JPanel boardPanel) {
        this.player = player;
        this.enemy = enemy;
        this.board = board;
        this.boardPanel = boardPanel;
    }

    public void attackEnemy() {
        // ลด HP ของศัตรูลง 10 หน่วย
        enemy.takeDamage(10);

        // ทำให้ศัตรูกระเด็นถอยหลัง 1 ช่องตามทิศทางที่ผู้เล่นกำลังหันหน้าไป
        int newX = enemy.getX();
        int newY = enemy.getY();

        switch (player.getDirection()) {
            case "UP":
                newX -= 1;
                break;
            case "DOWN":
                newX += 1;
                break;
            case "LEFT":
                newY -= 1;
                break;
            case "RIGHT":
                newY += 1;
                break;
        }

        // เช็คว่าศัตรูจะกระเด็นไปตกในลาวาหรือไม่
        if (canPushEnemy(newX, newY, enemy)) {
            if (board.getTile(newX, newY).isLava()) {
                System.out.println("Enemy fell into lava! Enemy is dead.");
                enemy.setHp(0); // กำหนด HP ของศัตรูเป็น 0
            } else {
                // ถ้าศัตรูไม่ตกลงลาวาและยังอยู่ในขอบเขตบอร์ด
                enemy.setX(newX);
                enemy.setY(newY);
            }
        }

        // เช็คว่า HP ของศัตรูเป็น 0 หรือไม่
        if (enemy.getHp() <= 0) {
            System.out.println("Enemy is dead.");
            // คุณสามารถเพิ่มการจบเกมที่นี่
        }

        boardPanel.repaint(); // วาดกระดานใหม่หลังจากศัตรูโดนโจมตีและกระเด็น
    }

// ฟังก์ชันเพื่อเช็คว่าศัตรูสามารถถูกกระเด็นได้หรือไม่
    private boolean canPushEnemy(int newX, int newY, Character enemy) {
        // เช็คว่าศัตรูอยู่ชิดขอบบอร์ดหรือไม่
        boolean atEdge = newX < 0 || newX >= board.getSize() || newY < 0 || newY >= board.getSize();
        return !atEdge;
    }
}


