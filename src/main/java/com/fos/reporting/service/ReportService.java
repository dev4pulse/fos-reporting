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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private CollectionsRepository collectionsRepository;

    @Autowired
    private BorrowerRepository borrowerRepository;

    public float getLastClosing(String productName, String gun) {
        Sales last = salesRepository.findTopByProductNameAndGunOrderByDateTimeDesc(productName, gun);
        return (last != null) ? last.getClosingStock() : 0f;
    }

    public boolean addToSales(EntryProduct entryProduct) {
        try {
            LocalDateTime entryDateTime = LocalDateTime.parse(entryProduct.getDate(), FORMATTER);

            for (Product product : entryProduct.getProducts()) {
                Sales sales = new Sales();
                sales.setDateTime(entryDateTime);
                sales.setProductName(product.getProductName());
                sales.setGun(product.getGun());
                sales.setEmployeeId(entryProduct.getEmployeeId());


                float opening = product.getOpening() == 0f
                        ? getLastClosing(product.getProductName(), product.getGun())
                        : product.getOpening();

                float closing = product.getClosing();
                float testing = product.getTesting();

                sales.setOpeningStock(opening);
                sales.setClosingStock(closing);
                sales.setTestingTotal(testing);

                BigDecimal saleVolume = BigDecimal.valueOf(closing - opening - testing);
                sales.setSalesInLiters(saleVolume);

                sales.setPrice(product.getPrice());
                float amount = saleVolume.multiply(BigDecimal.valueOf(product.getPrice())).floatValue();
                sales.setSalesInRupees(amount);

                salesRepository.save(sales);
            }
            return true;
        } catch (Exception e) {
            // In production, replace with real logging
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToCollections(CollectionsDto dto) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dto.getDate(), FORMATTER);
            Collections collections = new Collections();
            BeanUtils.copyProperties(dto, collections);
            collections.setDateTime(dateTime);

            List<Sales> salesByTime = salesRepository.findByDateTime(dateTime);
            double expected = salesByTime.stream()
                    .mapToDouble(Sales::getSalesInRupees).sum();

            double received = dto.getCashReceived()
                    + dto.getPhonePay()
                    + dto.getCreditCard()
                    + dto.getBorrowedAmount();

            collections.setExpectedTotal(expected);
            collections.setReceivedTotal(received);
            collections.setDifference(expected - received);

            Collections saved = collectionsRepository.save(collections);

            if (dto.getBorrowers() != null) {
                for (BorrowerDto b : dto.getBorrowers()) {
                    Borrower borrower = new Borrower();
                    BeanUtils.copyProperties(b, borrower);
                    borrower.setCollection(saved); // Link to parent collection
                    borrowerRepository.save(borrower);
                }

            }
            return saved.getId() != null && saved.getId() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public GetReportResponse getDashboard(GetReportRequest req) {
        LocalDateTime from = LocalDateTime.parse(req.getFromDate(), FORMATTER);
        LocalDateTime to = LocalDateTime.parse(req.getToDate(), FORMATTER);

        List<Collections> collections = collectionsRepository.findByDateTimeBetween(from, to);
        List<Sales> sales = salesRepository.findByDateTimeBetween(from, to);

        double petrolLiters = getSalesVolume(sales, "petrol");
        double dieselLiters = getSalesVolume(sales, "diesel");

        double petrolExpected = getSalesAmount(sales, "petrol");
        double dieselExpected = getSalesAmount(sales, "diesel");

        double totalReceived = collections.stream()
                .mapToDouble(Collections::getReceivedTotal)
                .sum();

        GetReportResponse response = new GetReportResponse();
        response.setActualCollection((float) totalReceived);
        response.setDifference((float) (petrolExpected + dieselExpected - totalReceived));
        response.setPetrol(ReportData.builder()
                .saleInLtr((float) petrolLiters)
                .expectedCollections((float) petrolExpected)
                .build());
        response.setDiesel(ReportData.builder()
                .saleInLtr((float) dieselLiters)
                .expectedCollections((float) dieselExpected)
                .build());

        return response;
    }

    private static double getSalesVolume(List<Sales> sales, String productName) {
        return sales.stream()
                .filter(s -> productName.equalsIgnoreCase(s.getProductName()))
                .map(Sales::getSalesInLiters)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    private static double getSalesAmount(List<Sales> sales, String productName) {
        return sales.stream()
                .filter(s -> productName.equalsIgnoreCase(s.getProductName()))
                .mapToDouble(Sales::getSalesInRupees)
                .sum();
    }
}
