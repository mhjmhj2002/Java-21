package com.mhj.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

	private Long id;
	private String firstName;
	private String lastName;
	private String role;

	EmployeeDTO(String firstName, String lastName, String role) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}
}
