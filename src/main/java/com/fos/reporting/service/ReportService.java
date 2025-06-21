package com.fos.reporting.service;

import com.fos.reporting.domain.*;
import com.fos.reporting.entity.Borrower;
import com.fos.reporting.entity.Collections;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.BorrowerRepository;
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

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private CollectionsRepository collectionsRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;

    /**
     * Fetch last closing stock for a given product and sub-product.
     */
    public Float getLastClosing(String productName, String subProduct) {
        Sales last = salesRepository
            .findTopByProductNameAndSubProductOrderByDateTimeDesc(productName, subProduct);
        return (last != null) ? last.getClosingStock() : 0f;
    }

    /**
     * Save sales data from entry.
     */
    public boolean addToSales(EntryProduct entryProduct) {
        try {
            entryProduct.getProducts().forEach(product -> {
                Sales sales = new Sales();
                sales.setDateTime(LocalDateTime.parse(entryProduct.getDate(), formatter));
                sales.setProductName(product.getProductName());
                sales.setSubProduct(product.getSubProduct());
                sales.setEmployeeId(entryProduct.getEmployeeId());

                float opening = product.getOpening();
                if (opening == 0f) {
                    opening = getLastClosing(product.getProductName(), product.getSubProduct());
                }

                sales.setOpeningStock(opening);
                sales.setClosingStock(product.getClosing());
                sales.setTestingTotal(product.getTesting());

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
     * Save collections and borrowers.
     */
    public boolean addToCollections(CollectionsDto collectionsDto) {
        try {
            // 1. Map and save Collections entity
            Collections collections = mapToCollections(collectionsDto);
            LocalDateTime requestedTime = LocalDateTime.parse(collectionsDto.getDate(), formatter);
            collections.setEmployeeId(collectionsDto.getEmployeeId());

            List<Sales> salesByTime = salesRepository.findByDateTime(requestedTime);

            double expectedTotal = salesByTime.stream().map(Sales::getSaleAmount)
                    .mapToDouble(Float::doubleValue).sum();

            double receivedTotal = collectionsDto.getCashReceived() +
                    collectionsDto.getPhonePay() +
                    collectionsDto.getCreditCard() +
                    collectionsDto.getBorrowedAmount() +
                    collectionsDto.getDebtRecovered();


            collections.setExpectedTotal(expectedTotal);
            collections.setReceivedTotal(receivedTotal);
            collections.setDifference(expectedTotal - receivedTotal);

            Collections savedCollection = collectionsRepository.save(collections);

            // 2. Save borrowers (if any)
            if (collectionsDto.getBorrowers() != null) {
                for (BorrowerDto b : collectionsDto.getBorrowers()) {
                    Borrower borrower = new Borrower();
                    borrower.setName(b.getName());
                    borrower.setAmount(b.getAmount());
                    borrower.setBorrowedAt(LocalDateTime.now());
                    borrower.setCollection(savedCollection);
                    borrowerRepository.save(borrower);
                }
            }

            return savedCollection.getId() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Collections mapToCollections(CollectionsDto collectionsDto) {
        Collections collections = new Collections();
        BeanUtils.copyProperties(collectionsDto, collections);
        collections.setDateTime(LocalDateTime.parse(collectionsDto.getDate(), formatter));
        return collections;
    }

    /**
     * Dashboard logic to generate report summary.
     */
    public GetReportResponse getDashboard(GetReportRequest req) {
        GetReportResponse getReportResponse = new GetReportResponse();
        LocalDateTime fromDate = LocalDateTime.parse(req.getFromDate(), formatter);
        LocalDateTime toDate = LocalDateTime.parse(req.getToDate(), formatter);

        List<Collections> collections = collectionsRepository.findByDateTimeBetween(fromDate, toDate);
        List<Sales> sales = salesRepository.findByDateTimeBetween(fromDate, toDate);

        float petrol = (float) getSalesByProductName(sales, "petrol");
        float diesel = (float) getSalesByProductName(sales, "diesel");
        float petrolCollections = (float) getCollections(sales, "petrol");
        float dieselCollections = (float) getCollections(sales, "diesel");

        float actualCollections = (float) collections.stream()
                .map(Collections::getReceivedTotal)
                .mapToDouble(x -> x).sum();

        getReportResponse.setActualCollection(actualCollections);
        getReportResponse.setDifference(petrolCollections + dieselCollections - actualCollections);
        getReportResponse.setPetrol(ReportData.builder().saleInLtr(petrol).expectedCollections(petrolCollections).build());
        getReportResponse.setDiesel(ReportData.builder().saleInLtr(diesel).expectedCollections(dieselCollections).build());

        return getReportResponse;
    }

    private static double getSalesByProductName(List<Sales> sales, String productName) {
        return sales.stream()
                .filter(x -> productName.equalsIgnoreCase(x.getProductName())).map(Sales::getSale)
                .mapToDouble(x -> x).sum();
    }

    private static double getCollections(List<Sales> sales, String productName) {
        return sales.stream()
                .filter(x -> productName.equalsIgnoreCase(x.getProductName())).map(Sales::getSaleAmount)
                .mapToDouble(x -> x).sum();
    }
}

