package com.example.bishe.controller;

import com.example.bishe.entity.Device;
import com.example.bishe.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    @Autowired
    private DeviceService deviceService;
    
    @PostMapping
    public Device saveDevice(@RequestBody Device device) {
        return deviceService.saveDevice(device);
    }
    
    @GetMapping
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }
    
    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable Long id) {
        return deviceService.getDeviceById(id);
    }
    
    @GetMapping("/device/{deviceId}")
    public Device getDeviceByDeviceId(@PathVariable String deviceId) {
        return deviceService.getDeviceByDeviceId(deviceId);
    }
    
    @GetMapping("/type/{deviceType}")
    public List<Device> getDevicesByDeviceType(@PathVariable String deviceType) {
        return deviceService.getDevicesByDeviceType(deviceType);
    }
    
    @GetMapping("/status/{status}")
    public List<Device> getDevicesByStatus(@PathVariable String status) {
        return deviceService.getDevicesByStatus(status);
    }
    
    @GetMapping("/search/name")
    public List<Device> searchDevicesByName(@RequestParam String name) {
        return deviceService.searchDevicesByName(name);
    }
    
    @GetMapping("/search/location")
    public List<Device> searchDevicesByLocation(@RequestParam String location) {
        return deviceService.searchDevicesByLocation(location);
    }
    
    @PutMapping("/{id}")
    public Device updateDevice(@PathVariable Long id, @RequestBody Device device) {
        return deviceService.saveDevice(device);
    }
    
    @PutMapping("/{id}/status")
    public Device updateDeviceStatus(@PathVariable Long id, @RequestParam String status) {
        return deviceService.updateDeviceStatus(id, status);
    }
    
    @PutMapping("/{id}/params")
    public Device updateDeviceControlParams(@PathVariable Long id, @RequestParam String controlParams) {
        return deviceService.updateDeviceControlParams(id, controlParams);
    }
    
    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
    }
}