package com.fos.reporting.service;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.repository.BorrowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    // Use constructor injection - it's safer and better for testing.
    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    @Transactional
    public BorrowerDto createBorrower(BorrowerDto dto) {
        if (dto.getBorrowDate() == null) {
            dto.setBorrowDate(LocalDateTime.now());
        }

        Borrower borrower = toEntity(dto);
        Borrower savedBorrower = borrowerRepository.save(borrower);
        return toDto(savedBorrower);
    }

    @Transactional
    public BorrowerDto updateBorrower(Long id, BorrowerDto dto) {
        Borrower existingBorrower = borrowerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Borrower not found with id: " + id));

        // Update properties from the DTO
        updateEntityFromDto(existingBorrower, dto);

        Borrower updatedBorrower = borrowerRepository.save(existingBorrower);
        return toDto(updatedBorrower);
    }

    @Transactional(readOnly = true)
    public List<BorrowerDto> findBorrowers(String customerName) {
        List<Borrower> borrowers;
        if (StringUtils.hasText(customerName)) {
            borrowers = borrowerRepository.findByCustomerNameContainingIgnoreCaseOrderByBorrowDateDesc(customerName);
        } else {
            // Use the new sorted method for a consistent default order
            borrowers = borrowerRepository.findAllByOrderByBorrowDateDesc();
        }
        return borrowers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // --- Helper Methods for Mapping ---

    private BorrowerDto toDto(Borrower entity) {
        BorrowerDto dto = new BorrowerDto();
        dto.setId(entity.getId());
        dto.setCustomerName(entity.getCustomerName());
        dto.setCustomerVehicle(entity.getCustomerVehicle());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setAmountBorrowed(entity.getAmountBorrowed());
        dto.setBorrowDate(entity.getBorrowDate());
        dto.setDueDate(entity.getDueDate());
        dto.setStatus(entity.getStatus());
        dto.setNotes(entity.getNotes());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        return dto;
    }

    private Borrower toEntity(BorrowerDto dto) {
        Borrower entity = new Borrower();
        // We don't set the ID here, it will be generated on save.
        updateEntityFromDto(entity, dto);
        return entity;
    }

    private void updateEntityFromDto(Borrower entity, BorrowerDto dto) {
        entity.setCustomerName(dto.getCustomerName());
        entity.setCustomerVehicle(dto.getCustomerVehicle());
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setAmountBorrowed(dto.getAmountBorrowed());
        entity.setBorrowDate(dto.getBorrowDate());
        entity.setDueDate(dto.getDueDate());
        entity.setStatus(dto.getStatus());
        entity.setNotes(dto.getNotes());
        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
    }
}