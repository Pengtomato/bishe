package com.example.bishe.repository;

import com.example.bishe.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    Optional<Device> findByDeviceId(String deviceId);
    
    List<Device> findByDeviceType(String deviceType);
    
    List<Device> findByStatus(String status);
    
    List<Device> findByNameContaining(String name);
    
    List<Device> findByLocationContaining(String location);
}