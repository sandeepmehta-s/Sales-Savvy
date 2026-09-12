package com.salesSavvy.user.repository;

import com.salesSavvy.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    List<Users> findByRole(String role);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}