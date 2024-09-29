/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game_project;

/**
 *
 * @author THAN
 */
import javax.swing.*;
import java.awt.*;

public class HealthBar extends JPanel {

    private JProgressBar healthBar;
    private String label;

    public HealthBar(String label) {
        this.label = label;
        setLayout(new BorderLayout());

        healthBar = new JProgressBar(JProgressBar.VERTICAL,0, 100);
        healthBar.setValue(100); // เริ่มต้นที่ 100
        healthBar.setStringPainted(true);
        healthBar.setForeground(Color.GREEN);
        
        JLabel healthLabel = new JLabel(label);
        healthLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(healthLabel, BorderLayout.NORTH);
        add(healthBar, BorderLayout.CENTER);
    }

    public void setHealth(int health) {
        healthBar.setValue(health);

        if (health > 50) {
            healthBar.setForeground(Color.GREEN); // สีเขียวเมื่อเลือดยังมาก
        } else if (health > 20) {
            healthBar.setForeground(Color.YELLOW); // สีเหลืองเมื่อเลือดลด
        } else {
            healthBar.setForeground(Color.RED); // สีแดงเมื่อเลือดต่ำ
        }
    }
}
