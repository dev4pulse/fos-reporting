package com.fos.reporting.controller;

import com.fos.reporting.domain.BorrowerHistoryDto;
import com.fos.reporting.entity.BorrowerHistory;
import com.fos.reporting.service.BorrowerHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/borrower-history")
public class BorrowerHistoryController {

    private final BorrowerHistoryService historyService;

    public BorrowerHistoryController(BorrowerHistoryService historyService) {
        this.historyService = historyService;
    }


    // Get history by borrower ID
    @GetMapping("/id/{borrowerId}")
    public ResponseEntity<List<BorrowerHistoryDto>> getHistoryById(@PathVariable Long borrowerId) {
        List<BorrowerHistoryDto> history = historyService.getHistory(borrowerId);
        return ResponseEntity.ok(history);
    }

    // Search history by customer name
    @GetMapping("/search")
    public ResponseEntity<List<BorrowerHistoryDto>> getHistoryByCustomerName(@RequestParam String customerName) {
        List<BorrowerHistoryDto> history = historyService.getHistoryByCustomerName(customerName);
        return ResponseEntity.ok(history);
    }

    // Get all Borrowers History
    @GetMapping("/all")
    public ResponseEntity<List<BorrowerHistoryDto>> getAllBorrowersHistory() {
        try {
            List<BorrowerHistoryDto> borrowerHistory = historyService.getAllBorrowersHistory();
            return ResponseEntity.ok(borrowerHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
