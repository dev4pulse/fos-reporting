package com.fos.reporting.service;

import com.fos.reporting.domain.*;
import com.fos.reporting.entity.Collections;
import com.fos.reporting.entity.Sales;
import com.fos.reporting.repository.CollectionsRepository;
import com.fos.reporting.repository.InventoryLogRepository;
import com.fos.reporting.repository.SalesRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Sales last = salesRepository.findTopByProductNameAndGunOrderByDateTimeDesc(productName, gun);
        return (last != null) ? last.getClosingStock() : 0f;
    }

    public boolean addToSales(EntrySaleDto entrySaleDto, String entryId) {
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
            }
            return true;
        } catch (Exception e) {
            // In production, replace with real logging
            e.printStackTrace();
            return false;
        }
    }

    public boolean addToCollections(CollectionsDto dto, String entryId) {
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
                    + dto.getCreditCard();

            collections.setExpectedTotal(expected);
            collections.setReceivedTotal(received);
            collections.setDifference(expected - received);
            collections.setEntryId(entryId);

            Collections saved = collectionsRepository.save(collections);

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

    public List<Sales> getRecentSales() {
        return salesRepository.findTop10ByOrderByDateTimeDesc();
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

    @Transactional
    public void deleteById(String entryId){
        try {
            salesRepository.deleteByEntryId(entryId);
            collectionsRepository.deleteByEntryId(entryId);
            inventoryLogRepository.deleteByEntryId(entryId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete data with entryId: " + entryId, e);
        }
    }
    public boolean addData(EntryData entryData) {
        String entryId = UUID.randomUUID().toString();
        this.addToSales(entryData.getEntrySaleDto(), entryId);
        this.addToCollections(entryData.getCollectionsDto(), entryId);
        inventoryService.recordInventoryTransaction(entryData.getInventoryDto(), entryId);
        return true;
    }
    public EntryData getEntryById(String entryId) {
        // Fetch all related entities from the database
        List<Sales> salesList = salesRepository.findByEntryId(entryId);
        List<Collections> collectionsList = collectionsRepository.findByEntryId(entryId);

        // If there are no sales, the entry doesn't exist.
        if (salesList.isEmpty()) {
            throw new RuntimeException("No entry found with entryId: " + entryId);
        }

        // Map entities to DTOs
        EntrySaleDto entrySaleDto = mapSalesToDto(salesList);

        CollectionsDto collectionsDto = null;
        if (collectionsList != null && !collectionsList.isEmpty()) {
            // Assuming we only want the first result for a given entryId
            Collections firstCollection = collectionsList.get(0);
            collectionsDto = this.mapCollectionsToDto(firstCollection);
        }
        // Assemble the final EntryData object
        EntryData entryData = new EntryData();
        entryData.setEntrySaleDto(entrySaleDto);
        entryData.setCollectionsDto(collectionsDto);

        // Note: The current design has one inventory log per entry. This is a simplification.
        // A more robust design would have one inventory log per product sold.
        inventoryLogRepository.findByEntryId(entryId).stream().findFirst().ifPresent(log -> {
            InventoryDto inventoryDto = new InventoryDto();
            BeanUtils.copyProperties(log, inventoryDto);
            inventoryDto.setProductId(log.getProduct().getId());
            entryData.setInventoryDto(inventoryDto);
        });

        return entryData;
    }

    public List<RecentData> getRecentEntries() {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(7);

        // 1. Find all unique entry IDs in the last 10 days.
        List<String> recentEntryIds = salesRepository.findDistinctEntryIdsByDateTimeAfter(sinceDate);

        // 2. For each ID, get the full EntryData and collect it into a list.
        return recentEntryIds.stream()
                .map(entryId -> new RecentData(entryId, this.getEntryById(entryId)))
                .collect(Collectors.toList());
    }

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
