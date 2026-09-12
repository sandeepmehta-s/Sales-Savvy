package com.salesSavvy.user.repository;

import com.salesSavvy.user.entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UsersRepository extends MongoRepository<Users, String> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}