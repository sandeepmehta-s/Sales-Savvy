package com.salesSavvy.user.service;

import com.salesSavvy.cart.entity.Cart;
import com.salesSavvy.cart.repository.CartRepository;
import com.salesSavvy.shared.exception.DuplicateResourceException;
import com.salesSavvy.shared.exception.ResourceNotFoundException;
import com.salesSavvy.user.entity.Users;
import com.salesSavvy.user.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
    public void signUp(Users user) {
        if (usersRepository.existsByUsername(user.getUsername()))
            throw new DuplicateResourceException("User", "username", user.getUsername());
        if (usersRepository.existsByEmail(user.getEmail()))
            throw new DuplicateResourceException("User", "email", user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_CUSTOMER");
        Users saved = usersRepository.save(user);

        // Create cart document for new user
        Cart cart = new Cart(saved.getUsername());
        Cart savedCart = cartRepository.save(cart);
        saved.setCartId(savedCart.getId());
        usersRepository.save(saved);
    }

    @Override
    public Users getUser(String username) {
        return usersRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    @Override
    public boolean validateUser(String username, String password) {
        Optional<Users> userOpt = usersRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        return passwordEncoder.matches(password, userOpt.get().getPassword());
    }

    @Override
    public List<Users> getAllUsers() { return usersRepository.findAll(); }

    @Override
    public String deleteUser(String id) {
        Users user = usersRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getOrderIds() != null && !user.getOrderIds().isEmpty())
            throw new IllegalStateException("Cannot delete user with existing orders");

        cartRepository.findByUsername(user.getUsername()).ifPresent(cartRepository::delete);
        usersRepository.delete(user);
        return "User deleted successfully";
    }

    @Override
    public String updateUser(Users user) {
        Users existing = usersRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + user.getId()));

        if (user.getUsername() != null && !user.getUsername().equals(existing.getUsername())) {
            if (usersRepository.existsByUsername(user.getUsername()))
                throw new DuplicateResourceException("User", "username", user.getUsername());
            existing.setUsername(user.getUsername());
        }
        if (user.getEmail() != null && !user.getEmail().equals(existing.getEmail())) {
            if (usersRepository.existsByEmail(user.getEmail()))
                throw new DuplicateResourceException("User", "email", user.getEmail());
            existing.setEmail(user.getEmail());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty())
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getGender() != null) existing.setGender(user.getGender());
        if (user.getDob() != null) existing.setDob(user.getDob());
        if (user.getRole() != null && existing.getRole().equals("ROLE_ADMIN"))
            existing.setRole(user.getRole());

        usersRepository.save(existing);
        return "User updated successfully";
    }

    @Override
    public Users getUserWithCart(String username) { return getUser(username); }

    @Override
    public Users getUserWithOrders(String username) { return getUser(username); }
}