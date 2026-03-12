package com.example.bishe.repository;

import com.example.bishe.entity.EnvironmentData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvironmentDataRepository extends JpaRepository<EnvironmentData, Long> {
    
    List<EnvironmentData> findByDeviceIdOrderByTimestampDesc(String deviceId);
    
    List<EnvironmentData> findByDeviceIdAndTimestampBetweenOrderByTimestampDesc(
            String deviceId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT e FROM EnvironmentData e WHERE e.deviceId = :deviceId ORDER BY e.timestamp DESC")
    List<EnvironmentData> findLatestByDeviceId(@Param("deviceId") String deviceId, Pageable pageable);
    
    @Query("SELECT e FROM EnvironmentData e ORDER BY e.timestamp DESC")
    List<EnvironmentData> findLatestData(Pageable pageable);
}