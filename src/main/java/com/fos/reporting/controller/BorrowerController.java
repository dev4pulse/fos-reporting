package com.fos.reporting.controller;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.service.BorrowerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    @Autowired
    private BorrowerService borrowerService;

    // 1. List/fetch borrowers (all)
    @GetMapping
    public ResponseEntity<List<Borrower>> getAllBorrowers() {
        List<Borrower> list = borrowerService.listBorrowers();
        return ResponseEntity.ok(list);
    }

    // 2. Update borrower by ID
    @PutMapping("/{id}")
    public ResponseEntity<Borrower> updateBorrower(
            @PathVariable Long id,
            @Valid @RequestBody BorrowerDto dto) {
        Borrower updated = borrowerService.updateBorrower(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 3. Customer id with history (by customerName)
    @GetMapping("/history")
    public ResponseEntity<List<Borrower>> getHistoryByCustomerName(@RequestParam String customerName) {
        List<Borrower> history = borrowerService.findByCustomerName(customerName);
        return ResponseEntity.ok(history);
    }

    // (Optional) Create new borrower
    @PostMapping
    public ResponseEntity<Borrower> createBorrower(@Valid @RequestBody BorrowerDto dto) {
        Borrower saved = borrowerService.createBorrower(dto);
        return ResponseEntity.ok(saved);
    }
}
