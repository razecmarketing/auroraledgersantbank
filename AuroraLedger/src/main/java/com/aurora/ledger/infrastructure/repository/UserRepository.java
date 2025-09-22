package com.aurora.ledger.infrastructure.repository;

import com.aurora.ledger.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository
 * Database access layer for User entities
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by login username
     */
    Optional<User> findByLogin(String login);
    
    /**
     * Find user by document number (CPF)
     */
    Optional<User> findByDocument(String document);
    
    /**
     * Check if login already exists
     */
    boolean existsByLogin(String login);
    
    /**
     * Check if document (CPF) already exists
     */
    boolean existsByDocument(String document);
    
    /**
     * Find active users only
     */
    Optional<User> findByLoginAndActiveTrue(String login);
}










