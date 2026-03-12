package com.example.bishe.service;

import com.example.bishe.entity.Device;
import com.example.bishe.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    public Device saveDevice(Device device) {
        return deviceRepository.save(device);
    }
    
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    
    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }
    
    public Device getDeviceByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId).orElse(null);
    }
    
    public List<Device> getDevicesByDeviceType(String deviceType) {
        return deviceRepository.findByDeviceType(deviceType);
    }
    
    public List<Device> getDevicesByStatus(String status) {
        return deviceRepository.findByStatus(status);
    }
    
    public List<Device> searchDevicesByName(String name) {
        return deviceRepository.findByNameContaining(name);
    }
    
    public List<Device> searchDevicesByLocation(String location) {
        return deviceRepository.findByLocationContaining(location);
    }
    
    public Device updateDeviceStatus(Long id, String status) {
        Device device = deviceRepository.findById(id).orElse(null);
        return device;
    }
    
    public Device updateDeviceControlParams(Long id, String controlParams) {
        Device device = deviceRepository.findById(id).orElse(null);
        return device;
    }
    
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }
}