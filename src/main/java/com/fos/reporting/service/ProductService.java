package com.fos.reporting.service;

import com.fos.reporting.domain.ProductDto;
import com.fos.reporting.domain.ProductStatus;
import com.fos.reporting.entity.Product;
import com.fos.reporting.entity.ProductPriceHistory;
import com.fos.reporting.repository.ProductPriceHistoryRepository;
import com.fos.reporting.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceHistoryRepository priceHistoryRepository;

    public ProductService(ProductRepository productRepository, ProductPriceHistoryRepository priceHistoryRepository) {
        this.productRepository = productRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        productRepository.findByNameIgnoreCase(productDto.getProductName()).ifPresent(p -> {
            throw new IllegalStateException("Product with name '" + productDto.getProductName() + "' already exists.");
        });

        Product product = toEntity(productDto);
        if (productDto.getStatus() == null) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        Product savedProduct = productRepository.save(product);
        return toDto(savedProduct);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        BigDecimal oldPrice = existingProduct.getPrice();
        BigDecimal newPrice = productDto.getPrice();

        if (newPrice != null && oldPrice.compareTo(newPrice) != 0) {
            ProductPriceHistory historyRecord = new ProductPriceHistory(
                    existingProduct,
                    newPrice,
                    LocalDateTime.now(),
                    null // TODO: Replace with logged-in employee's ID
            );
            priceHistoryRepository.save(historyRecord);
        }

        existingProduct.setName(productDto.getProductName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setTankCapacity(productDto.getTankCapacity());
        existingProduct.setPrice(productDto.getPrice());
        if (productDto.getStatus() != null) {
            existingProduct.setStatus(productDto.getStatus());
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return toDto(updatedProduct);
    }

    @Transactional
    public ProductDto updateProductPrice(Long id, Double newPriceDouble) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        BigDecimal newPrice = BigDecimal.valueOf(newPriceDouble);
        BigDecimal oldPrice = product.getPrice();

        if (oldPrice.compareTo(newPrice) != 0) {
            ProductPriceHistory history = new ProductPriceHistory(
                    product,
                    newPrice,
                    LocalDateTime.now(),
                    null // TODO: Replace with logged-in employee's ID
            );
            priceHistoryRepository.save(history);

            product.setPrice(newPrice);
            product = productRepository.save(product);
        }

        return toDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return toDto(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setTankCapacity(product.getTankCapacity());
        dto.setPrice(product.getPrice());
        dto.setStatus(product.getStatus());
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setTankCapacity(dto.getTankCapacity());
        product.setPrice(dto.getPrice());
        product.setStatus(dto.getStatus());
        return product;
    }
}
