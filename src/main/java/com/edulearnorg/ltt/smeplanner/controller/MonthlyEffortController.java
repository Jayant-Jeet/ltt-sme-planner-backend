package com.edulearnorg.ltt.smeplanner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edulearnorg.ltt.smeplanner.dto.ConsolidatedMonthlyEffortResponse;
import com.edulearnorg.ltt.smeplanner.dto.ErrorResponse;
import com.edulearnorg.ltt.smeplanner.dto.MonthlyEffortDetailsResponse;
import com.edulearnorg.ltt.smeplanner.entity.User;
import com.edulearnorg.ltt.smeplanner.service.MonthlyEffortService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller for managing monthly effort details
 */
@RestController
@RequestMapping("/ltt-sme-planner/v1")
@CrossOrigin(origins = "*")
@Tag(name = "Monthly Effort Details", description = "APIs for viewing monthly effort details of SMEs")
@SecurityRequirement(name = "bearerAuth")
public class MonthlyEffortController {

    private static final String MONTH_YEAR_REGEX = "\\d{4}-\\d{2}";
    private static final String INVALID_MONTH_YEAR_FORMAT_MSG = "Invalid month-year format. Use YYYY-MM format.";
    private static final String ROLE_SUPERVISOR = "SUPERVISOR";
    private static final String ROLE_LEAD = "LEAD";
    private static final String NOT_FOUND_LITERAL = "not found";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Internal server error: ";
    private static final String ACCESS_DENIED_MSG = "Access denied - insufficient permissions";
    
    private final MonthlyEffortService monthlyEffortService;

    public MonthlyEffortController(MonthlyEffortService monthlyEffortService) {
        this.monthlyEffortService = monthlyEffortService;
    }
    
