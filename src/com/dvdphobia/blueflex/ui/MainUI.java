package com.dvdphobia.blueflex.ui;

import javax.swing.*;

import java.awt.*;
import java.util.concurrent.Flow;

public class MainUI extends JFrame {
    public JFrame mainFrame;
    public JButton btn1, btn2, btn3;
    public JPanel panel1, panel2, panel3;
    private ImageIcon icon;

    public MainUI() {
        mainFrame = new JFrame("My Application");
        mainFrame.setSize(500, 600);
        mainFrame.setLayout(new GridLayout(3, 1)); // 3 rows, 1 column
        icon = new ImageIcon("/Users/ashiba/Documents/ahphou.jpg");

        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

        panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.CENTER));

        panel3 = new JPanel();
        panel3.setLayout(new FlowLayout(FlowLayout.RIGHT));


        btn1 = new JButton("LEFT", icon);
        btn2 = new JButton("CENTER");
        btn3 = new JButton("RIGHT");


        panel1.add(btn1);
        panel2.add(btn2);
        panel3.add(btn3);
        // Add buttons to frame
        mainFrame.add(panel1);
        mainFrame.add(panel2);
        mainFrame.add(panel3);



        mainFrame.setVisible(true);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}