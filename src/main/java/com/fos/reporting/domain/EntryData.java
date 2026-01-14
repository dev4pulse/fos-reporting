package com.fos.reporting.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EntryData {
    @JsonProperty ("entrySaleData")
    private EntrySaleDto entrySaleDto;
    @JsonProperty ("entryCollectionData")
    private CollectionsDto collectionsDto;
    @JsonProperty ("inventoryData")
    private InventoryDto inventoryDto;
}
