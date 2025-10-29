package com.blueflex.ui;

import com.blueflex.service.AppService;
import com.blueflex.utils.AppLogger;
import javax.swing.*;



public class UI extends AppService {
    private final AppLogger log = new AppLogger("UI");
    private JFrame frame;

    public UI() {
        super("UI_Component", "UI", "0.0.1", "UI component for BlueFlex");
    }


}
