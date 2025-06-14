package com.mhj.crud.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.mhj.crud.dto.EmployeeDTO;
import com.mhj.crud.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
	
	public EmployeeDTO toDTO(Employee employee);
	
	public Employee toEntity(EmployeeDTO employeeDTO);

	@Mapping(target = "id", ignore = true)
	public void updateEmployeeFromDTO(EmployeeDTO employeeDTO, @MappingTarget Employee employee);

}
