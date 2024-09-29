/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game_project;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author THAN
 */
public class MainMenu extends JPanel {

    private GameUI gameUI;

    public MainMenu(GameUI gameUI) {
        this.gameUI = gameUI;
        setLayout(new GridBagLayout());
        setBackground(Color.CYAN);

        JButton startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> gameUI.showGamePanel());
        styleButton(startGameButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        styleButton(exitButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // คอลัมน์แรก
        gbc.gridy = 0; // แถวแรก
        gbc.insets = new Insets(10, 10, 10, 10); // เว้นระยะห่างระหว่างปุ่ม
        gbc.anchor = GridBagConstraints.CENTER; // จัดกลาง

        add(startGameButton, gbc);

        gbc.gridy = 1; // ย้ายไปยังแถวถัดไป
        add(exitButton, gbc);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }
}
