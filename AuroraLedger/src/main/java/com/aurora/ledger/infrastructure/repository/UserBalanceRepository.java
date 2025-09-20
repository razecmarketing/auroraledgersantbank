package com.aurora.ledger.infrastructure.repository;

import com.aurora.ledger.domain.balance.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User Balance Repository
 * Database access layer for user balance management
 * Implements CQRS Write Side with optimistic locking for concurrent access
 */
@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
    
    /**
     * Find balance by user login
     */
    Optional<UserBalance> findByUserLogin(String userLogin);
    
    /**
     * Find balance by user login with pessimistic lock
     * Used for concurrent transaction safety
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ub FROM UserBalance ub WHERE ub.userLogin = :userLogin")
    Optional<UserBalance> findByUserLoginWithLock(@Param("userLogin") String userLogin);
    
    /**
     * Check if balance exists for user
     */
    boolean existsByUserLogin(String userLogin);
    
    /**
     * Find users with negative balances
     */
    @Query("SELECT ub FROM UserBalance ub WHERE ub.negativeBalance > 0")
    List<UserBalance> findUsersWithNegativeBalance();
    
    /**
     * Find users with balances above threshold
     */
    @Query("SELECT ub FROM UserBalance ub WHERE ub.currentBalance >= :threshold")
    List<UserBalance> findUsersWithBalanceAbove(@Param("threshold") BigDecimal threshold);
    
    /**
     * Find users with old negative balances (for interest calculation)
     */
    @Query("SELECT ub FROM UserBalance ub WHERE ub.negativeBalance > 0 " +
           "AND ub.lastNegativeDate <= :cutoffDate")
    List<UserBalance> findUsersWithOldNegativeBalance(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Get total positive balance in system
     */
    @Query("SELECT COALESCE(SUM(ub.currentBalance), 0) FROM UserBalance ub WHERE ub.currentBalance > 0")
    BigDecimal getTotalPositiveBalance();
    
    /**
     * Get total negative balance in system
     */
    @Query("SELECT COALESCE(SUM(ub.negativeBalance), 0) FROM UserBalance ub WHERE ub.negativeBalance > 0")
    BigDecimal getTotalNegativeBalance();
}
