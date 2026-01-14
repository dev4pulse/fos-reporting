package com.fos.reporting.service;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.repository.BorrowerHistoryRepository;
import com.fos.reporting.repository.BorrowerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BorrowerService {

    private static final Logger log = LoggerFactory.getLogger(BorrowerService.class);

    private final BorrowerRepository borrowerRepository;
    private final BorrowerHistoryService historyService;
    private final BorrowerHistoryRepository historyRepository;

    public BorrowerService(BorrowerRepository borrowerRepository,
                           BorrowerHistoryService historyService,
                           BorrowerHistoryRepository historyRepository) {
        this.borrowerRepository = borrowerRepository;
        this.historyService = historyService;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public BorrowerDto createBorrower(BorrowerDto dto) {
        try {
            if (dto.getBorrowDate() == null) {
                dto.setBorrowDate(LocalDateTime.now());
            }
            Borrower borrower = toEntity(dto);
            Borrower savedBorrower = borrowerRepository.save(borrower);
            log.info("Borrower created successfully: {}", savedBorrower);
            return toDto(savedBorrower);
        } catch (Exception e) {
            log.error("Error creating borrower: {}", dto, e);
            throw e;
        }
    }

    @Transactional
    public BorrowerDto updateBorrower(Long id, BorrowerDto dto) {
        try {
            Borrower existingBorrower = borrowerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Borrower not found with id: " + id));

            historyService.saveHistory(existingBorrower, dto.getDuePaid(), dto.getExtraBorrowed());

            updateEntityFromDto(existingBorrower, dto);
            Borrower updatedBorrower = borrowerRepository.save(existingBorrower);
            log.info("Borrower updated successfully: {}", updatedBorrower);
            return toDto(updatedBorrower);
        } catch (Exception e) {
            log.error("Error updating borrower with ID {}: {}", id, dto, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<BorrowerDto> findBorrowers(String customerName) {
        try {
            List<Borrower> borrowers;
            if (StringUtils.hasText(customerName)) {
                borrowers = borrowerRepository.findByCustomerNameContainingIgnoreCaseOrderByBorrowDateDesc(customerName);
            } else {
                borrowers = borrowerRepository.findAllByOrderByBorrowDateDesc();
            }
            log.info("Fetched {} borrowers", borrowers.size());
            return borrowers.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching borrowers with filter: {}", customerName, e);
            throw e;
        }
    }

    @Transactional
    public void deleteBorrower(Long id) {
        try {
            Borrower borrower = borrowerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Borrower not found with id: " + id));

            // Step 1: Delete borrower history
            historyRepository.deleteAll(
                    historyRepository.findByBorrowerOrderByUpdatedAtDesc(borrower)
            );

            // Step 2: Delete borrower
            borrowerRepository.delete(borrower);

            log.info("Borrower and related history deleted successfully for ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting borrower with ID: {}", id, e);
            throw e;
        }
    }

    // --- Helper Methods ---
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
