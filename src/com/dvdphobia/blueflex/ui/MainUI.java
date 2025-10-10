package com.dvdphobia.blueflex.ui;

import com.dvdphobia.blueflex.service.AppService;
import com.dvdphobia.blueflex.utils.AppLogger;
import javax.swing.*;



public class MainUI extends AppService {
    private final AppLogger log = new AppLogger("MainUI");
    private JFrame frame;

    public MainUI() {
        super("BlueFlex UI", "UI", "0.0.1", "UI for BlueFlex");
    }
}
