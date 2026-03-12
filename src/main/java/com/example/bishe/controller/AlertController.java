package com.example.bishe.controller;

import com.example.bishe.entity.Alert;
import com.example.bishe.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {
    
    @Autowired
    private AlertService alertService;
    
    @PostMapping
    public Alert saveAlert(@RequestBody Alert alert) {
        return alertService.saveAlert(alert);
    }
    
    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertService.getAllAlerts();
    }
    
    @GetMapping("/{id}")
    public Alert getAlertById(@PathVariable Long id) {
        return alertService.getAlertById(id);
    }
    
    @GetMapping("/device/{deviceId}")
    public List<Alert> getAlertsByDeviceId(@PathVariable String deviceId) {
        return alertService.getAlertsByDeviceId(deviceId);
    }
    
    @GetMapping("/status/{status}")
    public List<Alert> getAlertsByStatus(@PathVariable String status) {
        return alertService.getAlertsByStatus(status);
    }
    
    @GetMapping("/level/{level}")
    public List<Alert> getAlertsByLevel(@PathVariable String level) {
        return alertService.getAlertsByLevel(level);
    }
    
    @GetMapping("/type/{type}")
    public List<Alert> getAlertsByType(@PathVariable String type) {
        return alertService.getAlertsByType(type);
    }
    
    @GetMapping("/range")
    public List<Alert> getAlertsByTimeRange(
            @RequestParam String start, 
            @RequestParam String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        return alertService.getAlertsByTimeRange(startTime, endTime);
    }
    
    @GetMapping("/range/level")
    public List<Alert> getAlertsByTimeRangeOrderByLevel(
            @RequestParam String start, 
            @RequestParam String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        return alertService.getAlertsByTimeRangeOrderByLevel(startTime, endTime);
    }
    
    @GetMapping("/unprocessed")
    public List<Alert> getUnprocessedAlerts() {
        return alertService.getUnprocessedAlerts();
    }
    
    @PutMapping("/{id}/process")
    public Alert processAlert(@PathVariable Long id, @RequestParam String status) {
        return alertService.processAlert(id, status);
    }
    
    @DeleteMapping("/{id}")
    public void deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
    }
}