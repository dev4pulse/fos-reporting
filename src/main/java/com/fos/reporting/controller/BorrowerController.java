package com.fos.reporting.controller;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.service.BorrowerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing borrower (credit customer) records.
 */
@RestController
@RequestMapping("/borrowers") // UPDATED: Standardized API path
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    /**
     * POST /api/borrowers : Creates a new borrower record.
     */
    @PostMapping
    public ResponseEntity<BorrowerDto> createBorrower(@Valid @RequestBody BorrowerDto borrowerDto) {
        BorrowerDto savedBorrower = borrowerService.createBorrower(borrowerDto);
        return new ResponseEntity<>(savedBorrower, HttpStatus.CREATED);
    }

    /**
     * GET /api/borrowers : Retrieves borrower records.
     * If customerName is provided, it returns that customer's transaction history,
     * sorted by the most recent transaction first.
     */
    @GetMapping
    public ResponseEntity<List<BorrowerDto>> getBorrowers(@RequestParam(required = false) String customerName) {
        List<BorrowerDto> borrowers = borrowerService.findBorrowers(customerName);
        return ResponseEntity.ok(borrowers);
    }

    /**
     * PUT /api/borrowers/{id} : Updates an existing borrower record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BorrowerDto> updateBorrower(@PathVariable Long id, @Valid @RequestBody BorrowerDto borrowerDto) {
        BorrowerDto updatedBorrower = borrowerService.updateBorrower(id, borrowerDto);
        return ResponseEntity.ok(updatedBorrower);
    }
}