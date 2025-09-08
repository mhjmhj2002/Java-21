package com.example.ticketsystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ticketsystem.entity.BatchProcess;
import com.example.ticketsystem.entity.BatchStatus;

@Repository
public interface BatchRepository extends JpaRepository<BatchProcess, Long> {

    List<BatchProcess> findByStatus(BatchStatus status);
    
    List<BatchProcess> findByFileNameContainingIgnoreCase(String fileName);
    
    List<BatchProcess> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    List<BatchProcess> findByStatusOrderByStartTimeDesc(BatchStatus status);
    
    @Query("SELECT bp FROM BatchProcess bp ORDER BY bp.startTime DESC")
    List<BatchProcess> findAllOrderByStartTimeDesc();
    
    @Query("SELECT bp FROM BatchProcess bp WHERE bp.status = :status AND bp.startTime >= :startDate ORDER BY bp.startTime DESC")
    List<BatchProcess> findRecentByStatus(@Param("status") BatchStatus status, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(bp) FROM BatchProcess bp WHERE bp.status = :status")
    long countByStatus(@Param("status") BatchStatus status);
    
    @Query("SELECT COUNT(bp) FROM BatchProcess bp WHERE bp.startTime BETWEEN :start AND :end")
    long countByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT bp FROM BatchProcess bp WHERE bp.totalRecords > 0 ORDER BY bp.processedRecords / bp.totalRecords DESC")
    List<BatchProcess> findByProgressPercentage();
    
    List<BatchProcess> findTop5ByOrderByStartTimeDesc();
}