package com.edulearnorg.ltt.smeplanner.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edulearnorg.ltt.smeplanner.entity.User;
import com.edulearnorg.ltt.smeplanner.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    // Find users by role
    java.util.List<User> findByRole(UserRole role);
    
    // Find all users with SME role
    default java.util.List<User> findAllSMEs() {
        return findByRole(UserRole.SME);
    }
    
    // Check if SME is a reportee of supervisor
    boolean existsByIdAndSupervisorId(Long smeId, Long supervisorId);
    
    // Find reportees of a supervisor
    java.util.List<User> findBySupervisorIdAndRole(Long supervisorId, UserRole role);
}
