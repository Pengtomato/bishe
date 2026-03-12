package com.example.bishe.service;

import com.example.bishe.entity.VideoMonitor;
import com.example.bishe.repository.VideoMonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoMonitorService {
    
    @Autowired
    private VideoMonitorRepository videoMonitorRepository;
    
    public VideoMonitor saveVideoMonitor(VideoMonitor videoMonitor) {
        return videoMonitorRepository.save(videoMonitor);
    }
    
    public List<VideoMonitor> getAllVideoMonitors() {
        return videoMonitorRepository.findAll();
    }
    
    public VideoMonitor getVideoMonitorById(Long id) {
        return videoMonitorRepository.findById(id).orElse(null);
    }
    
    public VideoMonitor getVideoMonitorByDeviceId(String deviceId) {
        return videoMonitorRepository.findByDeviceId(deviceId).orElse(null);
    }
    
    public List<VideoMonitor> getVideoMonitorsByStatus(String status) {
        return videoMonitorRepository.findByStatus(status);
    }
    
    public List<VideoMonitor> searchVideoMonitorsByName(String name) {
        return videoMonitorRepository.findByNameContaining(name);
    }
    
    public void deleteVideoMonitor(Long id) {
        videoMonitorRepository.deleteById(id);
    }
}