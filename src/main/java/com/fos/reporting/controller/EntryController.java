package com.fos.reporting.controller;

import com.fos.reporting.domain.EntryRequestDto;
import com.fos.reporting.service.ReportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/entry")
public class EntryController {

    private final ReportService salesService;  // Service containing addToSales()
   // Service containing addToCollections()

    public EntryController(ReportService salesService) {
        this.salesService = salesService;
        ;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEntry(@RequestBody EntryRequestDto request) {

        String entryId = java.util.UUID.randomUUID().toString(); // unique entry ID

        boolean salesSuccess = true;
        boolean collectionsSuccess = true;

        if (request.getSaleEntry() != null) {
            salesSuccess = salesService.addToSales(request.getSaleEntry(), entryId);
        }

        if (request.getCollectionsEntry() != null) {
            collectionsSuccess = salesService.addToCollections(request.getCollectionsEntry(), entryId);
        }

        if (salesSuccess && collectionsSuccess) {
            return ResponseEntity.ok("Sales and Collections added successfully.");
        } else if (!salesSuccess && collectionsSuccess) {
            return ResponseEntity.status(500).body("Sales failed, Collections succeeded.");
        } else if (salesSuccess && !collectionsSuccess) {
            return ResponseEntity.status(500).body("Collections failed, Sales succeeded.");
        } else {
            return ResponseEntity.status(500).body("Both Sales and Collections failed.");
        }
    }
}
