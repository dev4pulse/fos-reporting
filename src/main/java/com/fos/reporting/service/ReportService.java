package com.fos.reporting.service;

import com.fos.reporting.domain.CollectionsDto;
import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.domain.GetReportRequest;
import com.fos.reporting.domain.GetReportResponse;
import com.fos.reporting.domain.ReportData;
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
    private static final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private CollectionsRepository collectionsRepository;

    /**
     * Save a batch of sales entries, auto-filling openingStock when zero.
     */
    public boolean addToSales(EntryProduct entryProduct) {
        try {
            entryProduct.getProducts().forEach(product -> {
                Sales sales = new Sales();
                sales.setDateTime(LocalDateTime.parse(entryProduct.getDate(), formatter));
                sales.setProductName(product.getProductName());
                sales.setSubProduct(product.getSubProduct());
                sales.setEmployeeId(entryProduct.getEmployeeId());

                // if opening==0, pull last closingStock; otherwise use provided opening
                float opening = product.getOpening();
                if (opening == 0f) {
                    opening = getLastClosing(product.getProductName(), product.getSubProduct());
                }
                sales.setOpeningStock(opening);

                sales.setClosingStock(product.getClosing());
                sales.setTestingTotal(product.getTesting());

                // compute sale and saleAmount
                float sale = product.getClosing() - opening - product.getTesting();
                sales.setSale(sale);
                sales.setPrice(product.getPrice());
                sales.setSaleAmount(sale * product.getPrice());

                salesRepository.save(sales);
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lookup the most-recent closingStock for a product/subProduct.
     * Returns 0 if none exists.
     */
    public float getLastClosing(String productName, String subProduct) {
        Sales lastSale = salesRepository
            .findTopByProductNameAndSubProductOrderByDateTimeDesc(productName, subProduct);
        return (lastSale != null ? lastSale.getClosingStock() : 0f);
    }

    /**
     * Save a collections entry and compute expected vs received totals.
     */
    public boolean addToCollections(CollectionsDto collectionsDto) {
        try {
            Collections collections = mapToCollections(collectionsDto);
            LocalDateTime requestedTime = LocalDateTime.parse(collectionsDto.getDate(), formatter);

            // compute expected total from sales at that timestamp
            List<Sales> salesByTime = salesRepository.findByDateTime(requestedTime);
            double expectedTotal = salesByTime.stream()
                .map(Sales::getSaleAmount)
                .mapToDouble(Float::doubleValue)
                .sum();

            // sum up all received amounts
            double receivedTotal =
                collectionsDto.getCashReceived()
                + collectionsDto.getPhonePay()
                + collectionsDto.getCreditCard()
                + collectionsDto.getBorrowedAmount()
                + collectionsDto.getDebtRecovered()
                + collectionsDto.getExpenses();

            collections.setExpectedTotal(expectedTotal);
            collections.setReceivedTotal(receivedTotal);
            collections.setDifference(expectedTotal - receivedTotal);

            Collections saved = collectionsRepository.save(collections);
            return saved.getId() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Collections mapToCollections(CollectionsDto dto) {
        Collections c = new Collections();
        BeanUtils.copyProperties(dto, c);
        c.setDateTime(LocalDateTime.parse(dto.getDate(), formatter));
        return c;
    }

    /**
     * Build dashboard response for a date range.
     */
    public GetReportResponse getDashboard(GetReportRequest req) {
        GetReportResponse resp = new GetReportResponse();
        LocalDateTime from = LocalDateTime.parse(req.getFromDate(), formatter);
        LocalDateTime to   = LocalDateTime.parse(req.getToDate(),   formatter);

        List<Collections> collections = collectionsRepository.findByDateTimeBetween(from, to);
        List<Sales>       sales       = salesRepository.findByDateTimeBetween(from, to);

        double petrolSales  = sumSalesByProduct(sales, "petrol");
        double dieselSales  = sumSalesByProduct(sales, "diesel");
        double petrolColl   = sumSaleAmountByProduct(sales, "petrol");
        double dieselColl   = sumSaleAmountByProduct(sales, "diesel");
        double actualColl   = collections.stream()
                                  .map(Collections::getReceivedTotal)
                                  .mapToDouble(x -> x)
                                  .sum();

        resp.setActualCollection((float) actualColl);
        resp.setDifference((float)(petrolColl + dieselColl - actualColl));
        resp.setPetrol(ReportData.builder()
                            .saleInLtr((float) petrolSales)
                            .expectedCollections((float) petrolColl)
                            .build());
        resp.setDiesel(ReportData.builder()
                            .saleInLtr((float) dieselSales)
                            .expectedCollections((float) dieselColl)
                            .build());
        return resp;
    }

    private static double sumSalesByProduct(List<Sales> sales, String productName) {
        return sales.stream()
                    .filter(s -> productName.equalsIgnoreCase(s.getProductName()))
                    .map(Sales::getSale)
                    .mapToDouble(x -> x)
                    .sum();
    }

    private static double sumSaleAmountByProduct(List<Sales> sales, String productName) {
        return sales.stream()
                    .filter(s -> productName.equalsIgnoreCase(s.getProductName()))
                    .map(Sales::getSaleAmount)
                    .mapToDouble(x -> x)
                    .sum();
    }
}
