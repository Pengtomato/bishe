package com.example.bishe.service;

import com.example.bishe.entity.Alert;
import com.example.bishe.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertService {
    
    @Autowired
    private AlertRepository alertRepository;
    
    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }
    
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }
    
    public Alert getAlertById(Long id) {
        return alertRepository.findById(id).orElse(null);
    }
    
    public List<Alert> getAlertsByDeviceId(String deviceId) {
        return alertRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }
    
    public List<Alert> getAlertsByStatus(String status) {
        return alertRepository.findByStatus(status);
    }
    
    public List<Alert> getAlertsByLevel(String level) {
        return alertRepository.findByLevel(level);
    }
    
    public List<Alert> getAlertsByType(String type) {
        return alertRepository.findByType(type);
    }
    
    public List<Alert> getAlertsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return alertRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }
    
    public List<Alert> getAlertsByTimeRangeOrderByLevel(LocalDateTime start, LocalDateTime end) {
        return alertRepository.findByTimeRangeOrderByLevel(start, end);
    }
    
    public List<Alert> getUnprocessedAlerts() {
        return alertRepository.findUnprocessedAlerts();
    }
    
    public Alert processAlert(Long id, String status) {
        Alert alert = alertRepository.findById(id).orElse(null);
        if (alert != null) {
            alert.setStatus(status);
            return alertRepository.save(alert);
        }
        return null;
    }
    
    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
}