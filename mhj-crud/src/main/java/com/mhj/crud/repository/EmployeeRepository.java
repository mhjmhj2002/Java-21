package com.mhj.crud.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mhj.crud.entity.Employee;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    
}
