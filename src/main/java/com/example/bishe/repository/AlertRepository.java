package com.example.bishe.repository;

import com.example.bishe.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByDeviceIdOrderByTimestampDesc(String deviceId);
    
    List<Alert> findByStatus(String status);
    
    List<Alert> findByLevel(String level);
    
    List<Alert> findByType(String type);
    
    List<Alert> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Alert a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.level DESC, a.timestamp DESC")
    List<Alert> findByTimeRangeOrderByLevel(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT a FROM Alert a WHERE a.status = '未处理' ORDER BY a.level DESC, a.timestamp DESC")
    List<Alert> findUnprocessedAlerts();
}