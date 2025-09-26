package com.fos.reporting.service;

import com.fos.reporting.entity.Collections;
import com.fos.reporting.entity.ProfitLoss;
import com.fos.reporting.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class ProfitLossService {

    private final CollectionsRepository collectionsRepository;
    private final ExpenseRepository expenseRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final ProductRepository productRepository;
    private final ProfitLossRepository profitLossRepository;
    private final ExpenseService expenseService;

    public ProfitLossService(CollectionsRepository collectionsRepository,
                             ExpenseRepository expenseRepository,
                             InventoryLogRepository inventoryLogRepository,
                             ProductRepository productRepository,
                             ProfitLossRepository profitLossRepository,
                             ExpenseService expenseService) {
        this.collectionsRepository = collectionsRepository;
        this.expenseRepository = expenseRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.productRepository = productRepository;
        this.profitLossRepository = profitLossRepository;
        this.expenseService = expenseService;
    }

    /**
     * Calculates and saves Profit/Loss ensuring proper decimal formatting for DB storage.
     */
    @Transactional
    public ProfitLoss calculateAndSaveProfitLoss() {
        try {
            // 1️⃣ Latest Cash Received
            double cashReceived = collectionsRepository.findTopByOrderByIdDesc()
                    .map(Collections::getReceivedTotal)
                    .orElse(0.0);

            log.debug("Latest Cash Received: {}", cashReceived);

            // 2️⃣ Total Expenses (current month)
            BigDecimal totalExpenses = expenseService.calculateCurrentMonthTotalExpenses()
                    .setScale(2, RoundingMode.HALF_UP);
            double expenses = totalExpenses.doubleValue();

            log.debug("Current Month Expenses: {}", expenses);

            // 3️⃣ Inventory Value (sum of all products)
            double inventoryValue = productRepository.findAll().stream()
                    .mapToDouble(p -> {
                        double latestValue = inventoryLogRepository
                                .findTopByProductIdOrderByTransactionDateDesc(p.getId())
                                .map(log -> log.getCurrentLevel() * p.getPrice().doubleValue())
                                .orElse(0.0);

                        // Round to 2 decimals
                        BigDecimal rounded = BigDecimal.valueOf(latestValue)
                                .setScale(2, RoundingMode.HALF_UP);
                        return rounded.doubleValue();
                    })
                    .sum();

            // Round total inventory value
            inventoryValue = BigDecimal.valueOf(inventoryValue)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            log.debug("Total Inventory Value: {}", inventoryValue);

            // 4️⃣ Profit/Loss Calculation
            double profitLoss = BigDecimal.valueOf(cashReceived + inventoryValue - expenses)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            log.info("Calculated Profit/Loss: {}", profitLoss);

            // 5️⃣ Save to DB
            ProfitLoss pl = new ProfitLoss();
            pl.setCashReceived(cashReceived);
            pl.setTotalExpenses(expenses);
            pl.setInventoryValue(inventoryValue);
            pl.setProfitLoss(profitLoss);
            pl.setCalculatedAt(java.time.LocalDateTime.now());

            ProfitLoss saved = profitLossRepository.save(pl);

            log.info("Profit/Loss saved at {} → {}", saved.getCalculatedAt(), saved.getProfitLoss());
            return saved;

        } catch (Exception e) {
            log.error("Failed to calculate Profit/Loss", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch all Profit/Loss records
     */
    public List<ProfitLoss> getAllProfitLoss() {
        return profitLossRepository.findAll();
    }

    // ---------------- Debug Helpers ----------------

    public double debugCash() {
        return collectionsRepository.findTopByOrderByIdDesc()
                .map(Collections::getReceivedTotal)
                .orElse(0.0);
    }

    public double debugExpenses() {
        BigDecimal totalExpenses = expenseService.calculateCurrentMonthTotalExpenses()
                .setScale(2, RoundingMode.HALF_UP);
        return totalExpenses.doubleValue();
    }

    public double debugInventory() {
        double inventoryValue = productRepository.findAll().stream()
                .mapToDouble(p -> {
                    double latestValue = inventoryLogRepository
                            .findTopByProductIdOrderByTransactionDateDesc(p.getId())
                            .map(log -> log.getCurrentLevel() * p.getPrice().doubleValue())
                            .orElse(0.0);
                    return BigDecimal.valueOf(latestValue)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                })
                .sum();

        return BigDecimal.valueOf(inventoryValue)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
