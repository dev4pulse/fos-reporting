package com.fos.reporting.domain;

public class EntryRequestDto {

    private EntrySaleDto saleEntry;
    private CollectionsDto collectionsEntry;

    // Getters and setters
    public EntrySaleDto getSaleEntry() { return saleEntry; }
    public void setSaleEntry(EntrySaleDto saleEntry) { this.saleEntry = saleEntry; }

    public CollectionsDto getCollectionsEntry() { return collectionsEntry; }
    public void setCollectionsEntry(CollectionsDto collectionsEntry) { this.collectionsEntry = collectionsEntry; }
}
