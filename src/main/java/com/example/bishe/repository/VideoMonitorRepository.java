package com.example.bishe.repository;

import com.example.bishe.entity.VideoMonitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoMonitorRepository extends JpaRepository<VideoMonitor, Long> {
    
    Optional<VideoMonitor> findByDeviceId(String deviceId);
    
    List<VideoMonitor> findByStatus(String status);
    
    List<VideoMonitor> findByNameContaining(String name);
}