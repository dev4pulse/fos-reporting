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
    public ResponseEntity<String> addEntry(@Valid @RequestBody EmployeeDto employeeDto) {
        try {
            Employee saved = employeeService.registerEmployee(employeeDto);
            return ResponseEntity.ok("Added to employee with ID: " + saved.getEmployeeId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Exception: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequest, HttpSession session) {
        Optional<Employee> emp = employeeService.login(loginRequest.getUsername(), loginRequest.getPassword());

        if (emp.isPresent()) {
            session.setAttribute("employeeId", emp.get().getEmployeeId()); // Store in session
            return ResponseEntity.ok(Map.of("status", "success", "employeeId", emp.get().getEmployeeId()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", "fail", "message", "Invalid credentials"));
    }

}