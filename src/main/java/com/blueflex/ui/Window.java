package com.blueflex.ui;

import javax.swing.*;
import java.awt.*;
import com.blueflex.utils.debug;

public class Window {
    private JFrame frame;
    private JPanel panel;

    public Window(String title, int width, int height, boolean Debug) {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null); // absolute positioning
        frame.add(panel);

    }

    // Add Button with optional position
    public Window addButton(String text, Runnable onClick) {
        return addButton(text, onClick, 0, 0, 100, 30);
    }

    public Window addButton(String text, Runnable onClick, int x, int y) {
        return addButton(text, onClick, x, y, 100, 30);
    }

    public Window addButton(String text, Runnable onClick, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.addActionListener(e -> onClick.run());
        button.setBounds(x, y, width, height);
        panel.add(button);
        return this;
    }

    // Add Label with optional position
    public Window addLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        panel.add(label);
        return this;
    }

    public Window nig(String text) {
        return addLabel(text, 0, 0, 100, 30);
    }

    // Add TextField with optional position
    public Window addTextField(String name, int x, int y, int width, int height) {
        JTextField field = new JTextField();
        field.setName(name);
        field.setBounds(x, y, width, height);
        panel.add(field);
        return this;
    }

    public Window addTextField(String name) {
        return addTextField(name, 0, 0, 100, 30);
    }

    // Get TextField value by name
    public String getText(String name) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JTextField && name.equals(c.getName())) {
                return ((JTextField) c).getText();
            }
        }
        return null;
    }

    public Window enableDebug()
    {
        debug.attach(frame);
        return this;
    }


    public void show() {
        frame.setVisible(true);
    }


    public static String test(int x, int y)
    {
        int cal = x + y;
        return String.valueOf(cal);
    }



    // Example usage
    public static void main(String[] args) {
        Window win = new Window("My App", 400, 300, true);

        win.addLabel("Enter Name:", 50, 30, 100, 30)
                .enableDebug()
                .addTextField("nameField", 160, 30, 150, 30)
                .addButton("Say Hello", () -> {
                    System.out.println("Hello " + win.getText("nameField"));
                }, 50, 80)
                .addButton("Exit", () -> System.exit(0), 200, 80)
                .addButton("Test", () -> win.addLabel(test(12,21), 50, 200, 150, 30), 50,115)
                .show();
    }
}
