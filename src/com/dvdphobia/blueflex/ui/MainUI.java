package com.dvdphobia.blueflex.ui;

import com.dvdphobia.blueflex.service.AppService;
import com.dvdphobia.blueflex.utils.AppLogger;

import javax.swing.*;
import java.util.Map;

public class MainUI extends AppService {
    private final AppLogger log = new AppLogger("MainUI");
    private JFrame frame;

    private int width = 600;
    private int height = 400;
    private String title = "BlueFlex UI";

    public MainUI() {
        super("BlueFlex UI", "UI", "0.0.1", "Main User Interface");
    }

    public void configure(Map<String, Object> config) {
        if (config.get("width") != null) width = ((Number) config.get("width")).intValue();
        if (config.get("height") != null) height = ((Number) config.get("height")).intValue();
        if (config.get("title") != null) title = config.get("title").toString();
    }

    @Override
    public void start() {
        super.start();
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        log.info("MainUI started with size " + width + "x" + height);
    }

    @Override
    public void stop() {
        super.stop();
        if (frame != null) frame.dispose();
        log.info("MainUI stopped");
    }
}
