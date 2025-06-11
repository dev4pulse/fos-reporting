package com.fos.reporting.service;

import com.fos.reporting.domain.CollectionsDto;
import com.fos.reporting.domain.EntryProduct;
import com.fos.reporting.domain.GetReportRequest;
import com.fos.reporting.domain.GetReportResponse;
import com.fos.reporting.domain.Product;
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
    private static final DateTimeFormatter DATE_TIME_FMT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private CollectionsRepository collectionsRepository;

    public boolean addToSales(EntryProduct entryProduct) {
        try {
            LocalDateTime saleDateTime = entryProduct.getDate().atStartOfDay();

            for (Product product : entryProduct.getProducts()) {
                Sales sales = new Sales();
                sales.setDateTime(saleDateTime);
                sales.setEmployeeId(entryProduct.getEmployeeId());
                sales.setProductName(product.getProductName());
                sales.setSubProduct(product.getSubProduct());

                float opening = product.getOpening();
                if (opening == 0f) {
                    Sales recent = salesRepository
                      .findTopByProductNameAndSubProductOrderByDateTimeDesc(
                        sales.getProductName(), sales.getSubProduct());
                    if (recent != null) {
                        opening = recent.getClosingStock();
                    }
                }
                sales.setOpeningStock(opening);
                sales.setClosingStock(product.getClosing());
                sales.setTestingTotal(product.getTesting());
                float sale = product.getClosing() - opening - product.getTesting();
                sales.setSale(sale);
                sales.setPrice(product.getPrice());
                sales.setSaleAmount(sale * product.getPrice());
                salesRepository.save(sales);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToCollections(CollectionsDto dto) {
        try {
            Collections coll = mapToCollections(dto);
            LocalDateTime ts = LocalDateTime.parse(dto.getDate(), DATE_TIME_FMT);
            coll.setDateTime(ts);

            List<Sales> salesList = salesRepository.findByDateTime(ts);
            double expected = salesList.stream()
                .mapToDouble(Sales::getSaleAmount)
                .sum();

            double received = dto.getCashReceived()
                            + dto.getPhonePay()
                            + dto.getCreditCard()
                            + dto.getBorrowedAmount()
                            + dto.getDebtRecovered()
                            + dto.getExpenses();

            coll.setExpectedTotal(expected);
            coll.setReceivedTotal(received);
            coll.setDifference(expected - received);
            return collectionsRepository.save(coll).getId() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Collections mapToCollections(CollectionsDto dto) {
        Collections c = new Collections();
        BeanUtils.copyProperties(dto, c);
        c.setDateTime(LocalDateTime.parse(dto.getDate(), DATE_TIME_FMT));
        return c;
    }

    public GetReportResponse getDashboard(GetReportRequest req) {
        GetReportResponse resp = new GetReportResponse();
        LocalDateTime from = LocalDateTime.parse(req.getFromDate(), DATE_TIME_FMT);
        LocalDateTime to   = LocalDateTime.parse(req.getToDate(),   DATE_TIME_FMT);

        List<Collections> collList = collectionsRepository.findByDateTimeBetween(from, to);
        List<Sales>       salesList = salesRepository.findByDateTimeBetween(from, to);

        double petrolLiters = salesList.stream()
            .filter(s -> "petrol".equalsIgnoreCase(s.getProductName()))
            .mapToDouble(Sales::getSale)
            .sum();
        double dieselLiters = salesList.stream()
            .filter(s -> "diesel".equalsIgnoreCase(s.getProductName()))
            .mapToDouble(Sales::getSale)
            .sum();

        double petrolColl = salesList.stream()
            .filter(s -> "petrol".equalsIgnoreCase(s.getProductName()))
            .mapToDouble(Sales::getSaleAmount)
            .sum();
        double dieselColl = salesList.stream()
            .filter(s -> "diesel".equalsIgnoreCase(s.getProductName()))
            .mapToDouble(Sales::getSaleAmount)
            .sum();

        double actual = collList.stream()
            .mapToDouble(Collections::getReceivedTotal)
            .sum();

        resp.setActualCollection((float) actual);
        resp.setDifference((float)(petrolColl + dieselColl - actual));

        resp.setPetrol(ReportData.builder()
            .saleInLtr((float) petrolLiters)
            .expectedCollections((float) petrolColl)
            .build());
        resp.setDiesel(ReportData.builder()
            .saleInLtr((float) dieselLiters)
            .expectedCollections((float) dieselColl)
            .build());

        return resp;
    }

    /** Exposed for the new `/sales/last` endpoint */
    public Sales findLastSale(String productName, String subProduct) {
        return salesRepository
            .findTopByProductNameAndSubProductOrderByDateTimeDesc(productName, subProduct);
    }
}

