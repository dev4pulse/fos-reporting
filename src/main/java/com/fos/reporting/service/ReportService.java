package com.fos.reporting.service;

import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class ReportService {

    @Autowired
    private SalesRepository salesRepository;
    public boolean addToSales(EntryProduct entryProduct) {
        try {
        entryProduct.getProducts().forEach(product -> {
            Sales sales = new Sales();
            sales.setEmployeeId(entryProduct.getEmployeeId());
            sales.setClosingStock(product.getClosing());
            sales.setTestingTotal(product.getTesting());
          //  sales.setSale(product)
            sales.setCost(product.getPrice());
            //sales.setInventory(product.get)

            salesRepository.save(sales);
        });
            return true;
        }catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }
}
