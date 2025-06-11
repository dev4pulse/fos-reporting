package com.fos.reporting.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mirrors the JSON:
 * { "productName":"…", "subProduct":"…", "opening":0.0, "closing":0.0,
 *   "price":0.0, "testing":0.0 }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
  
  /** e.g. "petrol", "diesel", "other" */
  @NotNull
  @JsonProperty("productName")
  private String productName;

  /** e.g. "G1", "G2", "G3" */
  @JsonProperty("subProduct")
  private String subProduct;

  /** opening stock (may default to 0 if missing) */
  @JsonProperty("opening")
  private Float opening = 0f;

  /** closing stock */
  @JsonProperty("closing")
  private Float closing = 0f;

  /** price per unit (required) */
  @NotNull
  @JsonProperty("price")
  private Float price;

  /** testing amount (may default to 0 if missing) */
  @JsonProperty("testing")
  private Float testing = 0f;
}

