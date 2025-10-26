package com.example.designation.service;

import com.example.designation.dto.FaixaCepInputDTO;
import com.example.designation.dto.FaixaCepResponseDTO;
import com.example.designation.entity.FaixaCep;
import com.example.designation.entity.OperadorLogistico;
import com.example.designation.mapper.FaixaCepMapper;
import com.example.designation.repository.FaixaCepRepository;
import com.example.designation.repository.OperadorLogisticoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FaixaCepServiceImpl implements FaixaCepService {

	private final FaixaCepRepository faixaCepRepository;
	private final OperadorLogisticoRepository operadorLogisticoRepository;
	private final FaixaCepMapper mapper;

	public FaixaCepServiceImpl(FaixaCepRepository faixaCepRepository,
			OperadorLogisticoRepository operadorLogisticoRepository, FaixaCepMapper mapper) {
		this.faixaCepRepository = faixaCepRepository;
		this.operadorLogisticoRepository = operadorLogisticoRepository;
		this.mapper = mapper;
	}

	@Override
	@Transactional
	public FaixaCepResponseDTO criar(FaixaCepInputDTO dto) {
		// 1. Validação de sobreposição
		if (faixaCepRepository.existsOverlappingRange(dto.getCepInicial(), dto.getCepFinal())) {
			// Lança uma exceção de negócio. HttpStatus 409 Conflict ou 400 Bad Request são
			// apropriados.
			throw new IllegalArgumentException(
					String.format("A faixa de CEP [%s-%s] se sobrepõe com uma faixa já existente.", dto.getCepInicial(),
							dto.getCepFinal()));
		}

		// 2. Busca o operador logístico (lógica que já tínhamos)
		OperadorLogistico operador = operadorLogisticoRepository.findById(dto.getOperadorLogisticoId())
				.orElseThrow(() -> new EntityNotFoundException(
						"Operador Logístico não encontrado: " + dto.getOperadorLogisticoId()));

		// 3. Mapeia e salva a entidade
		FaixaCep novaFaixa = mapper.toEntity(dto);
		novaFaixa.setOperadorLogistico(operador);

		FaixaCep faixaSalva = faixaCepRepository.save(novaFaixa);

		return mapper.toResponseDTO(faixaSalva);

	}

	@Override
	public Page<FaixaCepResponseDTO> listar(Pageable pageable) {
		return faixaCepRepository.findAll(pageable).map(mapper::toResponseDTO);
	}

	@Override
	public FaixaCepResponseDTO buscarPorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public FaixaCepResponseDTO atualizar(Long id, FaixaCepInputDTO dto) {
		// Validação de sobreposição para atualização
		if (faixaCepRepository.existsOverlappingRangeForUpdate(id, dto.getCepInicial(), dto.getCepFinal())) {
			throw new IllegalArgumentException("A faixa de CEP se sobrepõe com outra faixa existente.");
		}

		FaixaCep faixaExistente = faixaCepRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Faixa de CEP não encontrada: " + id));

		OperadorLogistico operador = operadorLogisticoRepository.findById(dto.getOperadorLogisticoId())
				.orElseThrow(() -> new EntityNotFoundException(
						"Operador Logístico não encontrado: " + dto.getOperadorLogisticoId()));

		// Atualiza os campos da entidade existente
		faixaExistente.setCepInicial(dto.getCepInicial());
		faixaExistente.setCepFinal(dto.getCepFinal());
		faixaExistente.setCidade(dto.getCidade());
		faixaExistente.setUf(dto.getUf());
		faixaExistente.setTipoEntrega(dto.getTipoEntrega());
		faixaExistente.setOperadorLogistico(operador);

		return mapper.toResponseDTO(faixaCepRepository.save(faixaExistente));
	}

	@Override
	public void deletar(Long id) {
		// TODO Auto-generated method stub

	}

	// Implementar os outros métodos (buscarPorId, atualizar, deletar) seguindo o
	// mesmo padrão...
}
