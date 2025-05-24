package com.fos.reporting.service;

import com.fos.reporting.domain.CollectionsDto;
import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.entity.Collections;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.CollectionsRepository;
import com.fos.reporting.repository.SalesRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    private SalesRepository salesRepository;
    @Autowired
    private CollectionsRepository collectionsRepository;
    public boolean addToSales(EntryProduct entryProduct) {
        try {
        entryProduct.getProducts().forEach(product -> {
            Sales sales = new Sales();
            sales.setDateTime(LocalDateTime.parse(entryProduct.getDate(), formatter));
            sales.setProductName(product.getProductName());
            sales.setSubProduct(product.getSubProduct());
            sales.setEmployeeId(entryProduct.getEmployeeId());
            float opening = product.getOpening();
            if (product.getOpening() == 0) {
                Sales recentSale = salesRepository.findTopByProductNameAndSubProductOrderByDateTimeDesc(sales.getProductName(), sales.getSubProduct());
                if (recentSale != null) {
                    opening = recentSale.getClosingStock();
                }
            }
            sales.setClosingStock(product.getClosing());
            sales.setOpeningStock(opening);
            sales.setTestingTotal(product.getTesting());
            float sale = product.getClosing() - opening - product.getTesting();
            sales.setSale(sale);
            sales.setPrice(product.getPrice());
            sales.setSaleAmount(sale * product.getPrice());
            salesRepository.save(sales);
        });
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToCollections(CollectionsDto collectionsDto) {
        try {
            Collections collections = mapToCollections(collectionsDto);
            LocalDateTime requestedTime = LocalDateTime.parse(collectionsDto.getDate(), formatter);
            collections.setEmployeeId(collections.getEmployeeId());
            List<Sales> salesByTime = salesRepository.findByDateTime(requestedTime);
            double expectedTotal = salesByTime.stream().map(Sales::getSaleAmount)
                    .mapToDouble(Float::doubleValue).sum();
            double receivedTotal = collectionsDto.getCashReceived() +
                    collectionsDto.getPhonePay() +
                    collectionsDto.getCreditCard() +
                    collectionsDto.getBorrowedAmount() +
                    collectionsDto.getDebtRecovered() +
                    collectionsDto.getExpenses();
            collections.setExpectedTotal(expectedTotal);
            collections.setReceivedTotal(receivedTotal);
            collections.setDifference(expectedTotal - receivedTotal);
            Collections save = collectionsRepository.save(collections);
            return save.getId() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Collections mapToCollections(CollectionsDto collectionsDto) {
        Collections collections = new Collections();
        BeanUtils.copyProperties(collectionsDto,collections);
        collections.setDateTime(LocalDateTime.parse(collectionsDto.getDate(), formatter));
        return collections;
    }
}
