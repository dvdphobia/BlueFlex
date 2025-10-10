package com.dvdphobia.blueflex.utils;

import com.dvdphobia.blueflex.service.AppService;
import com.dvdphobia.blueflex.service.ServiceManager;
import java.util.Scanner;

public class CLI extends AppService {
    private final AppLogger log = new AppLogger("CLI");
    private boolean running;
    private final ServiceManager manager; // make it final, set in constructor

    public CLI(ServiceManager manager) {
        super("CLI Utils", "CLI", "0.0.1", "A Service type that handles CLI input for the application");
        this.manager = manager; // assign manager passed in
    }

    public void runUntilStopped() {
        running = true;
        log.info("CLI started. Type 'stop' to exit.");

        Scanner scanner = new Scanner(System.in);
        while (running) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();
            if (command.equals("stop") || command.equals("exit") || command.equals("quit")) {
                running = false;
                manager.stopAll();
            } else if (command.startsWith("restart ")) {
                String name = command.substring(8).trim();
                manager.restartServiceByName(name);
            } else if (command.startsWith("stop type ")) {
                String type = command.substring(10).trim();
                manager.stopServiceByType(type);
            } else {
                log.info("Unknown command: " + command);
            }
        }

        log.info("CLI stopped.");
        scanner.close();
    }
}
