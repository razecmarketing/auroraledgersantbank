-- Aurora Ledger Banking System - PostgreSQL Schema
-- Following Michael Stonebraker relational design principles
-- Production-ready schema with banking-grade constraints

-- Enable required extensions for banking operations
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";

-- Create dedicated schema for banking operations
CREATE SCHEMA IF NOT EXISTS banking;
SET search_path TO banking, public;

-- ==========================================
-- Users Table - Customer Identity
-- ==========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    document VARCHAR(11) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT true,
    
    CONSTRAINT valid_document_length CHECK (LENGTH(document) = 11),
    CONSTRAINT valid_login_length CHECK (LENGTH(login) >= 3),
    CONSTRAINT valid_name_length CHECK (LENGTH(full_name) >= 2)
);

-- ==========================================
-- User Balance - Account Balance Management
-- ==========================================
CREATE TABLE IF NOT EXISTS user_balance (
    id BIGSERIAL PRIMARY KEY,
    user_login VARCHAR(50) NOT NULL UNIQUE,
    current_balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    negative_balance DECIMAL(15,2) DEFAULT 0.00,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_negative_date TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1,
    
    CONSTRAINT fk_balance_user FOREIGN KEY (user_login) REFERENCES users(login),
    CONSTRAINT positive_negative_balance CHECK (negative_balance >= 0)
);

-- ==========================================
-- Transaction History - Audit Trail
-- ==========================================
CREATE TABLE IF NOT EXISTS transaction_history (
    id BIGSERIAL PRIMARY KEY,
    user_login VARCHAR(50) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description TEXT,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    correlation_id VARCHAR(36),
    balance_after DECIMAL(15,2),
    
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_login) REFERENCES users(login),
    CONSTRAINT valid_transaction_type CHECK (transaction_type IN ('DEPOSIT', 'PAYMENT', 'TRANSFER')),
    CONSTRAINT positive_amount CHECK (amount > 0)
);

-- ==========================================
-- Banking Indexes for Performance
-- ==========================================
CREATE INDEX IF NOT EXISTS idx_users_login ON users(login);
CREATE INDEX IF NOT EXISTS idx_users_document ON users(document);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(active) WHERE active = true;

CREATE INDEX IF NOT EXISTS idx_balance_user_login ON user_balance(user_login);
CREATE INDEX IF NOT EXISTS idx_balance_last_updated ON user_balance(last_updated);

CREATE INDEX IF NOT EXISTS idx_transaction_user_login ON transaction_history(user_login);
CREATE INDEX IF NOT EXISTS idx_transaction_date ON transaction_history(transaction_date);
CREATE INDEX IF NOT EXISTS idx_transaction_correlation ON transaction_history(correlation_id);
CREATE INDEX IF NOT EXISTS idx_transaction_type ON transaction_history(transaction_type);

-- ==========================================
-- Banking Security & Audit Functions
-- ==========================================

-- Function to update balance timestamp
CREATE OR REPLACE FUNCTION update_balance_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_updated = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for automatic timestamp update
DROP TRIGGER IF EXISTS trigger_update_balance_timestamp ON user_balance;
CREATE TRIGGER trigger_update_balance_timestamp
    BEFORE UPDATE ON user_balance
    FOR EACH ROW
    EXECUTE FUNCTION update_balance_timestamp();

-- Function for balance consistency check
CREATE OR REPLACE FUNCTION check_balance_consistency()
RETURNS TRIGGER AS $$
BEGIN
    -- Ensure balance calculations are consistent
    IF NEW.current_balance < 0 AND NEW.negative_balance = 0 THEN
        NEW.negative_balance = ABS(NEW.current_balance);
        NEW.current_balance = 0;
        NEW.last_negative_date = CURRENT_TIMESTAMP;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for balance consistency
DROP TRIGGER IF EXISTS trigger_balance_consistency ON user_balance;
CREATE TRIGGER trigger_balance_consistency
    BEFORE INSERT OR UPDATE ON user_balance
    FOR EACH ROW
    EXECUTE FUNCTION check_balance_consistency();

-- ==========================================
-- Banking Views for Reporting
-- ==========================================

-- View: User Account Summary
CREATE OR REPLACE VIEW user_account_summary AS
SELECT 
    u.id,
    u.full_name,
    u.login,
    u.created_at,
    u.last_login,
    u.active,
    COALESCE(ub.current_balance, 0.00) as current_balance,
    COALESCE(ub.negative_balance, 0.00) as negative_balance,
    COALESCE(ub.current_balance - ub.negative_balance, 0.00) as effective_balance,
    ub.last_updated as balance_last_updated
FROM users u
LEFT JOIN user_balance ub ON u.login = ub.user_login;

-- View: Transaction Summary
CREATE OR REPLACE VIEW transaction_summary AS
SELECT 
    user_login,
    transaction_type,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount,
    AVG(amount) as average_amount,
    MIN(transaction_date) as first_transaction,
    MAX(transaction_date) as last_transaction
FROM transaction_history
GROUP BY user_login, transaction_type;

-- ==========================================
-- Sample Data for Development (Optional)
-- ==========================================

-- Insert sample user for testing (password: encrypted "password123")
INSERT INTO users (full_name, document, login, password) 
VALUES ('Test User', '12345678901', 'testuser', '$2a$10$encoded_password_here')
ON CONFLICT (login) DO NOTHING;

-- Insert sample balance
INSERT INTO user_balance (user_login, current_balance) 
VALUES ('testuser', 1000.00)
ON CONFLICT (user_login) DO NOTHING;

-- Grant permissions to aurora user
GRANT ALL PRIVILEGES ON SCHEMA banking TO aurora;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA banking TO aurora;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA banking TO aurora;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA banking TO aurora;

-- Set default search path for aurora user
ALTER USER aurora SET search_path TO banking, public;

COMMIT;