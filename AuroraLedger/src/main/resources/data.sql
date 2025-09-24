-- Sample data for development H2 database
-- The password hash below was generated for the raw password 'SenhaForte123!'.
-- If you want to regenerate locally, run tools/PasswordHashGenerator.java or use BCrypt online tool.
-- Explicit created_at to avoid H2 default timing issues in test initialization
INSERT INTO users (full_name, document, login, password, active, created_at) VALUES
('Joao Silva', '35060268870', 'joao.silva', '$2a$12$C6UzMDM.H6dfI/f/IKcZPOuX0Q1Yq9Y2q0K1Qw2qQ6e1N0E/1gW9e', TRUE, CURRENT_TIMESTAMP);

-- Include last_updated explicitly to avoid H2 timing/default anomalies during test context initialization
INSERT INTO user_balance (user_login, current_balance, negative_balance, last_updated) VALUES
('joao.silva', 1000.00, 0.00, CURRENT_TIMESTAMP);

-- Explicit transaction_date to satisfy H2 when default CURRENT_TIMESTAMP is not picked up during initialization
INSERT INTO transaction_history (user_login, transaction_date, transaction_type, amount, balance_after, description) VALUES
('joao.silva', CURRENT_TIMESTAMP, 'DEPOSIT', 1000.00, 1000.00, 'Initial deposit');

-- Note: Password above is placeholder bcrypt; during dev you may want to use plaintext and adjust PasswordEncoder in dev profile.
