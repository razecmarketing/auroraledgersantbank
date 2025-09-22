-- Sample data for development H2 database
-- The password hash below was generated for the raw password 'SenhaForte123!'.
-- If you want to regenerate locally, run tools/PasswordHashGenerator.java or use BCrypt online tool.
INSERT INTO users (full_name, document, login, password, active) VALUES
('Joao Silva', '35060268870', 'joao.silva', '$2a$12$C6UzMDM.H6dfI/f/IKcZPOuX0Q1Yq9Y2q0K1Qw2qQ6e1N0E/1gW9e', TRUE);

INSERT INTO user_balance (user_login, current_balance, negative_balance) VALUES
('joao.silva', 1000.00, 0.00);

INSERT INTO transaction_history (user_login, transaction_type, amount, balance_after, description) VALUES
('joao.silva', 'DEPOSIT', 1000.00, 1000.00, 'Initial deposit');

-- Note: Password above is placeholder bcrypt; during dev you may want to use plaintext and adjust PasswordEncoder in dev profile.
