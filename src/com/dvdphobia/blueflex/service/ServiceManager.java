package com.dvdphobia.blueflex.service;

import com.dvdphobia.blueflex.utils.AppLogger;
import com.dvdphobia.blueflex.utils.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceManager {
    private final AppLogger log = new AppLogger("ServiceManager");
    private final List<AppService> services = new ArrayList<>();
    private static final String DEFAULT_CONFIG_PATH = "config/services.json";



    // === Control Methods ===

    public void startAll() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Configuration> configs = mapper.readValue(
                    new File(DEFAULT_CONFIG_PATH),
                    new TypeReference<List<Configuration>>() {}
            );

            for (Configuration cfg : configs) {
                if (!cfg.enabled) continue;

                try {
                    Class<?> cls = Class.forName(cfg.className);
                    Object instance = cls.getDeclaredConstructor().newInstance();

                    if (instance instanceof AppService service) {
                        services.add(service);
                        if (cfg.config != null && service instanceof ConfigurableAppService configurable) {
                            configurable.configure(cfg.config);
                        }
                        service.start();
                        log.info("Started: " + service.getServiceName());
                    } else {
                        log.warn("Not an AppService: " + cfg.className);
                    }
                } catch (Exception e) {
                    log.error("Failed to start " + cfg.className + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Failed to load services.json: " + e.getMessage());
        }
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

