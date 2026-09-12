package com.shopSphere.user.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopSphere.cart.entity.Cart;
import com.shopSphere.cart.repository.CartRepository;
import com.shopSphere.shared.exception.DuplicateResourceException;
import com.shopSphere.shared.exception.ResourceNotFoundException;
import com.shopSphere.user.entity.Users;
import com.shopSphere.user.repository.UsersRepository;

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
        String username = Objects.toString(user.getUsername(), "");
        String email    = Objects.toString(user.getEmail(), "");

        if (usersRepository.existsByUsername(username))
            throw new DuplicateResourceException("User", "username", username);
        if (usersRepository.existsByEmail(email))
            throw new DuplicateResourceException("User", "email", email);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_CUSTOMER");
        Users saved = usersRepository.save(user);

        // Create cart document for new user
        String savedUsername = Objects.toString(saved.getUsername(), "");
        Cart cart = new Cart(savedUsername);
        Cart savedCart = cartRepository.save(cart);
        saved.setCartId(savedCart.getId());
        usersRepository.save(saved);
    }

    @Override
    public Users getUser(String username) {
        Users found = usersRepository.findByUsername(username).orElse(null);
        if (found == null)
            throw new ResourceNotFoundException("User not found: " + username);
        return found;
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
        // Objects.requireNonNull narrows type to @NonNull — satisfies JDT checker for findById
        Users user = usersRepository.findById(Objects.requireNonNull(id)).orElse(null);
        if (user == null)
            throw new ResourceNotFoundException("User not found with id: " + id);

        if (user.getOrderIds() != null && !user.getOrderIds().isEmpty())
            throw new IllegalStateException("Cannot delete user with existing orders");

        String username = Objects.toString(user.getUsername(), "");
        cartRepository.findByUsername(username).ifPresent(cartRepository::delete);
        usersRepository.delete(user);
        return "User deleted successfully";
    }

    @Override
    public String updateUser(Users user) {
        String userId = Objects.requireNonNull(user.getId(), "User ID must not be null");
        Users existing = usersRepository.findById(userId).orElse(null);
        if (existing == null)
            throw new ResourceNotFoundException("User not found: " + userId);

        String newUsername = user.getUsername();
        if (newUsername != null && !newUsername.equals(existing.getUsername())) {
            if (usersRepository.existsByUsername(newUsername))
                throw new DuplicateResourceException("User", "username", newUsername);
            existing.setUsername(newUsername);
        }
        String newEmail = user.getEmail();
        if (newEmail != null && !newEmail.equals(existing.getEmail())) {
            if (usersRepository.existsByEmail(newEmail))
                throw new DuplicateResourceException("User", "email", newEmail);
            existing.setEmail(newEmail);
        }
        String newPassword = user.getPassword();
        if (newPassword != null && !newPassword.isEmpty())
            existing.setPassword(passwordEncoder.encode(newPassword));
        if (user.getGender() != null) existing.setGender(user.getGender());
        if (user.getDob() != null) existing.setDob(user.getDob());
        String newRole = user.getRole();
        if (newRole != null && "ROLE_ADMIN".equals(existing.getRole()))
            existing.setRole(newRole);

        usersRepository.save(existing);
        return "User updated successfully";
    }

    @Override
    public Users getUserWithCart(String username) { return getUser(username); }

    @Override
    public Users getUserWithOrders(String username) { return getUser(username); }
}