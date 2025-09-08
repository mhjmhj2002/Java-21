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

	@PostMapping("/upload")
	public ResponseEntity<BatchProcess> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			BatchProcess process = batchService.createProcess(file.getOriginalFilename());

			// Iniciar processamento assíncrono
			CompletableFuture.runAsync(() -> {
				batchService.processBatchWithWebSocket(file, process, messagingTemplate);
			});

			return ResponseEntity.ok(process);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(null);
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

	@GetMapping("/all")
	public ResponseEntity<List<BatchProcess>> getAllProcesses() {
		List<BatchProcess> processes = batchService.getAllProcesses();
		return ResponseEntity.ok(processes);
	}

	@GetMapping("/recent")
	public ResponseEntity<List<BatchProcess>> getRecentProcesses() {
		List<BatchProcess> processes = batchService.getRecentProcesses(5);
		return ResponseEntity.ok(processes);
	}

	@PostMapping("/cancel/{id}")
	public ResponseEntity<Void> cancelProcess(@PathVariable Long id) {
		try {
			batchService.cancelProcess(id);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

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