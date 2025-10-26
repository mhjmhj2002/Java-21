package com.example.designation.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.designation.entity.ItemLote;
import com.example.designation.repository.ItemLoteRepository;

/**
 * Serviço auxiliar com a única responsabilidade de processar um "sub-lote" de
 * itens em uma transação separada. Isso resolve a referência circular.
 */
@Service
public class SubLoteService {

	private final ItemLoteRepository itemLoteRepository;

	public SubLoteService(ItemLoteRepository itemLoteRepository) {
		this.itemLoteRepository = itemLoteRepository;
	}

	/**
	 * Salva um sub-lote de itens em uma nova transação. Agora, ele retorna o ID do
	 * sub-lote criado para ser enfileirado posteriormente.
	 *
	 * @param subLote A lista de itens a serem salvos.
	 * @return O ID (UUID em String) do sub-lote que foi salvo.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String salvarSubLote(List<ItemLote> subLote) {
		// A lógica de merge/reatribuição do Lote pai ainda é necessária aqui
		// para evitar o erro de chave estrangeira, assumindo que o LoteUploadService
		// não foi alterado.

		String loteIdString = UUID.randomUUID().toString();
		for (ItemLote item : subLote) {
			item.setLoteId(loteIdString);
		}

		itemLoteRepository.saveAll(subLote);

		return loteIdString;
	}
}
