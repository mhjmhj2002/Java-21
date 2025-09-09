package com.example.ticketsystem.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.ticketsystem.entity.BatchProcess;
import com.example.ticketsystem.service.BatchService;

import io.swagger.v3.oas.annotations.Operation;

@Controller
@RequestMapping("/batch")
public class BatchController {

	private final BatchService batchService;
	private final SimpMessagingTemplate messagingTemplate;

	@Autowired
	public BatchController(BatchService batchService, SimpMessagingTemplate messagingTemplate) {
		this.batchService = batchService;
		this.messagingTemplate = messagingTemplate;
	}

	@GetMapping("/upload")
	public String uploadForm() {
		return "batch/upload";
	}
	
	@Operation(summary = "Faz upload de arquivo para processamento", 
	           description = "Recebe um arquivo Excel (.xlsx ou .xls) para processamento assíncrono")
	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
	    try {
	        if (file.isEmpty()) {
	            return ResponseEntity.badRequest().body("Arquivo vazio");
	        }
	        
	        if (!file.getOriginalFilename().toLowerCase().endsWith(".xlsx") && 
	            !file.getOriginalFilename().toLowerCase().endsWith(".xls")) {
	            return ResponseEntity.badRequest().body("Formato de arquivo inválido. Use .xlsx ou .xls");
	        }
	        
	        // Salvar o conteúdo do arquivo em bytes antes de processar assincronamente
	        byte[] fileContent = file.getBytes();
	        String fileName = file.getOriginalFilename();
	        
	        BatchProcess process = batchService.createProcess(fileName);
	        
	        System.out.println("Processo criado com ID: " + process.getId());
	        
	        // Iniciar processamento assíncrono com o conteúdo do arquivo
	        CompletableFuture.runAsync(() -> {
	            try {
	                batchService.processBatchWithWebSocket(fileContent, fileName, process, messagingTemplate);
	            } catch (Exception e) {
	                System.err.println("Erro no processamento assíncrono: " + e.getMessage());
	                e.printStackTrace();
	            }
	        });
	        
	        return ResponseEntity.ok(process);
	        
	    } catch (Exception e) {
	        System.err.println("Erro no upload: " + e.getMessage());
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().body("Erro interno: " + e.getMessage());
	    }
	}

	@GetMapping("/status")
	public String status(Model model) {
		model.addAttribute("processos", batchService.getAllProcesses());
		return "batch/status";
	}

	@GetMapping("/status/{id}")
	public ResponseEntity<BatchProcess> getStatus(@PathVariable Long id) {
		try {
			BatchProcess process = batchService.getProcessStatus(id);
			return ResponseEntity.ok(process);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "Obtém status de todos os processos")
	@GetMapping("/all")
	public ResponseEntity<List<BatchProcess>> getAllProcesses() {
		List<BatchProcess> processes = batchService.getAllProcesses();
		return ResponseEntity.ok(processes);
	}

	@Operation(summary = "Obtém processos recentes")
	@GetMapping("/recent")
	public ResponseEntity<List<BatchProcess>> getRecentProcesses() {
		List<BatchProcess> processes = batchService.getRecentProcesses(5);
		return ResponseEntity.ok(processes);
	}

	@Operation(summary = "Cancela um processo")
	@PostMapping("/cancel/{id}")
	public ResponseEntity<Void> cancelProcess(@PathVariable Long id) {
		try {
			batchService.cancelProcess(id);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "Obtém estatísticas de processamento")
	@GetMapping("/stats")
	public ResponseEntity<String> getBatchStats() {
		long totalProcessed = batchService.getTotalProcessedRecords();
		long totalSuccess = batchService.getTotalSuccessRecords();
		long totalErrors = batchService.getTotalErrorRecords();

		String stats = String.format("Total processado: %d, Sucessos: %d, Erros: %d", totalProcessed, totalSuccess,
				totalErrors);
		return ResponseEntity.ok(stats);
	}
}