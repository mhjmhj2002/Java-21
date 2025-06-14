package com.mhj.crud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mhj.crud.dto.EmployeeDTO;
import com.mhj.crud.entity.Employee;
import com.mhj.crud.mapper.EmployeeMapper;
import com.mhj.crud.repository.EmployeeRepository;

class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private EmployeeMapper employeeMapper;

	@InjectMocks
	private EmployeeService employeeService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSave() {
		// Arrange
		EmployeeDTO employeeDTO = new EmployeeDTO();
		Employee employee = new Employee();
		Employee savedEmployee = new Employee();
		EmployeeDTO savedEmployeeDTO = new EmployeeDTO();

		when(employeeMapper.toEntity(employeeDTO)).thenReturn(employee);
		when(employeeRepository.save(employee)).thenReturn(savedEmployee);
		when(employeeMapper.toDTO(savedEmployee)).thenReturn(savedEmployeeDTO);

		// Act
		EmployeeDTO result = employeeService.save(employeeDTO);

		// Assert
		assertNotNull(result);
		assertEquals(savedEmployeeDTO, result);
		verify(employeeRepository, times(1)).save(employee);
	}

	@Test
	void testFindAll() {
		// Arrange
		Employee employee1 = new Employee();
		Employee employee2 = new Employee();
		EmployeeDTO employeeDTO1 = new EmployeeDTO();
		EmployeeDTO employeeDTO2 = new EmployeeDTO();

		when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));
		when(employeeMapper.toDTO(employee1)).thenReturn(employeeDTO1);
		when(employeeMapper.toDTO(employee2)).thenReturn(employeeDTO2);

		// Act
		var result = employeeService.findAll();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(employeeRepository, times(1)).findAll();
	}

	@Test
	void testFindById() {
		// Arrange
		Long id = 1L;
		Employee employee = new Employee();
		EmployeeDTO employeeDTO = new EmployeeDTO();

		when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
		when(employeeMapper.toDTO(employee)).thenReturn(employeeDTO);

		// Act
		EmployeeDTO result = employeeService.findById(id);

		// Assert
		assertNotNull(result);
		assertEquals(employeeDTO, result);
		verify(employeeRepository, times(1)).findById(id);
	}

	@Test
	void testUpdate() {
		// Arrange
		Long id = 1L;
		EmployeeDTO employeeDTO = new EmployeeDTO();
		Employee employee = new Employee();
		Employee updatedEmployee = new Employee();
		EmployeeDTO updatedEmployeeDTO = new EmployeeDTO();

		when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));
		when(employeeRepository.save(employee)).thenReturn(updatedEmployee);
		when(employeeMapper.toDTO(updatedEmployee)).thenReturn(updatedEmployeeDTO);

		// Act
		EmployeeDTO result = employeeService.update(id, employeeDTO);

		// Assert
		assertNotNull(result);
		assertEquals(updatedEmployeeDTO, result);
		verify(employeeRepository, times(1)).save(employee);
	}

	@Test
	void testDeleteById() {
		// Arrange
		Long id = 1L;

		// Act
		employeeService.deleteById(id);

		// Assert
		verify(employeeRepository, times(1)).deleteById(id);
	}

	@Test
	void testFindByIdThrowsExceptionWhenEmployeeNotFound() {
		// Arrange
		Long id = 1L;
		when(employeeRepository.findById(id)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> employeeService.findById(id), "Employee not found, id: " + id);
	}

	@Test
	void testUpdateThrowsExceptionWhenEmployeeNotFound() {
		// Arrange
		Long id = 1L;
		EmployeeDTO employeeDTO = new EmployeeDTO();
		when(employeeRepository.findById(id)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> employeeService.update(id, employeeDTO),
				"Employee not found, id: " + id);
	}
}
