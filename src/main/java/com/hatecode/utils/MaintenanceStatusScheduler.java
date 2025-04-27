package com.hatecode.utils;

import com.hatecode.services.impl.MaintenanceStatusUpdateServiceImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaintenanceStatusScheduler {
    private static final Logger LOGGER = Logger.getLogger(MaintenanceStatusScheduler.class.getName());
    private static final int DEFAULT_INTERVAL_MINUTES = 15; // Check every 15 minutes
    
    private static MaintenanceStatusScheduler instance;
    private final ScheduledExecutorService scheduler;
    private final MaintenanceStatusUpdateServiceImpl statusUpdateService;
    
    private MaintenanceStatusScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.statusUpdateService = new MaintenanceStatusUpdateServiceImpl();
    }
    
    public static synchronized MaintenanceStatusScheduler getInstance() {
        if (instance == null) {
            instance = new MaintenanceStatusScheduler();
        }
        return instance;
    }
    
    /**
     * Start the scheduler with default interval
     */
    public void start() {
        start(DEFAULT_INTERVAL_MINUTES);
    }
    
    /**
     * Start the scheduler with custom interval
     */
    public void start(int intervalMinutes) {
        LOGGER.info("Starting maintenance status scheduler with interval: " + intervalMinutes + " minutes");
        
        // Run the update process immediately once
        runUpdate();
        
        // Then schedule at regular intervals
        scheduler.scheduleAtFixedRate(
            this::runUpdate, 
            intervalMinutes, 
            intervalMinutes, 
            TimeUnit.MINUTES
        );
    }
    
    /**
     * Run a single update immediately
     */
    public void runUpdate() {
        try {
            statusUpdateService.updateAllMaintenanceStatuses();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during scheduled maintenance status update", e);
        }
    }
    
    /**
     * Stop the scheduler
     */
    public void shutdown() {
        LOGGER.info("Shutting down maintenance status scheduler");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}