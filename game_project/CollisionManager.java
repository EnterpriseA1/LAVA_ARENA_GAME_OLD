/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game_project;

/**
 *
 * @author THAN
 */
public class CollisionManager {

    private Character player;
    private Character enemy;

    public CollisionManager(Character player, Character enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    // ตรวจสอบว่าตัวละครกำลังจะเดินทับกันหรือไม่
    public boolean isColliding(int newX, int newY) {
        // ถ้าตำแหน่งที่ต้องการเดินตรงกับตำแหน่งของอีกตัวละคร แสดงว่าชนกัน
        if ((newX == player.getX() && newY == player.getY()) || (newX == enemy.getX() && newY == enemy.getY())) {
            return true;
        }
        return false;
    }

    // ตรวจสอบว่าตัวละครที่ต้องการจะเดินทับอีกตัวหรือไม่
    public boolean willCollide(Character character, int newX, int newY) {
        if (character.equals(player)) {
            return newX == enemy.getX() && newY == enemy.getY();
        } else if (character.equals(enemy)) {
            return newX == player.getX() && newY == player.getY();
        }
        return false;
    }
}
