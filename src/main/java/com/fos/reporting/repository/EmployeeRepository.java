package com.fos.reporting.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fos.reporting.entity.Employee;
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByIsActiveTrue();
}