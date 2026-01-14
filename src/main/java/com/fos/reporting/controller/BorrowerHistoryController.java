package com.fos.reporting.controller;

import com.fos.reporting.domain.BorrowerHistoryDto;
import com.fos.reporting.service.BorrowerHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrower-history")
public class BorrowerHistoryController {

    private static final Logger log = LoggerFactory.getLogger(BorrowerHistoryController.class);

    private final BorrowerHistoryService historyService;

    public BorrowerHistoryController(BorrowerHistoryService historyService) {
        this.historyService = historyService;
    }

    // Get history by borrower ID
    @GetMapping("/id/{borrowerId}")
    public ResponseEntity<List<BorrowerHistoryDto>> getHistoryById(@PathVariable Long borrowerId) {
        try {
            List<BorrowerHistoryDto> history = historyService.getHistory(borrowerId);
            log.info("Fetched {} history records for borrower ID {}", history.size(), borrowerId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error fetching history for borrower ID {}", borrowerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search history by customer name
    @GetMapping("/search")
    public ResponseEntity<List<BorrowerHistoryDto>> getHistoryByCustomerName(@RequestParam String customerName) {
        try {
            List<BorrowerHistoryDto> history = historyService.getHistoryByCustomerName(customerName);
            log.info("Fetched {} history records for customer name '{}'", history.size(), customerName);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error fetching history for customer name '{}'", customerName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get all Borrowers History
    @GetMapping("/all")
    public ResponseEntity<List<BorrowerHistoryDto>> getAllBorrowersHistory() {
        try {
            List<BorrowerHistoryDto> borrowerHistory = historyService.getAllBorrowersHistory();
            log.info("Fetched all borrower history records: {}", borrowerHistory.size());
            return ResponseEntity.ok(borrowerHistory);
        } catch (Exception e) {
            log.error("Error fetching all borrower history records", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
