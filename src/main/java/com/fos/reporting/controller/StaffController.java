
package com.fos.reporting.controller;

import com.fos.reporting.domain.StaffDto;
import com.fos.reporting.entity.Employee;
import com.fos.reporting.repository.EmployeeRepository;
import com.fos.reporting.service.StaffService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;
    private final EmployeeRepository employeeRepository;

    public StaffController(StaffService staffService, EmployeeRepository employeeRepository) {
        this.staffService = staffService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<StaffDto> getAllStaff() {
        return staffService.getAllStaff();
    }

    @PostMapping
    public StaffDto createStaff(@RequestBody StaffDto staffDto) {
        Employee employee = employeeRepository.findById(staffDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return staffService.createStaff(staffDto, employee);
    }
}