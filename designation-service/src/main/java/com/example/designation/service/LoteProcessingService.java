package com.example.designation.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.designation.aggregator.LoteProgressAggregator;
import com.example.designation.dto.FaixaCepInputDTO;
import com.example.designation.entity.ItemLote;
import com.example.designation.entity.OperadorLogistico;
import com.example.designation.entity.StatusProcessamento;
import com.example.designation.repository.ItemLoteRepository;
import com.example.designation.repository.OperadorLogisticoRepository;

/**
 * Serviço responsável por processar os sub-lotes em segundo plano. Ao final do
 * processamento de um sub-lote, ele publica uma mensagem para a fila de
 * verificação para que o lote pai seja finalizado.
 */
@Service
public class LoteProcessingService {

	private static final Logger log = LoggerFactory.getLogger(LoteProcessingService.class);

	private final ItemLoteRepository itemLoteRepository;
	private final OperadorLogisticoRepository operadorRepository;
	private final FaixaCepService faixaCepService;
	private final LoteProgressAggregator progressAggregator;

	public LoteProcessingService(ItemLoteRepository itemLoteRepository, OperadorLogisticoRepository operadorRepository,
			FaixaCepService faixaCepService, LoteProgressAggregator progressAggregator) {
		this.itemLoteRepository = itemLoteRepository;
		this.operadorRepository = operadorRepository;
		this.faixaCepService = faixaCepService;
		this.progressAggregator = progressAggregator;
	}

	@Transactional
	public void processarSubLote(String loteId) {

		AtomicInteger sucessos = new AtomicInteger(0);
		AtomicInteger erros = new AtomicInteger(0);

		List<ItemLote> itensPendentes = itemLoteRepository.findByLoteIdAndStatus(loteId, StatusProcessamento.PENDENTE);
		if (itensPendentes.isEmpty()) {
			log.warn("Nenhum item pendente encontrado para o loteId: {}. Nenhum processamento necessário.", loteId);
			return;
		}

		final Long lotePaiId = itensPendentes.get(0).getLote().getId();

		for (ItemLote item : itensPendentes) {
			if (processarRegistro(item)) { // processarRegistro agora retorna boolean
				sucessos.incrementAndGet();
			} else {
				erros.incrementAndGet();
			}
		}
		
		itemLoteRepository.saveAll(itensPendentes);

		progressAggregator.registrarProgresso(lotePaiId, sucessos.get(), erros.get());

		log.info("Processamento do sub-lote {} concluído.", loteId);

	}

	private boolean processarRegistro(ItemLote item) {
		try {
			OperadorLogistico operador = operadorRepository.findByNome(item.getOperadorLogisticoNome())
					.orElseThrow(() -> new IllegalArgumentException(
							"Operador logístico não encontrado: " + item.getOperadorLogisticoNome()));

			FaixaCepInputDTO dto = new FaixaCepInputDTO();
			dto.setCepInicial(item.getCepInicial());
			dto.setCepFinal(item.getCepFinal());
			dto.setCidade(item.getCidade());
			dto.setUf(item.getUf());
			dto.setTipoEntrega(item.getTipoEntrega());
			dto.setOperadorLogisticoId(operador.getId());
			
			  // 1. VERIFICA antes de tentar criar, usando o novo método.
            if (faixaCepService.existeSobreposicao(dto)) {
                // Se sobrepõe, lança uma exceção controlada que será pega abaixo.
                throw new IllegalStateException("Faixa de CEP sobreposta a uma já existente.");
            }

			faixaCepService.criar(dto);

			item.setStatus(StatusProcessamento.PROCESSADO);
			itemLoteRepository.save(item);
			return true;
		} catch (Exception e) {
			item.setStatus(StatusProcessamento.ERRO);
			item.setMensagemErro(e.getMessage().substring(0, Math.min(e.getMessage().length(), 512)));
			itemLoteRepository.save(item);
			return false;
		}
	}
}
