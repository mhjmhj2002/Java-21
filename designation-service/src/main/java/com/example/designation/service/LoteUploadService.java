package com.example.designation.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.designation.aggregator.LoteProgressAggregator;
import com.example.designation.config.RabbitMQConfig;
import com.example.designation.entity.ItemLote;
import com.example.designation.entity.Lote;
import com.example.designation.repository.LoteRepository;

@Service
public class LoteUploadService {

	private static final Logger log = LoggerFactory.getLogger(LoteUploadService.class);

	private final LoteRepository loteRepository;
	private final SubLoteService subLoteService;
	private final RabbitTemplate rabbitTemplate; // INJETAR O RABBITTEMPLATE AQUI
	private final LoteProgressAggregator progressAggregator;

	public LoteUploadService(LoteRepository loteRepository, SubLoteService subLoteService,
			RabbitTemplate rabbitTemplate, LoteProgressAggregator progressAggregator) { // Adicionar ao construtor
		this.loteRepository = loteRepository;
		this.subLoteService = subLoteService;
		this.rabbitTemplate = rabbitTemplate;
		this.progressAggregator = progressAggregator;
	}

	/**
	 * Orquestra o upload. Este método NÃO precisa ser transacional, pois a
	 * transação é delegada ao SubLoteService para cada pedaço.
	 */
	public Lote processarUpload(MultipartFile file) throws Exception {
		Lote lotePai = new Lote();
		lotePai.setNomeArquivo(file.getOriginalFilename());
		lotePai = loteRepository.save(lotePai);

		final int TAMANHO_LOTE = 500;
		List<ItemLote> subLote = new ArrayList<>(TAMANHO_LOTE);
		int totalItens = 0;
		int subLotesGerados = 0; // Contador de sub-lotes
		
		 // --- IMPLEMENTANDO SUA IDEIA ---
        // Lista para acumular as mensagens (loteIds) a serem enviadas
        List<String> mensagensParaFila = new ArrayList<>();

		try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (row.getRowNum() == 0)
					continue;

				ItemLote item = new ItemLote();
				item.setLote(lotePai);
				item.setCepInicial(row.getCell(0).getStringCellValue());
				item.setCepFinal(row.getCell(1).getStringCellValue());
				item.setCidade(row.getCell(2).getStringCellValue());
				item.setUf(row.getCell(3).getStringCellValue());
				item.setTipoEntrega(row.getCell(4).getStringCellValue());
				item.setOperadorLogisticoNome(row.getCell(5).getStringCellValue());

				subLote.add(item);
				totalItens++;

				if (subLote.size() == TAMANHO_LOTE) {
					log.info("Processando sub-lote de {} itens...", TAMANHO_LOTE);
					// CHAMA O NOVO SERVIÇO
					String loteIdDaTarefa = subLoteService.salvarSubLote(subLote);
					mensagensParaFila.add(loteIdDaTarefa); // Acumula a mensagem
					subLote = new ArrayList<>(TAMANHO_LOTE); // Cria uma nova lista
					subLotesGerados++; // Incrementa o contador
				}
			}
		}

		if (!subLote.isEmpty()) {
			log.info("Processando último sub-lote de {} itens...", subLote.size());
			String loteIdDaTarefa = subLoteService.salvarSubLote(subLote);
			 mensagensParaFila.add(loteIdDaTarefa); // Acumula a última mensagem
			subLotesGerados++; // Incrementa para o último lote
		}

		lotePai.setTotalItens(totalItens);
		lotePai.setTotalSubLotes(subLotesGerados); // Salva o número total de sub-lotes
		loteRepository.save(lotePai);
		
		 // --- ENVIO DAS MENSAGENS APÓS A TRANSAÇÃO ---
        // A transação do 'processarUpload' termina aqui. Se tudo deu certo,
        // os dados estão no banco. Agora, podemos enviar as mensagens com segurança.
        enviarMensagensParaFila(mensagensParaFila);
        
        progressAggregator.iniciarLote(lotePai);

		return lotePai;
	}

	/**
	 * Método auxiliar para enfileirar as mensagens. É chamado após a transação
	 * principal ser concluída.
	 */
	private void enviarMensagensParaFila(List<String> loteIds) {
		log.info("Transação do upload concluída. Enviando {} mensagens para a fila...", loteIds.size());
		for (String loteId : loteIds) {
			rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_PRINCIPAL, RabbitMQConfig.QUEUE_LOTE_FAIXAS, loteId);
		}
		log.info("Todas as mensagens foram enfileiradas.");
	}
}
