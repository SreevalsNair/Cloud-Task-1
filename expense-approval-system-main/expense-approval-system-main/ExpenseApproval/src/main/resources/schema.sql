CREATE DATABASE IF NOT EXISTS expense_db;
USE expense_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    password_hash VARCHAR(64)
);

SET @password_hash_column_sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE users ADD COLUMN password_hash VARCHAR(64)',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'users'
      AND column_name = 'password_hash'
);

PREPARE password_hash_column_statement FROM @password_hash_column_sql;
EXECUTE password_hash_column_statement;
DEALLOCATE PREPARE password_hash_column_statement;

CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    submitted_by BIGINT NOT NULL,
    approved_by BIGINT,
    CONSTRAINT fk_expenses_submitted_by FOREIGN KEY (submitted_by) REFERENCES users(id),
    CONSTRAINT fk_expenses_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS expense_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    changed_by BIGINT NOT NULL,
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    `timestamp` DATETIME NOT NULL,
    CONSTRAINT fk_audit_expense FOREIGN KEY (expense_id) REFERENCES expenses(id),
    CONSTRAINT fk_audit_changed_by FOREIGN KEY (changed_by) REFERENCES users(id)
);
