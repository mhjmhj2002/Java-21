package com.example.ticketsystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "batch_process")
public class BatchProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "file_name", nullable = false)
    private String fileName;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "total_records", nullable = false)
    private int totalRecords;
    
    @Column(name = "processed_records", nullable = false)
    private int processedRecords;
    
    @Column(name = "success_count", nullable = false)
    private int successCount;
    
    @Column(name = "error_count", nullable = false)
    private int errorCount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    // Construtores
    public BatchProcess() {}
    
    public BatchProcess(String fileName) {
        this.fileName = fileName;
        this.startTime = LocalDateTime.now();
        this.status = BatchStatus.PROCESSING;
        this.totalRecords = 0;
        this.processedRecords = 0;
        this.successCount = 0;
        this.errorCount = 0;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
    
    public int getProcessedRecords() { return processedRecords; }
    public void setProcessedRecords(int processedRecords) { this.processedRecords = processedRecords; }
    
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    
    public BatchStatus getStatus() { return status; }
    public void setStatus(BatchStatus status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    // Métodos utilitários
    public void incrementProcessed() {
        this.processedRecords++;
    }
    
    public void incrementSuccess() {
        this.successCount++;
    }
    
    public void incrementError() {
        this.errorCount++;
    }
    
    public double getProgressPercentage() {
        if (totalRecords == 0) return 0;
        return (double) processedRecords / totalRecords * 100;
    }
    
    public void completeProcess() {
        this.endTime = LocalDateTime.now();
        this.status = BatchStatus.COMPLETED;
    }
    
    public void failProcess(String errorMessage) {
        this.endTime = LocalDateTime.now();
        this.status = BatchStatus.FAILED;
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "BatchProcess{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", status=" + status +
                ", progress=" + processedRecords + "/" + totalRecords +
                ", success=" + successCount + ", errors=" + errorCount +
                '}';
    }
}