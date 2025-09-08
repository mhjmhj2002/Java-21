package com.example.ticketsystem.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.ticketsystem.entity.BatchProcess;
import com.example.ticketsystem.entity.BatchStatus;
import com.example.ticketsystem.entity.Ticket;
import com.example.ticketsystem.entity.TicketData;
import com.example.ticketsystem.repository.BatchRepository;

@Service
public class BatchService {

    private final TicketService ticketService;
    private final BatchRepository batchRepository;
    private final ClienteService clienteService;

    @Autowired
    public BatchService(TicketService ticketService, BatchRepository batchRepository, ClienteService clienteService) {
        this.ticketService = ticketService;
        this.batchRepository = batchRepository;
        this.clienteService = clienteService;
    }

    @Async
    public CompletableFuture<BatchProcess> processBatch(MultipartFile file) {
        BatchProcess process = new BatchProcess(file.getOriginalFilename());
        process = batchRepository.save(process);
        
        final BatchProcess finalProcess = process;
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<TicketData> tickets = readExcelFile(file);
                finalProcess.setTotalRecords(tickets.size());
                batchRepository.save(finalProcess);
                
                for (int i = 0; i < tickets.size(); i++) {
                    TicketData ticketData = tickets.get(i);
                    try {
                        // Simula o processamento lento (4 segundos por ticket)
                        Thread.sleep(4000);
                        
                        Ticket ticket = convertToTicket(ticketData);
                        if (ticket != null) {
                            ticketService.save(ticket);
                            finalProcess.incrementSuccess();
                        } else {
                            finalProcess.incrementError();
                        }
                    } catch (Exception e) {
                        finalProcess.incrementError();
                    }
                    
                    finalProcess.incrementProcessed();
                    batchRepository.save(finalProcess);
                    
                    // Atualiza a cada 10 registros ou no último
                    if ((i + 1) % 10 == 0 || (i + 1) == tickets.size()) {
                        batchRepository.save(finalProcess);
                    }
                }
                
                finalProcess.completeProcess();
            } catch (Exception e) {
                finalProcess.failProcess(e.getMessage());
            }
            
            return batchRepository.save(finalProcess);
        });
    }

    public BatchProcess getProcessStatus(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo batch não encontrado com id: " + id));
    }

    public List<BatchProcess> getAllProcesses() {
        return batchRepository.findAllOrderByStartTimeDesc();
    }

    public List<BatchProcess> getRecentProcesses(int limit) {
        return batchRepository.findTop5ByOrderByStartTimeDesc();
    }

    public List<BatchProcess> getProcessesByStatus(BatchStatus status) {
        return batchRepository.findByStatusOrderByStartTimeDesc(status);
    }

    private List<TicketData> readExcelFile(MultipartFile file) throws IOException {
        List<TicketData> tickets = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Pular cabeçalho se existir
            if (rows.hasNext()) {
                Row headerRow = rows.next();
                // Verificar se é realmente um cabeçalho
                if (!isHeaderRow(headerRow)) {
                    // Se não for cabeçalho, reiniciar o iterator
                    workbook.close();
                    try (Workbook newWorkbook = new XSSFWorkbook(file.getInputStream())) {
                        sheet = newWorkbook.getSheetAt(0);
                        rows = sheet.iterator();
                    }
                }
            }
            
            while (rows.hasNext()) {
                Row row = rows.next();
                if (isEmptyRow(row)) continue;
                
                TicketData ticketData = new TicketData();
                
                // Coluna A: Título
                Cell titleCell = row.getCell(0);
                if (titleCell != null) {
                    ticketData.setTitulo(getCellValueAsString(titleCell));
                }
                
                // Coluna B: Descrição
                Cell descriptionCell = row.getCell(1);
                if (descriptionCell != null) {
                    ticketData.setDescricao(getCellValueAsString(descriptionCell));
                }
                
                // Coluna C: ID do Cliente
                Cell clientIdCell = row.getCell(2);
                if (clientIdCell != null) {
                    try {
                        ticketData.setClienteId((long) getCellValueAsDouble(clientIdCell));
                    } catch (NumberFormatException e) {
                        // Tentar converter de string para long
                        String cellValue = getCellValueAsString(clientIdCell);
                        if (cellValue != null && !cellValue.trim().isEmpty()) {
                            try {
                                ticketData.setClienteId(Long.parseLong(cellValue.trim()));
                            } catch (NumberFormatException ex) {
                                // Ignorar se não puder converter
                            }
                        }
                    }
                }
                
                tickets.add(ticketData);
            }
        }
        
        return tickets;
    }

    private boolean isHeaderRow(Row row) {
        if (row == null) return false;
        
        Cell firstCell = row.getCell(0);
        if (firstCell == null) return false;
        
        String cellValue = getCellValueAsString(firstCell);
        return cellValue != null && 
              (cellValue.equalsIgnoreCase("título") || 
               cellValue.equalsIgnoreCase("titulo") ||
               cellValue.equalsIgnoreCase("title"));
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue() ? 1 : 0;
            default:
                return 0;
        }
    }

    private Ticket convertToTicket(TicketData ticketData) {
        if (ticketData.getTitulo() == null || ticketData.getTitulo().trim().isEmpty()) {
            return null;
        }
        
        if (ticketData.getClienteId() == null) {
            return null;
        }
        
        // Verificar se o cliente existe
        if (!clienteService.existsById(ticketData.getClienteId())) {
            return null;
        }
        
        Ticket ticket = new Ticket();
        ticket.setTitulo(ticketData.getTitulo());
        ticket.setDescricao(ticketData.getDescricao());
        ticket.setCliente(clienteService.findById(ticketData.getClienteId()).orElse(null));
        
        return ticket;
    }

    public void cancelProcess(Long id) {
        BatchProcess process = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo batch não encontrado com id: " + id));
        
        if (process.getStatus() == BatchStatus.PROCESSING) {
            process.setStatus(BatchStatus.CANCELLED);
            process.setEndTime(LocalDateTime.now());
            batchRepository.save(process);
        }
    }

    public long getTotalProcessedRecords() {
        return batchRepository.findAll().stream()
                .mapToInt(BatchProcess::getProcessedRecords)
                .sum();
    }

    public long getTotalSuccessRecords() {
        return batchRepository.findAll().stream()
                .mapToInt(BatchProcess::getSuccessCount)
                .sum();
    }

    public long getTotalErrorRecords() {
        return batchRepository.findAll().stream()
                .mapToInt(BatchProcess::getErrorCount)
                .sum();
    }
}