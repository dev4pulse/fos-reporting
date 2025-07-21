package com.fos.reporting.service;

import com.fos.reporting.domain.ProductDto;
import com.fos.reporting.entity.Product;
import com.fos.reporting.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductByName(String name) {
        return productRepository.findByNameIgnoreCase(name);
    }

    public Product createProduct(ProductDto dto) {
        // Check if product already exists
        if (productRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Product with name '" + dto.getName() + "' already exists");
        }

        Product product = new Product();
        BeanUtils.copyProperties(dto, product, "productId");
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        BeanUtils.copyProperties(dto, product, "productId", "createdAt");
        return productRepository.save(product);
    }

    public boolean deleteProduct(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            productRepository.delete(productOpt.get());
            return true;
        }
        return false;
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByNameContaining(name);
    }
}
