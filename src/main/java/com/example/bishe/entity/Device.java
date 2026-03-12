package com.example.bishe.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String deviceId;

    @Column(nullable = false, length = 50)
    private String deviceType; // 风机、水泵、照明、温控等

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String status; // 开启、关闭

    @Column(length = 100)
    private String location;

    @Column(name = "control_params", columnDefinition = "TEXT")
    private String controlParams; // JSON格式的控制参数

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}