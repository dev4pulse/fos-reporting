package com.fos.reporting.domain;

/**
 * Represents the lifecycle status of a product.
 */
public enum ProductStatus {
    /**
     * The product is currently available for sale and use.
     */
    ACTIVE,

    /**
     * The product has been deactivated and is not available for new transactions.
     */
    INACTIVE
}