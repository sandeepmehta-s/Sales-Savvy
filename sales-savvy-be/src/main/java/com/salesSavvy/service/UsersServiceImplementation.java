package com.salesSavvy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.salesSavvy.entity.Cart;
import com.salesSavvy.entity.Users;
import com.salesSavvy.exception.DuplicateResourceException;
import com.salesSavvy.exception.ResourceNotFoundException;
import com.salesSavvy.repository.CartRepository;
import com.salesSavvy.repository.UsersRepository;

@Service
@Transactional
public class UsersServiceImplementation implements UsersService {

    private final UsersRepository usersRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImplementation(UsersRepository usersRepository, 
                                    CartRepository cartRepository,
                                    PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void signUp(Users user) {
        // Check if username already exists
        if (usersRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("User", "username", user.getUsername());
        }

        // Check if email already exists
        if (usersRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User", "email", user.getEmail());
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Public signup must never be able to create an administrator.
        user.setRole("ROLE_CUSTOMER");

        // Save user
        Users savedUser = usersRepository.save(user);

        // Create cart for the user
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public Users getUser(String username) {
        return usersRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateUser(String username, String password) {
        Optional<Users> userOptional = usersRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return false;
        }
        Users user = userOptional.get();
        return passwordEncoder.matches(password, user.getPassword());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    @Transactional
    public String deleteUser(Long id) {
        Users user = usersRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if user has orders
        if (user.getOrders() != null && !user.getOrders().isEmpty()) {
            throw new IllegalStateException("Cannot delete user with existing orders");
        }

        // Delete user's cart first
        cartRepository.findByUserId(id).ifPresent(cartRepository::delete);

        // Delete user
        usersRepository.delete(user);
        return "User deleted successfully";
    }

    @Override
    @Transactional
    public String updateUser(Users user) {
        Users existingUser = usersRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + user.getId()));

        // Update username if provided and different
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (usersRepository.existsByUsername(user.getUsername())) {
                throw new DuplicateResourceException("User", "username", user.getUsername());
            }
            existingUser.setUsername(user.getUsername());
        }

        // Update email if provided and different
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (usersRepository.existsByEmail(user.getEmail())) {
                throw new DuplicateResourceException("User", "email", user.getEmail());
            }
            existingUser.setEmail(user.getEmail());
        }

        // Update password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Update other fields
        if (user.getGender() != null) {
            existingUser.setGender(user.getGender());
        }
        if (user.getDob() != null) {
            existingUser.setDob(user.getDob());
        }
        if (user.getRole() != null && existingUser.getRole().equals("ROLE_ADMIN")) {
            existingUser.setRole(user.getRole());
        }

        usersRepository.save(existingUser);
        return "User updated successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public Users getUserWithCart(String username) {
        Users user = getUser(username);
        // Eagerly fetch cart
        cartRepository.findByUserUsername(username).ifPresent(user::setCart);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Users getUserWithOrders(String username) {
        Users user = getUser(username);
        // Orders are lazily loaded, but we can trigger loading if needed
        if (user.getOrders() != null) {
            user.getOrders().size(); // Trigger lazy loading
        }
        return user;
    }
}