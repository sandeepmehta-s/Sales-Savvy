package com.salesSavvy.product.service;

import com.salesSavvy.product.entity.Product;
import com.salesSavvy.shared.exception.DuplicateResourceException;
import com.salesSavvy.shared.exception.ResourceNotFoundException;
import com.salesSavvy.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImplementation implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImplementation(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public String addProduct(Product product) {
        // Check for duplicate product name
        if (productRepository.existsByName(product.getName())) {
            throw new DuplicateResourceException("Product", "name", product.getName());
        }

        productRepository.save(product);
        return "Product added successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductByName(String name) {
        return productRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    @Transactional
    public String updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + product.getId()));

        // Check if name is being changed to an existing name
        if (product.getName() != null && !product.getName().equals(existingProduct.getName())) {
            if (productRepository.existsByName(product.getName())) {
                throw new DuplicateResourceException("Product", "name", product.getName());
            }
            existingProduct.setName(product.getName());
        }

        // Update other fields
        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }
        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }
        if (product.getPhoto() != null) {
            existingProduct.setPhoto(product.getPhoto());
        }
        if (product.getCategory() != null) {
            existingProduct.setCategory(product.getCategory());
        }
        if (product.getReviews() != null) {
            existingProduct.setReviews(product.getReviews());
        }

        productRepository.save(existingProduct);
        return "Product updated successfully";
    }

    @Override
    @Transactional
    public String deleteProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
        return "Product deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryAndPrice(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByCategoryAndPriceBetween(category, minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return productRepository.findAll().stream()
            .map(Product::getCategory)
            .distinct()
            .collect(Collectors.toList());
    }
}