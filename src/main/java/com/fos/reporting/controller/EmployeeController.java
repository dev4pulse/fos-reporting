package com.fos.reporting.controller;

import java.util.List;

import com.fos.reporting.domain.EmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.fos.reporting.entity.Employee;
import com.fos.reporting.service.EmployeeService;

@RestController
public class EmployeeController {
    @Autowired
    private final EmployeeService employeeService;
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @GetMapping("/active")
    public ResponseEntity<List<Employee>> getActiveEmployees() {
        try {
            List<Employee> activeEmployees = employeeService.getActiveEmployees();
            return new ResponseEntity<>(activeEmployees, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("e" + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/employee")
    public ResponseEntity<String> addEntry(@RequestBody @Validated EmployeeDto employeeDto ) {
        try {
            if (employeeService.registerEmployee(employeeDto).isActive()) {
                return new ResponseEntity<>("added to employee", HttpStatus.OK);
            }
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.out.println("e" + e);
            return new ResponseEntity<>("failed exception", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}