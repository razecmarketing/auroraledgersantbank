package com.aurora.ledger.application.query;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Query Parameters
 * Encapsulates filtering, pagination, and sorting parameters for queries
 */
public class QueryParameters {
    
    private final Map<String, Object> filters = new HashMap<>();
    private int pageSize = 50;
    private int pageNumber = 0;
    private String sortBy;
    private String sortDirection = "ASC";
    
    /**
     * Default constructor  initializes with default pagination values
     */
    public QueryParameters() {
        // Default values already set in field declarations
    }
    
    // Builder pattern for fluent API
    public QueryParameters filter(String key, Object value) {
        if (value != null) {
            filters.put(key, value);
        }
        return this;
    }
    
    public QueryParameters page(int pageNumber, int pageSize) {
        this.pageNumber = Math.max(0, pageNumber);
        this.pageSize = Math.max(1, Math.min(pageSize, 1000)); // Max 1000 items
        return this;
    }
    
    public QueryParameters sortBy(String field, String direction) {
        this.sortBy = field;
        this.sortDirection = direction != null ? direction.toUpperCase() : "ASC";
        return this;
    }
    
    // Getters
    public Map<String, Object> getFilters() { return new HashMap<>(filters); }
    public int getPageSize() { return pageSize; }
    public int getPageNumber() { return pageNumber; }
    public Optional<String> getSortBy() { return Optional.ofNullable(sortBy); }
    public String getSortDirection() { return sortDirection; }
    
    // Query helpers for SQL/NoSQL queries
    public int getOffset() { return pageNumber * pageSize; }
    
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getFilter(String key, Class<T> type) {
        Object value = filters.get(key);
        if (value != null && type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }
    
    public boolean hasFilter(String key) {
        return filters.containsKey(key);
    }
    
    @Override
    public String toString() {
        return String.format("QueryParams{filters=%s, page=%d, size=%d, sort=%s %s}", 
                filters, pageNumber, pageSize, sortBy, sortDirection);
    }
}










