package com.mhj.auth.jwt.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("teste")
public class TesteController {

	@GetMapping
	public ResponseEntity<Map<String, Object>> getServiceOne() {
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("messageTeste", "Teste is up");
		return ResponseEntity.ok(response);
	}
}
