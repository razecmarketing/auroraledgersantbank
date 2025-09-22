-- Minimal schema for H2 to mimic production tables used in tests
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  document VARCHAR(11) NOT NULL UNIQUE,
  login VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_login TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_balance (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_login VARCHAR(50) NOT NULL UNIQUE,
  current_balance NUMERIC(15,2) NOT NULL DEFAULT 0,
  negative_balance NUMERIC(15,2),
  last_negative_date TIMESTAMP,
  last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transaction_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_login VARCHAR(50) NOT NULL,
  transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  transaction_type VARCHAR(50) NOT NULL,
  amount NUMERIC(15,2) NOT NULL,
  balance_after NUMERIC(15,2),
  description VARCHAR(255)
);
