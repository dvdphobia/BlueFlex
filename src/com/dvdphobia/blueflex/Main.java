package com.dvdphobia.blueflex;

import com.dvdphobia.blueflex.service.ServiceManager;
import com.dvdphobia.blueflex.utils.CLI;

public class Main {
    public static void main(String[] args) {
        ServiceManager manager = new ServiceManager();
        manager.startAll();

        CLI cli = new CLI(manager);
        cli.runUntilStopped(); // now it works — manager is not null
    }
}
