package com.fos.reporting.controller;

import com.fos.reporting.entity.ProfitLoss;
import com.fos.reporting.service.ProfitLossService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/profit-loss")
public class ProfitLossController {

    private final ProfitLossService profitLossService;

    public ProfitLossController(ProfitLossService profitLossService) {
        this.profitLossService = profitLossService;
    }

    /**
     * Fetch all historical Profit/Loss records
     */
    @GetMapping("/all")
    public List<ProfitLoss> getAllProfitLoss() {
        return profitLossService.getAllProfitLoss();
    }


}
