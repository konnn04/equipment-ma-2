package com.hatecode.utils;

import com.hatecode.services.MaintenanceCheckService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaintenanceCheckScheduler {
    private static final Logger LOGGER = Logger.getLogger(MaintenanceCheckScheduler.class.getName());
    private static final int DEFAULT_CHECK_HOURS = 24; // Check mỗi 24 giờ
    
    private static MaintenanceCheckScheduler instance;
    private final ScheduledExecutorService scheduler;
    private final MaintenanceCheckService maintenanceCheckService;
    
    private MaintenanceCheckScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.maintenanceCheckService = new MaintenanceCheckService();
    }
    
    public static synchronized MaintenanceCheckScheduler getInstance() {
        if (instance == null) {
            instance = new MaintenanceCheckScheduler();
        }
        return instance;
    }
    
    /**
     * Bắt đầu scheduler với chu kỳ mặc định
     */
    public void start() {
        start(DEFAULT_CHECK_HOURS);
    }
    
    /**
     * Bắt đầu scheduler với chu kỳ tùy chỉnh
     */
    public void start(int intervalHours) {
        LOGGER.info("Starting maintenance check scheduler with interval: " + intervalHours + " hours");
        
        // Chạy kiểm tra ngay lập tức một lần
        runCheck();
        
        // Sau đó lên lịch theo chu kỳ
        scheduler.scheduleAtFixedRate(
            this::runCheck, 
            intervalHours, 
            intervalHours, 
            TimeUnit.HOURS
        );
    }
    
    /**
     * Chạy kiểm tra ngay lập tức
     */
    public void runCheck() {
        try {
            maintenanceCheckService.checkAllEquipmentForMaintenance();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during scheduled maintenance check", e);
        }
    }
    
    /**
     * Dừng scheduler
     */
    public void shutdown() {
        LOGGER.info("Shutting down maintenance check scheduler");
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