package com.fos.reporting.service;

import com.fos.reporting.domain.*;
import com.fos.reporting.entity.Collections;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.CollectionsRepository;
import com.fos.reporting.repository.InventoryLogRepository;
import com.fos.reporting.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private CollectionsRepository collectionsRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    public float getLastClosing(String productName, String gun) {
        try {
            log.info("Fetching last closing for product {} and gun {}", productName, gun);
            Sales last = salesRepository.findTopByProductNameAndGunOrderByDateTimeDesc(productName, gun);
            float closing = (last != null) ? last.getClosingStock() : 0f;
            log.info("Last closing found: {}", closing);
            return closing;
        } catch (Exception e) {
            log.error("Error fetching last closing: {}", e.getMessage(), e);
            return 0f;
        }
    }

    public boolean addToSales(EntrySaleDto entrySaleDto, String entryId) {
        log.info("Adding sales entry: {} with entryId: {}", entrySaleDto, entryId);
        try {
            LocalDateTime entryDateTime = LocalDateTime.parse(entrySaleDto.getDate(), FORMATTER);

            for (Product product : entrySaleDto.getProducts()) {
                Sales sales = new Sales();
                sales.setDateTime(entryDateTime);
                sales.setProductName(product.getProductName());
                sales.setGun(product.getGun());
                sales.setEmployeeId(entrySaleDto.getEmployeeId());
                sales.setEntryId(entryId);

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
                log.info("Saved sale: {}", sales);
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to add sales entry: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean addToCollections(CollectionsDto dto, String entryId) {
        log.info("Adding collections: {} with entryId: {}", dto, entryId);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dto.getDate(), FORMATTER);
            Collections collections = new Collections();
            BeanUtils.copyProperties(dto, collections);
            collections.setDateTime(dateTime);

            List<Sales> salesByTime = salesRepository.findByDateTime(dateTime);
            double expected = salesByTime.stream().mapToDouble(Sales::getSalesInRupees).sum();

            double received = dto.getCashReceived() + dto.getPhonePay() + dto.getCreditCard();

            collections.setExpectedTotal(expected);
            collections.setReceivedTotal(received);
            collections.setDifference(expected - received);
            collections.setEntryId(entryId);

            Collections saved = collectionsRepository.save(collections);
            log.info("Collections saved successfully: {}", saved);
            return saved.getId() != null && saved.getId() > 0;
        } catch (Exception e) {
            log.error("Failed to add collections: {}", e.getMessage(), e);
            return false;
        }
    }

    public GetReportResponse getDashboard(GetReportRequest req) {
        log.info("Fetching dashboard report for request: {}", req);
        try {
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
            response.setPetrol(ReportData.builder().saleInLtr((float) petrolLiters).expectedCollections((float) petrolExpected).build());
            response.setDiesel(ReportData.builder().saleInLtr((float) dieselLiters).expectedCollections((float) dieselExpected).build());

            log.info("Dashboard report generated successfully");
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch dashboard report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate dashboard report", e);
        }
    }

    @Transactional
    public void deleteById(String entryId) {
        log.info("Deleting entry with entryId: {}", entryId);
        try {
            salesRepository.deleteByEntryId(entryId);
            collectionsRepository.deleteByEntryId(entryId);
            inventoryLogRepository.deleteByEntryId(entryId);
            log.info("Deleted entry successfully for entryId: {}", entryId);
        } catch (Exception e) {
            log.error("Failed to delete entry: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete data with entryId: " + entryId, e);
        }
    }

    public boolean addData(EntryData entryData) {
        String entryId = UUID.randomUUID().toString();
        log.info("Adding full entryData with entryId: {}", entryId);
        try {
            this.addToSales(entryData.getEntrySaleDto(), entryId);
            this.addToCollections(entryData.getCollectionsDto(), entryId);
            inventoryService.recordInventoryTransaction(entryData.getInventoryDto(), entryId);
            log.info("Entry data added successfully for entryId: {}", entryId);
            return true;
        } catch (Exception e) {
            log.error("Failed to add entry data: {}", e.getMessage(), e);
            return false;
        }
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

    public Page<Sales> getAllSales(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return salesRepository.findAll(pageable);
    }

    public Page<Sales> getSalesByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return salesRepository.findByDateTimeBetween(from, to, pageable);
    }

    public Page<Sales> getSalesByDateRangeAndProduct(LocalDateTime from, LocalDateTime to, String productName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateTime").descending());
        return salesRepository.findByProductNameAndDateTimeBetween(productName, from, to, pageable);
    }

    // Map Sales to DTO
    private EntrySaleDto mapSalesToDto(List<Sales> salesList) {
        EntrySaleDto dto = new EntrySaleDto();
        Sales firstSale = salesList.get(0);
        dto.setDate(firstSale.getDateTime().format(FORMATTER));
        dto.setEmployeeId(firstSale.getEmployeeId());

        List<Product> products = salesList.stream().map(sale -> {
            Product p = new Product();
            BeanUtils.copyProperties(sale, p);
            p.setPrice(sale.getPrice());
            p.setOpening(sale.getOpeningStock());
            p.setClosing(sale.getClosingStock());
            p.setTesting(sale.getTestingTotal());
            p.setSaleInLiters(sale.getSalesInLiters());
            p.setSalesInRupees(sale.getSalesInRupees());
            return p;
        }).collect(Collectors.toList());
        dto.setProducts(products);
        return dto;
    }

    private CollectionsDto mapCollectionsToDto(Collections collections) {
        CollectionsDto dto = new CollectionsDto();
        BeanUtils.copyProperties(collections, dto);
        dto.setDate(collections.getDateTime().format(FORMATTER));
        return dto;
    }
}
