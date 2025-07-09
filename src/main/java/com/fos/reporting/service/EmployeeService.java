package com.fos.reporting.service;

import com.fos.reporting.domain.EmployeeDto;
import com.fos.reporting.entity.Employee;
import com.fos.reporting.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByIsActiveTrue();
    }

    public Employee registerEmployee(EmployeeDto employeeDto) {
        Employee newEmployee = new Employee();
        newEmployee.setEmployeeFirstName(employeeDto.getEmployeeFirstName());
        newEmployee.setEmployeeLastName(employeeDto.getEmployeeLastName());
        newEmployee.setEmployeeEmail(employeeDto.getEmployeeEmail());
        newEmployee.setEmployeePhoneNumber(employeeDto.getEmployeePhoneNumber());
        newEmployee.setEmployeeRole(employeeDto.getEmployeeRole());
        newEmployee.setEmployeeSalary(employeeDto.getEmployeeSalary());
        newEmployee.setStartDate(employeeDto.getStartDate());
        newEmployee.setEndDate(employeeDto.getEndDate());
        newEmployee.setUsername(employeeDto.getUsername());
        newEmployee.setPassword(employeeDto.getPassword());
        newEmployee.setActive(true); // default
        return employeeRepository.save(newEmployee);
    }

    public Optional<Employee> login(String username, String password) {
        return employeeRepository.findByUsernameAndPassword(username, password);
    }

}