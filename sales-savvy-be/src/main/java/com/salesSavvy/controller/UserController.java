package com.salesSavvy.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salesSavvy.dto.UserResponse;
import com.salesSavvy.entity.Users;
import com.salesSavvy.service.UsersService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<Users> users = usersService.getAllUsers();
        List<UserResponse> userResponses = users.stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getGender(),
                user.getDob(),
                user.getRole()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username, Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }
        Users user = usersService.getUser(username);
        UserResponse response = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getGender(),
            user.getDob(),
            user.getRole()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @Valid @RequestBody Users user) {
        user.setId(id);
        String result = usersService.updateUser(user);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String result = usersService.deleteUser(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(Principal principal) {
        String username = principal.getName();
        Users user = usersService.getUser(username);
        UserResponse response = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getGender(),
            user.getDob(),
            user.getRole()
        );
        return ResponseEntity.ok(response);
    }

}