package com.example.bishe.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "alert")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type; // 温度异常、湿度异常、CO2超标、设备离线等

    @Column(nullable = false, length = 20)
    private String level; // 低、中、高

    @Column(nullable = false, length = 50)
    private String deviceId;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(length = 20)
    private String status; // 未处理、已处理、已忽略

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}