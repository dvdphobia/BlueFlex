package com.blueflex.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class debug {
    private final JLabel positionLabel;

    public debug(JFrame frame) {
        positionLabel = new JLabel("X: 0, Y: 0");
        positionLabel.setForeground(Color.WHITE);
        positionLabel.setOpaque(true);
        positionLabel.setBackground(new Color(0, 0, 0, 120)); // semi-transparent black
        positionLabel.setFont(new Font("Monospaced", Font.BOLD, 12));

        // Add to layered pane to stay above all components
        JLayeredPane layeredPane = frame.getLayeredPane();
        layeredPane.add(positionLabel, JLayeredPane.DRAG_LAYER);
        positionLabel.setBounds(frame.getWidth() - 120, 10, 110, 20);

        // Adjust label position on resize
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionLabel.setBounds(frame.getWidth() - 120, 10, 110, 20);
            }
        });

        // Track mouse relative to content pane for accuracy
        frame.getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updatePosition(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updatePosition(e.getX(), e.getY());
            }
        });
    }

    private void updatePosition(int x, int y) {
        positionLabel.setText("X: " + x + ", Y: " + y);
    }

    public static void attach(JFrame frame) {
        new debug(frame);
    }
}
