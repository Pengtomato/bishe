package com.example.bishe.controller;

import com.example.bishe.entity.EnvironmentData;
import com.example.bishe.service.EnvironmentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/environment")
@CrossOrigin(origins = "*")
public class EnvironmentDataController {
    
    @Autowired
    private EnvironmentDataService environmentDataService;
    
    @PostMapping
    public EnvironmentData saveEnvironmentData(@RequestBody EnvironmentData data) {
        return environmentDataService.createEnvironmentData(data);
    }
    
    @GetMapping
    public List<EnvironmentData> getAllEnvironmentData() {
        return environmentDataService.getAllEnvironmentData();
    }
    
    @GetMapping("/{id}")
    public EnvironmentData getEnvironmentDataById(@PathVariable Long id) {
        return environmentDataService.getEnvironmentDataById(id);
    }
    
    @GetMapping("/device/{deviceId}")
    public List<EnvironmentData> getEnvironmentDataByDeviceId(@PathVariable String deviceId) {
        return environmentDataService.getEnvironmentDataByDeviceId(deviceId);
    }
    
    @GetMapping("/device/{deviceId}/latest")
    public EnvironmentData getLatestEnvironmentData(@PathVariable String deviceId) {
        return environmentDataService.getLatestEnvironmentData(deviceId);
    }
    
    @GetMapping("/device/{deviceId}/range")
    public List<EnvironmentData> getEnvironmentDataByTimeRange(
            @PathVariable String deviceId,
            @RequestParam String start,
            @RequestParam String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        return environmentDataService.getEnvironmentDataByTimeRange(deviceId, startTime, endTime);
    }
    
    @GetMapping("/latest")
    public List<EnvironmentData> getLatestData() {
        return environmentDataService.getLatestData();
    }
    
    @DeleteMapping("/{id}")
    public void deleteEnvironmentData(@PathVariable Long id) {
        environmentDataService.deleteEnvironmentData(id);
    }
}