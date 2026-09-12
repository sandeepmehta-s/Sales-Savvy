package com.salesSavvy.user.controller;

import com.salesSavvy.user.dto.UserResponse;
import com.salesSavvy.user.entity.Users;
import com.salesSavvy.user.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    private UserResponse toResponse(Users user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
            user.getGender(), user.getDob(), user.getRole());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(usersService.getAllUsers().stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username, Principal principal) {
        if (!principal.getName().equals(username)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(toResponse(usersService.getUser(username)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable String id, @Valid @RequestBody Users user) {
        user.setId(id);
        return ResponseEntity.ok(usersService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(usersService.deleteUser(id));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(Principal principal) {
        return ResponseEntity.ok(toResponse(usersService.getUser(principal.getName())));
    }
}