    @GetMapping("/supervisor/sme/{smeId}/effort/{monthYear}")
    @Operation(
        summary = "Get monthly effort details of SME by Supervisor or Lead",
        description = "Retrieve monthly effort details of a specific SME. Accessible by SUPERVISOR role (only for their reportees) and LEAD role (for any SME)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Monthly effort details retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid month-year format"),
        @ApiResponse(responseCode = "403", description = "Cannot view the report - SME is not a reportee (for supervisors only)"),
        @ApiResponse(responseCode = "404", description = "SME not found")
    })
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('LEAD')")
    public ResponseEntity<Object> getMonthlyEffortDetails(
            @Parameter(description = "SME ID", example = "1") @PathVariable Long smeId,
            @Parameter(description = "Month-Year in YYYY-MM format", example = "2025-07") @PathVariable String monthYear) {
        try {
            // Validate month-year format
            if (!monthYear.matches(MONTH_YEAR_REGEX)) {
                return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, INVALID_MONTH_YEAR_FORMAT_MSG));
            }
            
            // Get user information from authentication context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication);
            String userRole = getUserRoleFromAuthentication(authentication);
            
            MonthlyEffortDetailsResponse response;

            // Check for null userRole before proceeding
            if (userRole == null) {
                return ResponseEntity.status(403)
                    .body(new ErrorResponse(403, ACCESS_DENIED_MSG));
            }

            // Use rule switch for userRole
            response = switch (userRole) {
                case ROLE_LEAD ->
                    monthlyEffortService.getMonthlyEffortDetailsForLead(smeId, monthYear);
                case ROLE_SUPERVISOR ->
                    monthlyEffortService.getMonthlyEffortDetailsForSupervisor(userId, smeId, monthYear);
                default -> null;
            };

            if (response == null) {
                return ResponseEntity.status(403)
                    .body(new ErrorResponse(403, ACCESS_DENIED_MSG));
            }

            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Cannot view the report")) {
                return ResponseEntity.status(403)
                    .body(new ErrorResponse(403, "Cannot view the report - SME is not a reportee"));
            } else if (e.getMessage().contains(NOT_FOUND_LITERAL)) {
                return ResponseEntity.status(404)
                    .body(new ErrorResponse(404, e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, e.getMessage()));
            }
        }
    }
    
    @GetMapping("/supervisor/reportees/effort/{monthYear}")
    @Operation(
        summary = "Get consolidated monthly effort details of all reportees",
        description = "Retrieve consolidated monthly effort details of all reportees for a supervisor. Only accessible by SUPERVISOR role (for their reportees) and LEAD role (for all SMEs)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consolidated monthly effort details retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid month-year format"),
        @ApiResponse(responseCode = "404", description = "Supervisor not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - user is not authorized")
    })
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('LEAD')")
    public ResponseEntity<Object> getConsolidatedMonthlyEffortDetails(
            @Parameter(description = "Month-Year in YYYY-MM format", example = "2025-07") @PathVariable String monthYear) {
        
        try {
            // Validate month-year format
            if (!monthYear.matches(MONTH_YEAR_REGEX)) {
                return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, INVALID_MONTH_YEAR_FORMAT_MSG));
            }
            
            // Get user information from authentication context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = getUserIdFromAuthentication(authentication);
            String userRole = getUserRoleFromAuthentication(authentication);
            
            ConsolidatedMonthlyEffortResponse response;
            
            // Check user role and apply appropriate access control
            response = switch (userRole) {
                case ROLE_LEAD ->
                    // LEADs can view consolidated effort details of all SMEs
                    monthlyEffortService.getConsolidatedMonthlyEffortDetailsForLead(monthYear);
                case ROLE_SUPERVISOR ->
                    // SUPERVISORs can only view their reportees' consolidated effort details
                    monthlyEffortService.getConsolidatedMonthlyEffortDetails(userId, monthYear);
                default -> null;
            };
            
            if (response == null) {
                return ResponseEntity.status(403)
                    .body(new ErrorResponse(403, ACCESS_DENIED_MSG));
            }
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains(NOT_FOUND_LITERAL)) {
                return ResponseEntity.status(404)
                    .body(new ErrorResponse(404, e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ErrorResponse(500, INTERNAL_SERVER_ERROR_MSG + e.getMessage()));
        }
    }
    
    @GetMapping("/lead/sme/{smeId}/effort/{monthYear}")
    @Operation(
        summary = "Get monthly effort details of any SME by Lead",
        description = "Retrieve monthly effort details of any SME. Only accessible by LEAD role with unrestricted access."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Monthly effort details retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid month-year format"),
        @ApiResponse(responseCode = "404", description = "SME not found")
    })
    @PreAuthorize("hasRole('LEAD')")
    public ResponseEntity<Object> getMonthlyEffortDetailsByLead(
            @Parameter(description = "SME ID", example = "1") @PathVariable Long smeId,
            @Parameter(description = "Month-Year in YYYY-MM format", example = "2025-07") @PathVariable String monthYear) {
        
        try {
            // Validate month-year format
            if (!monthYear.matches(MONTH_YEAR_REGEX)) {
                return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, INVALID_MONTH_YEAR_FORMAT_MSG));
            }
            
            // LEADs have unrestricted access to any SME's data
            MonthlyEffortDetailsResponse response = monthlyEffortService
                .getMonthlyEffortDetailsForLead(smeId, monthYear);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains(NOT_FOUND_LITERAL)) {
                return ResponseEntity.status(404)
                    .body(new ErrorResponse(404, e.getMessage()));
            } else {
                return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, e.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ErrorResponse(500, INTERNAL_SERVER_ERROR_MSG + e.getMessage()));
        }
    }
    
    @GetMapping("/lead/smes/effort/{monthYear}")
    @Operation(
        summary = "Get consolidated monthly effort details of all SMEs by Lead",
        description = "Retrieve consolidated monthly effort details of all SMEs in the organization. Only accessible by LEAD role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consolidated monthly effort details retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid month-year format")
    })
    @PreAuthorize("hasRole('LEAD')")
    public ResponseEntity<Object> getConsolidatedMonthlyEffortDetailsByLead(
            @Parameter(description = "Month-Year in YYYY-MM format", example = "2025-07") @PathVariable String monthYear) {
        
        try {
            // Validate month-year format
            if (!monthYear.matches(MONTH_YEAR_REGEX)) {
                return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, INVALID_MONTH_YEAR_FORMAT_MSG));
            }
            
            // LEADs have unrestricted access to all SMEs' data
            ConsolidatedMonthlyEffortResponse response = monthlyEffortService
                .getConsolidatedMonthlyEffortDetailsForLead(monthYear);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(new ErrorResponse(500, INTERNAL_SERVER_ERROR_MSG + e.getMessage()));
        }
    }
    
    /**
     * Helper method to extract user ID from authentication context
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Get user from authentication principal (set by JwtAuthenticationFilter)
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        
        // Fallback: this should not happen if JWT filter is working correctly
        throw new UserIdExtractionException("Unable to extract user ID from authentication context");
    }
    
/**
 * Helper method to extract user role from authentication context
 */
private String getUserRoleFromAuthentication(Authentication authentication) {
    // Get user from authentication principal (set by JwtAuthenticationFilter)
    if (authentication.getPrincipal() instanceof User user) {
        return user.getRole().toString();
    }
    // Fallback: extract from authorities
    if (authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_LEAD"))) {
        return "LEAD";
    } else if (authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPERVISOR"))) {
        return ROLE_SUPERVISOR;
    }
    
    // Final fallback: this should not happen if JWT filter is working correctly
    throw new UserRoleExtractionException("Unable to extract user role from authentication context");
}
class UserRoleExtractionException extends RuntimeException {
    public UserRoleExtractionException(String message) {
        super(message);
    }
}

/**
 * Custom exception for user ID extraction errors.
 */
class UserIdExtractionException extends RuntimeException {
    public UserIdExtractionException(String message) {
        super(message);
    }
}
}
