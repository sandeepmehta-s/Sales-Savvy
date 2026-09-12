package com.salesSavvy.product.service;

import com.salesSavvy.product.entity.Product;
import com.salesSavvy.shared.exception.DuplicateResourceException;
import com.salesSavvy.shared.exception.ResourceNotFoundException;
import com.salesSavvy.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplementation implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImplementation(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public String addProduct(Product product) {
        if (productRepository.existsByName(product.getName()))
            throw new DuplicateResourceException("Product", "name", product.getName());
        productRepository.save(product);
        return "Product added successfully";
    }

    @Override
    public Product getProductById(String id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product getProductByName(String name) {
        return productRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with name: " + name));
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public String updateProduct(Product product) {
        Product existing = productRepository.findById(product.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + product.getId()));
        if (product.getName() != null && !product.getName().equals(existing.getName())) {
            if (productRepository.existsByName(product.getName()))
                throw new DuplicateResourceException("Product", "name", product.getName());
            existing.setName(product.getName());
        }
        if (product.getDescription() != null) existing.setDescription(product.getDescription());
        if (product.getPrice() != null) existing.setPrice(product.getPrice());
        if (product.getPhoto() != null) existing.setPhoto(product.getPhoto());
        if (product.getCategory() != null) existing.setCategory(product.getCategory());
        if (product.getReviews() != null) existing.setReviews(product.getReviews());
        productRepository.save(existing);
        return "Product updated successfully";
    }

    @Override
    public String deleteProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        return "Product deleted successfully";
    }

    @Override
    public List<Product> getAllProducts() { return productRepository.findAll(); }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Product> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    @Override
    public List<Product> getProductsByCategoryAndPrice(String category, BigDecimal min, BigDecimal max) {
        return productRepository.findByCategoryAndPriceBetween(category, min, max);
    }

    @Override
    public List<String> getAllCategories() {
        return productRepository.findAll().stream()
            .map(Product::getCategory).distinct().collect(Collectors.toList());
    }
}