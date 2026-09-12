package com.salesSavvy.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.salesSavvy.product.entity.Product;
import com.salesSavvy.product.repository.ProductRepository;
import com.salesSavvy.shared.exception.DuplicateResourceException;
import com.salesSavvy.shared.exception.ResourceNotFoundException;

@Service
public class ProductServiceImplementation implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImplementation(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public String addProduct(Product product) {
        String name = Objects.toString(product.getName(), "");
        if (productRepository.existsByName(name))
            throw new DuplicateResourceException("Product", "name", name);
        productRepository.save(product);
        return "Product added successfully";
    }

    @Override
    public Product getProductById(String id) {
        Product found = productRepository.findById(Objects.requireNonNull(id)).orElse(null);
        if (found == null)
            throw new ResourceNotFoundException("Product not found with id: " + id);
        return found;
    }

    @Override
    public Product getProductByName(String name) {
        Product found = productRepository.findByName(name).orElse(null);
        if (found == null)
            throw new ResourceNotFoundException("Product not found with name: " + name);
        return found;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public String updateProduct(Product product) {
        String productId = Objects.requireNonNull(product.getId(), "Product ID must not be null");
        Product existing = productRepository.findById(productId).orElse(null);
        if (existing == null)
            throw new ResourceNotFoundException("Product not found with id: " + productId);

        String newName = product.getName();
        if (newName != null && !newName.equals(existing.getName())) {
            if (productRepository.existsByName(newName))
                throw new DuplicateResourceException("Product", "name", newName);
            existing.setName(newName);
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
        Product product = productRepository.findById(Objects.requireNonNull(id)).orElse(null);
        if (product == null)
            throw new ResourceNotFoundException("Product not found with id: " + id);
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
            .map(p -> Objects.toString(p.getCategory(), ""))
            .distinct()
            .collect(Collectors.toList());
    }
}