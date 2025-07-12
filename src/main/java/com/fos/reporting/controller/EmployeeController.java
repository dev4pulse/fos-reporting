package com.fos.reporting.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fos.reporting.domain.EmployeeDto;
import com.fos.reporting.domain.LoginDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fos.reporting.entity.Employee;
import com.fos.reporting.service.EmployeeService;

@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employee")
    public ResponseEntity<String> addEntry(@Valid @RequestBody EmployeeDto employeeDto) {
        try {
            Employee saved = employeeService.registerEmployee(employeeDto);
            return ResponseEntity.ok("Added to employee with ID: " + saved.getEmployeeId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequest, HttpSession session) {
        System.out.println("request received" );

        Optional<Employee> emp = employeeService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return emp.map(employee -> {session.setAttribute("employeeId", employee.getEmployeeId());
            return ResponseEntity.ok(Map.of("status", "success", "employeeId", employee.getEmployeeId()));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("status", "fail", "message", "Invalid credentials")));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Employee>> getActiveEmployees() {
        try {
            return ResponseEntity.ok(employeeService.getActiveEmployees());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}