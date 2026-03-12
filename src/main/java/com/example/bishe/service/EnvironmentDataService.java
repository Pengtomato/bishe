package com.example.bishe.service;

import com.example.bishe.entity.EnvironmentData;
import com.example.bishe.repository.EnvironmentDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnvironmentDataService {
    
    @Autowired
    private EnvironmentDataRepository environmentDataRepository;
    
    public EnvironmentData createEnvironmentData(EnvironmentData data) {
        return environmentDataRepository.save(data);
    }
    
    public List<EnvironmentData> getAllEnvironmentData() {
        return environmentDataRepository.findAll();
    }
    
    public EnvironmentData getEnvironmentDataById(Long id) {
        return environmentDataRepository.findById(id).orElse(null);
    }
    
    public List<EnvironmentData> getEnvironmentDataByDeviceId(String deviceId) {
        return environmentDataRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }
    
    public EnvironmentData getLatestEnvironmentData(String deviceId) {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "timestamp"));
        List<EnvironmentData> result = environmentDataRepository.findLatestByDeviceId(deviceId, pageable);
        return result.isEmpty() ? null : result.get(0);
    }
    
    public List<EnvironmentData> getEnvironmentDataByTimeRange(
            String deviceId, LocalDateTime start, LocalDateTime end) {
        return environmentDataRepository.findByDeviceIdAndTimestampBetweenOrderByTimestampDesc(
                deviceId, start, end);
    }
    
    public List<EnvironmentData> getLatestData() {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "timestamp"));
        return environmentDataRepository.findLatestData(pageable);
    }
    
    public void deleteEnvironmentData(Long id) {
        environmentDataRepository.deleteById(id);
    }

    public EnvironmentData saveEnvironmentData(EnvironmentData data) {
        return null;
    }
}