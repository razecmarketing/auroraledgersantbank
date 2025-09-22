package com.aurora.ledger.infrastructure.repository;

import com.aurora.ledger.domain.transaction.TransactionHistory;
import com.aurora.ledger.domain.transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction History Repository
 * Database access layer for transaction records
 * Implements CQRS Write Side persistence for banking transactions
 */
@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    
    /**
     * Find all transactions for a user ordered by date (newest first)
     */
    List<TransactionHistory> findByUserLoginOrderByTransactionDateDesc(String userLogin);
    
    /**
     * Find transactions by user and type
     */
    List<TransactionHistory> findByUserLoginAndTransactionTypeOrderByTransactionDateDesc(
        String userLogin, TransactionType transactionType);
    
    /**
     * Find transactions in date range for user
     */
    @Query("SELECT th FROM TransactionHistory th WHERE th.userLogin = :userLogin " +
           "AND th.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY th.transactionDate DESC")
    List<TransactionHistory> findByUserLoginAndDateRange(
        @Param("userLogin") String userLogin,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count total transactions for user
     */
    long countByUserLogin(String userLogin);
    
    /**
     * Find transactions by correlation ID (for tracking)
     */
    List<TransactionHistory> findByCorrelationId(String correlationId);
    
    /**
     * Get last N transactions for user
     */
    @Query(value = "SELECT th FROM TransactionHistory th WHERE th.userLogin = :userLogin " +
           "ORDER BY th.transactionDate DESC")
    List<TransactionHistory> findTopNByUserLogin(@Param("userLogin") String userLogin, 
                                                org.springframework.data.domain.Pageable pageable);
}










