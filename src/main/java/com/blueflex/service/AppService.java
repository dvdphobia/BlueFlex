package com.blueflex.service;

import java.time.LocalDateTime;
import java.util.logging.Logger;
public class AppService {

    //Declare Log

    private final static Logger logger = Logger.getLogger(AppService.class.getName());

    // Declare Service Structure

    private String ServiceName;
    private String ServiceType;
    private String ServiceVersion;
    private LocalDateTime ServiceStartTime;
    private String ServiceDescription;
    private static int ServiceInstanceCounter = 0;
    private boolean isRunning;
    // AppService Constructor

    public AppService(String ServiceName, String ServiceType, String ServiceVersion, String ServiceDescription) {
        this.ServiceName = ServiceName;
        this.ServiceType = ServiceType;
        this.ServiceVersion = ServiceVersion;
        this.ServiceStartTime = LocalDateTime.now();
        this.ServiceDescription = ServiceDescription;
        isRunning = false;
        ServiceInstanceCounter++;


    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            ServiceStartTime = LocalDateTime.now();
            logger.info(() -> "[Started] " + ServiceName + " at " + ServiceStartTime);
        } else {
            logger.warning(ServiceName + " is already isRunning.");
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            ServiceInstanceCounter--;
            logger.info(() -> "[Stopped] " + ServiceName + " | Remaining: " + ServiceInstanceCounter);
        } else {
            logger.warning(ServiceName + " is not Running.");
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
    public String getServiceType() {
        return ServiceType;
    }

    public int  getServiceInstanceCounter() {
        return ServiceInstanceCounter;
    }

    public String getServiceName() {
        return ServiceName;
    }
    public String getServiceVersion() {return ServiceVersion;}
    public String getServiceDescription() {return ServiceDescription;}

    public LocalDateTime getServiceStartTime() {
        return ServiceStartTime;
    }
}
