package com.fos.reporting.service;
/*
import com.fos.reporting.domain.EmployeeDto;

import com.fos.reporting.entity.Employee;

import com.fos.reporting.repository.EmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmployeeService {
    @Autowired private EmployeeRepository employeeRepository;
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByIsActiveTrue();
    }
    public boolean addToEmployee(EmployeeDto employeeDto) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto, employee);
        employeeRepository.save(employee);
        return true;
    }
}
*/

import com.fos.reporting.domain.EmployeeDto;
import com.fos.reporting.entity.Employee;
import com.fos.reporting.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing Employee-related business logic.
 * This class handles operations such as fetching active employees and registering new ones.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository; // 1. Use 'final' for immutability

    // 2. Constructor Injection: Preferred way to inject dependencies.
    // Spring automatically handles the @Autowired for single constructors since Spring 4.3.
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Retrieves a list of all employees who are currently active.
     *
     * @return A list of active Employee entities.
     */
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByIsActiveTrue();
    }

    /**
     * Registers a new employee in the system.
     * This method maps the data from the EmployeeDto to an Employee entity,
     * sets default values (like isActive), and persists the new employee.
     *
     * @param employeeDto The Data Transfer Object containing the new employee's details.
     * @return The newly created Employee entity, including its generated ID.
     */
    public Employee registerEmployee(EmployeeDto employeeDto) { // 3. Renamed and changed return type
        Employee newEmployee = new Employee();

        // 4. Explicit Mapping: More controlled and readable than BeanUtils.copyProperties.
        // This ensures only desired properties are copied and allows for default values.
        newEmployee.setEmployeeFirstName(employeeDto.getEmployeeFirstName());
        newEmployee.setEmployeeLastName(employeeDto.getEmployeeLastName());
        newEmployee.setEmployeeEmail(employeeDto.getEmployeeEmail());
        newEmployee.setEmployeePhoneNumber(employeeDto.getEmployeePhoneNumber()); // Ensure this is String in Employee entity
        newEmployee.setEmployeeRole(employeeDto.getEmployeeRole());
        newEmployee.setEmployeeSalary(employeeDto.getEmployeeSalary());
        newEmployee.setStartDate(employeeDto.getStartDate());
        newEmployee.setEndDate(employeeDto.getEndDate());

        // 5. Set default values for new employees
        newEmployee.setActive(true); // New employees are active by default

        // The save method will perform an INSERT and return the entity with its new ID.
        return employeeRepository.save(newEmployee);
    }
}