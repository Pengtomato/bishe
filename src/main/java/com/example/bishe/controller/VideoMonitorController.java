package com.example.bishe.controller;

import com.example.bishe.entity.VideoMonitor;
import com.example.bishe.service.VideoMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video")
@CrossOrigin(origins = "*")
public class VideoMonitorController {
    
    @Autowired
    private VideoMonitorService videoMonitorService;
    
    @PostMapping
    public VideoMonitor saveVideoMonitor(@RequestBody VideoMonitor videoMonitor) {
        return videoMonitorService.saveVideoMonitor(videoMonitor);
    }
    
    @GetMapping
    public List<VideoMonitor> getAllVideoMonitors() {
        return videoMonitorService.getAllVideoMonitors();
    }
    
    @GetMapping("/{id}")
    public VideoMonitor getVideoMonitorById(@PathVariable Long id) {
        return videoMonitorService.getVideoMonitorById(id);
    }
    
    @GetMapping("/device/{deviceId}")
    public VideoMonitor getVideoMonitorByDeviceId(@PathVariable String deviceId) {
        return videoMonitorService.getVideoMonitorByDeviceId(deviceId);
    }
    
    @GetMapping("/status/{status}")
    public List<VideoMonitor> getVideoMonitorsByStatus(@PathVariable String status) {
        return videoMonitorService.getVideoMonitorsByStatus(status);
    }
    
    @GetMapping("/search")
    public List<VideoMonitor> searchVideoMonitorsByName(@RequestParam String name) {
        return videoMonitorService.searchVideoMonitorsByName(name);
    }
    
    @PutMapping("/{id}")
    public VideoMonitor updateVideoMonitor(@PathVariable Long id, @RequestBody VideoMonitor videoMonitor) {
        return videoMonitorService.saveVideoMonitor(videoMonitor);
    }
    
    @DeleteMapping("/{id}")
    public void deleteVideoMonitor(@PathVariable Long id) {
        videoMonitorService.deleteVideoMonitor(id);
    }
}