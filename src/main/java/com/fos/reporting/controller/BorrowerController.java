package com.fos.reporting.controller;

import com.fos.reporting.domain.BorrowerDto;
import com.fos.reporting.service.BorrowerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {
    @Autowired
    private BorrowerService borrowerService;
    @PostMapping
    public ResponseEntity<String> createBorrower(@Valid @RequestBody BorrowerDto dto) {
        borrowerService.createBorrower(dto);
        return ResponseEntity.ok("Borrower saved");
    }
}
