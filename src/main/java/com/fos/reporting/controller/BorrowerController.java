package com.fos.reporting.controller;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.service.BorrowerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing borrower (credit customer) records.
 */
@Slf4j
@RestController
@RequestMapping("/borrowers") // Standardized API path
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    /**
     * POST /borrowers : Creates a new borrower record.
     */
    @PostMapping
    public ResponseEntity<BorrowerDto> createBorrower(@Valid @RequestBody BorrowerDto borrowerDto) {
        log.info("Request to create borrower: {}", borrowerDto.getCustomerName());
        try {
            BorrowerDto savedBorrower = borrowerService.createBorrower(borrowerDto);
            log.debug("Borrower created successfully: {}", savedBorrower);
            return new ResponseEntity<>(savedBorrower, HttpStatus.CREATED);
        } catch (Exception ex) {
            log.error("Error creating borrower: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /borrowers : Retrieves borrower records.
     */
    @GetMapping
    public ResponseEntity<List<BorrowerDto>> getBorrowers(@RequestParam(required = false) String customerName) {
        log.info("Request to fetch borrowers. customerName={}", customerName);
        try {
            List<BorrowerDto> borrowers = borrowerService.findBorrowers(customerName);
            log.debug("Fetched {} borrowers", borrowers.size());
            return ResponseEntity.ok(borrowers);
        } catch (Exception ex) {
            log.error("Error fetching borrowers: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /borrowers/{id} : Updates an existing borrower record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BorrowerDto> updateBorrower(
            @PathVariable Long id,
            @Valid @RequestBody BorrowerDto borrowerDto
    ) {
        log.info("Request to update borrower id={}", id);
        try {
            BorrowerDto updatedBorrower = borrowerService.updateBorrower(id, borrowerDto);
            log.debug("Borrower updated successfully: {}", updatedBorrower);
            return ResponseEntity.ok(updatedBorrower);
        } catch (Exception ex) {
            log.error("Error updating borrower with id {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /borrowers/remove/{id} : Deletes a borrower record.
     */
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> deleteBorrower(@PathVariable Long id) {
        log.info("Request to delete borrower id={}", id);
        try {
            borrowerService.deleteBorrower(id);
            log.debug("Borrower deleted successfully: id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            log.error("Error deleting borrower with id {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
