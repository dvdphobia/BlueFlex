package com.dvdphobia.blueflex.service;

import com.dvdphobia.blueflex.utils.AppLogger;
import com.dvdphobia.blueflex.ui.MainUI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServiceManager {
    private final AppLogger log = new AppLogger("ServiceManager");
    private final List<AppService> services = new ArrayList<>();


    public ServiceManager() {
        // Register all services
        services.add(new MainUI());
    }

    // === Control Methods ===

    public void startAll() {
        log.info("Starting all services...");
        for (AppService service : services) {
            startService(service);
        }
        log.info("All services started.");
    }

    public void stopAll() {
        log.info("Stopping all services...");
        for (AppService service : services) {
            stopService(service);
        }
        log.info("All services stopped.");
    }

    public void startService(AppService service) {
        if (!service.isRunning()) {
            service.start();
            log.info("Started: " + service.getServiceName());
        } else {
            log.warn("Service already running: " + service.getServiceName());
        }
    }

    public void stopService(AppService service) {
        if (service.isRunning()) {
            service.stop();
            log.info("Stopped: " + service.getServiceName());
        } else {
            log.warn("Service already stopped: " + service.getServiceName());
        }
    }

    public void restartServiceByName(String name) {
        for (AppService service : services) {
            if (service.getServiceName().equalsIgnoreCase(name)) {
                log.info("Restarting: " + name);
                stopService(service);
                startService(service);
                return;
            }
        }
        log.warn("No service found with name: " + name);
    }

    public void stopServiceByType(String type) {
        for (AppService service : services) {
            if (service.getServiceType().equalsIgnoreCase(type)) {
                stopService(service);
                return;
            }
        }
        log.warn("No service found with type: " + type);
    }

    public List<AppService> getServices() {
        return services;
    }
}

