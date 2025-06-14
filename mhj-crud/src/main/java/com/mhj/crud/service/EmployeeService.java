package com.mhj.crud.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mhj.crud.dto.EmployeeDTO;
import com.mhj.crud.entity.Employee;
import com.mhj.crud.mapper.EmployeeMapper;
import com.mhj.crud.repository.EmployeeRepository;

@Service
public class EmployeeService {

	private EmployeeRepository employeeRepository;

	private EmployeeMapper employeeMapper;

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
		this.employeeRepository = employeeRepository;
		this.employeeMapper = employeeMapper;
	}

	public EmployeeDTO save(EmployeeDTO employeeDTO) {
		Employee employee = employeeMapper.toEntity(employeeDTO);
		Employee savedEmployee = employeeRepository.save(employee);
		return employeeMapper.toDTO(savedEmployee);
	}

	public List<EmployeeDTO> findAll() {
		Iterable<Employee> employeesIterable = employeeRepository.findAll();
		List<Employee> employeesList = StreamSupport.stream(employeesIterable.spliterator(), false).collect(Collectors.toList());
		List<EmployeeDTO> employeesDto = employeesList
				  .stream()
				  .map(employee -> employeeMapper.toDTO(employee))
				  .collect(Collectors.toList());
		return employeesDto;
	}

	public EmployeeDTO findById(Long id) {
		Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found, id: " + id));
		return employeeMapper.toDTO(employee);
	}

	public EmployeeDTO update(Long id, EmployeeDTO employeeDTO) {
		Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found, id: " + id));

		employeeMapper.updateEmployeeFromDTO(employeeDTO, employee);

		Employee updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.toDTO(updatedEmployee);
	}

	public void deleteById(Long id) {
		employeeRepository.deleteById(id);		
	}

}
