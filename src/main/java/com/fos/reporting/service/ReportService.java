package com.fos.reporting.service;

import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReportService {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    private SalesRepository salesRepository;
    public boolean addToSales(EntryProduct entryProduct) {
        try {
        entryProduct.getProducts().forEach(product -> {
            Sales sales = new Sales();
            sales.setDateTime(LocalDateTime.parse(entryProduct.getDate(), formatter));
            sales.setProductName(product.getProductName());
            sales.setSubProduct(product.getSubProduct());
            sales.setEmployeeId(entryProduct.getEmployeeId());
            Sales recentSale = salesRepository.findTopByProductNameAndSubProductOrderByDateTimeDesc(sales.getProductName(),sales.getSubProduct());
            sales.setClosingStock(product.getClosing());
            float closingStock = 0;
            if (recentSale != null) {
                closingStock = recentSale.getClosingStock();
            }
            sales.setOpeningStock(closingStock);
            sales.setTestingTotal(product.getTesting());
            float sale = product.getClosing() - closingStock - product.getTesting();
            sales.setSale(sale);
            sales.setPrice(product.getPrice());
            sales.setSaleAmount(sale * product.getPrice());
            salesRepository.save(sales);
        });
            return true;
        }catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }
}
