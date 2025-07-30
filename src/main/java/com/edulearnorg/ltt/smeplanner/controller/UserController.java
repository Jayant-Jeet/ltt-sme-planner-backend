package com.edulearnorg.ltt.smeplanner.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edulearnorg.ltt.smeplanner.dto.CreateUserRequest;
import com.edulearnorg.ltt.smeplanner.dto.ErrorResponse;
import com.edulearnorg.ltt.smeplanner.dto.UpdateUserRoleRequest;
import com.edulearnorg.ltt.smeplanner.dto.UserResponse;
import com.edulearnorg.ltt.smeplanner.entity.User;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;
import com.edulearnorg.ltt.smeplanner.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ltt-sme-planner/v1")
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "User management endpoints for different roles")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/users")
    @Operation(
        summary = "Get all users",
        description = "Retrieve a list of all users. Only accessible by LEAD role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('LEAD')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().toString()))
                .toList();
        return ResponseEntity.ok(userResponses);
    }
    
    @GetMapping("/users/smes")
    @Operation(
        summary = "Get all SMEs",
        description = "Retrieve a list of all SME users. Accessible by SUPERVISOR and LEAD roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SMEs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('LEAD')")
    public ResponseEntity<List<UserResponse>> getAllSMEs() {
        List<User> smes = userService.getAllSMEs();
        List<UserResponse> smeResponses = smes.stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().toString()))
                .toList();
        return ResponseEntity.ok(smeResponses);
    }

    @GetMapping("/smes/by-email")
    @Operation(
        summary = "Get SME by email",
        description = "Retrieve a specific SME user by email address. Accessible by SME, SUPERVISOR and LEAD roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SME retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "SME not found"),
        @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('SME') or hasRole('SUPERVISOR') or hasRole('LEAD')")
    public ResponseEntity<Object> getSMEByEmail(
            @Parameter(description = "SME email address") @RequestParam String email) {
        try {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (canActAsSme(user)) {
                    UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().toString());
                    return ResponseEntity.ok(userResponse);
                } else {
                    return ResponseEntity.status(404).body(new ErrorResponse(404, "User with email " + email + " is not an SME"));
                }
            } else {
                return ResponseEntity.status(404).body(new ErrorResponse(404, "SME not found with email: " + email));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal server error"));
        }
    }
    
    @GetMapping("/me")
    @Operation(
        summary = "Get current user details",
        description = "Retrieve details of the currently authenticated user. Accessible by all authenticated users."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current user retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Current user not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('SME') or hasRole('SUPERVISOR') or hasRole('LEAD')")
    public ResponseEntity<Object> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || authentication.getPrincipal() == null) {
                return ResponseEntity.status(401).body(new ErrorResponse(401, "User not authenticated"));
            }
            
            // The principal is the User entity set in JwtAuthenticationFilter
            if (authentication.getPrincipal() instanceof User currentUser) {
                UserResponse userResponse = new UserResponse(
                    currentUser.getId(), 
                    currentUser.getName(), 
                    currentUser.getEmail(), 
                    currentUser.getRole().toString()
                );
                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(401).body(new ErrorResponse(401, "Invalid authentication principal"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal server error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users/supervisors")
    @Operation(
        summary = "Get all Supervisors",
        description = "Retrieve a list of all Supervisor users. Only accessible by LEAD role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supervisors retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('LEAD')")
    public ResponseEntity<List<UserResponse>> getAllSupervisors() {
        List<User> supervisors = userService.getAllSupervisors();
        List<UserResponse> supervisorResponses = supervisors.stream()
                .map(user -> new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().toString()))
                .toList();
        return ResponseEntity.ok(supervisorResponses);
    }
    
    @GetMapping("/users/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieve a specific user by their ID. Accessible by SUPERVISOR and LEAD roles."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('LEAD')")
    public ResponseEntity<Object> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        try {
            Optional<User> userOptional = userService.getUserById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().toString());
                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.status(404).body(new ErrorResponse(404, "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal server error"));
        }
    }
    
    @PostMapping("/admin/users")
    @Operation(
        summary = "Create a new user",
        description = "Create a new user with specified role. Only accessible by LEAD role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PreAuthorize("hasRole('LEAD')")
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserRole role = UserRole.valueOf(request.getRole());
            User user = userService.createUser(request.getName(), request.getEmail(), 
                                             request.getPassword(), role);
            UserResponse userResponse = new UserResponse(user.getId(), user.getName(), 
                                                       user.getEmail(), user.getRole().toString());
            return ResponseEntity.status(201).body(userResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(400, "Invalid role specified"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(409).body(new ErrorResponse(409, e.getMessage()));
            }
            return ResponseEntity.status(400).body(new ErrorResponse(400, e.getMessage()));
        }
    }
    
    @PutMapping("/admin/users/{id}/role")
    @Operation(
        summary = "Update user role",
        description = "Update the role of an existing user. Only accessible by LEAD role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User role updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('LEAD')")
    public ResponseEntity<Object> updateUserRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        try {
            UserRole newRole = UserRole.valueOf(request.getRole());
            User user = userService.updateUserRole(id, newRole);
            UserResponse userResponse = new UserResponse(user.getId(), user.getName(), 
                                                       user.getEmail(), user.getRole().toString());
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(400, "Invalid role specified"));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage()));
            }
            return ResponseEntity.status(400).body(new ErrorResponse(400, e.getMessage()));
        }
    }
    
    @DeleteMapping("/admin/users/{id}")
    @Operation(
        summary = "Delete user",
        description = "Delete an existing user. Only accessible by LEAD role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('LEAD')")
    public ResponseEntity<Object> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage()));
            }
            return ResponseEntity.status(400).body(new ErrorResponse(400, e.getMessage()));
        }
    }
    
    /**
     * Helper method to check if a user can act as an SME
     * Since all supervisors and leads are also SMEs, this method checks for all three roles
     */
    private boolean canActAsSme(User user) {
        UserRole role = user.getRole();
        return role == UserRole.SME || role == UserRole.SUPERVISOR || role == UserRole.LEAD;
    }
}
