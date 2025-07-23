package com.fos.reporting.controller;

import com.fos.reporting.domain.ProductDto;
import com.fos.reporting.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing products.
 * Follows best practices by using DTOs for the API contract and constructor injection.
 */
@RestController
@RequestMapping("/products") // 1. Standardized API path
public class ProductController {

    private final ProductService productService;

    // 2. Use constructor injection instead of @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * POST /api/products : Creates a new product.
     */
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto savedProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * GET /api/products : Gets a list of all products.
     */
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        // 3. This now correctly returns a List<ProductDto>
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id} : Gets a single product by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        // 4. Simplified logic: The service will throw an exception if not found
        ProductDto product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * PUT /api/products/{id} : Updates an existing product.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * DELETE /api/products/{id} : Deletes a product by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // 5. Use 204 No Content for successful deletions
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Note: The "/search" endpoint is removed. This logic should be handled
    // by the service layer and can be added as a parameter to getAllProducts if needed,
    // e.g., @GetMapping(params = "name")
}