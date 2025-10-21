package com.dvdphobia.blueflex.ui;

import com.dvdphobia.blueflex.service.AppService;
import com.dvdphobia.blueflex.utils.AppLogger;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MainUI extends AppService {
    private final AppLogger log = new AppLogger("MainUI");
    private JFrame frame;
    private JPanel panel = new JPanel();
    private JTextField Username;
    private JPasswordField Password;
    // Make 5 btn
    private JButton btn1, btn2, btn3, btn4, btn5;

    private int width = 400;
    private int height = 400;
    private String title = "BlueFlex UI";
    private JLabel Lable1, Lable2, Lable3;

    public MainUI() {
        super("BlueFlex UI", "UI", "0.0.1", "Main User Interface");
    }

    public void configure(Map<String, Object> config) {
        if (config.get("width") != null) width = ((Number) config.get("width")).intValue();
        if (config.get("height") != null) height = ((Number) config.get("height")).intValue();
        if (config.get("title") != null) title = config.get("title").toString();
    }



    public void initFrame(String title, int width, int height) {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
            }
            frame = new JFrame(title);
            frame.setSize(width, height);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new GridLayout(3, 1));

            Lable1 = new JLabel("User Login", JLabel.CENTER);
            Lable2 = new JLabel("Contact", JLabel.CENTER);
            Lable3 = new JLabel("Info", JLabel.CENTER);

            // Buttons
            btn1 = new JButton("Login");
            btn2 = new JButton("Contact");
            btn3 = new JButton("Info");
            btn4 = new JButton("Logout");
            btn5 = new JButton("Exit");

            // Inputs
            Username = new JTextField(20);
            Password = new JPasswordField(20);

            // Main content panel
            JPanel formPanel = panel != null ? panel : new JPanel();
            formPanel.removeAll();
            formPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            formPanel.add(new JLabel("Username:"));
            formPanel.add(Username);
            formPanel.add(new JLabel("Password:"));
            formPanel.add(Password);
            formPanel.add(btn1);
            frame.getContentPane().removeAll();
            frame.add(Lable1);
            frame.add(formPanel);

            // Simple actions
            btn1.addActionListener(e -> {
                String User = Username.getText();
                String Pass = Password.getText();
                if (User.equals("admin") && Pass.equals("")) {
                    frame.dispose();
                    TestCall Tcall = new TestCall();
                    Tcall.FrameInit();
                }
            });

            frame.setVisible(true);
            log.info("MainUI started with size " + width + "x" + height);
        });
    }

    @Override
    public void start() {
        super.start();
        initFrame(title, width, height);
    }

    @Override
    public void stop() {
        super.stop();
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
            }
        });
        log.info("MainUI stopped");
    }
}
