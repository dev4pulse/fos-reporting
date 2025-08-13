
package com.fos.reporting.service;

import com.fos.reporting.domain.StaffDto;
import com.fos.reporting.entity.Staff;
import com.fos.reporting.entity.Employee;
import com.fos.reporting.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<StaffDto> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StaffDto createStaff(StaffDto staffDto, Employee employee) {
        Staff staff = new Staff();
        staff.setEmployee(employee);
        staff.setEmployeeName(staffDto.getEmployeeName());
        staff.setRole(staffDto.getRole());
        staff.setStatus(staffDto.getStatus());
        staff.setLoginTime(staffDto.getLoginTime());
        staff.setLogoutTime(staffDto.getLogoutTime());
        // Save as string directly
        staff.setTimeAtWork(staffDto.getTimeAtWork());
        staff.setJoinedDate(staffDto.getJoinedDate());
        staff.setDate(staffDto.getDate());
        Staff saved = staffRepository.save(staff);
        return toDto(saved);
    }

    private StaffDto toDto(Staff staff) {
        return new StaffDto(
                staff.getId(),
                staff.getEmployee().getEmployeeId(),
                staff.getEmployeeName(),
                staff.getRole(),
                staff.getStatus(),
                staff.getLoginTime(),
                staff.getLogoutTime(),
                staff.getTimeAtWork(), // Already a string
                staff.getJoinedDate(),
                staff.getDate()
        );
    }
}