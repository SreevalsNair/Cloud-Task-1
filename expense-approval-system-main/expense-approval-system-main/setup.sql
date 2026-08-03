CREATE DATABASE IF NOT EXISTS expense_db;
USE expense_db;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(255),
    role VARCHAR(50) CHECK (role IN ('EMPLOYEE', 'MANAGER', 'FINANCE'))
    );

CREATE TABLE IF NOT EXISTS expenses (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        amount DECIMAL(38,2),
    description VARCHAR(255),
    status VARCHAR(50) CHECK (status IN ('SUBMITTED', 'APPROVED', 'REJECTED', 'REIMBURSED')),
    submitted_by BIGINT,
    approved_by BIGINT,
    FOREIGN KEY (submitted_by) REFERENCES users(id),
    FOREIGN KEY (approved_by) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS expense_audit (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             expense_id BIGINT,
                                             changed_by BIGINT,
                                             from_status VARCHAR(50),
    to_status VARCHAR(50),
    timestamp DATETIME,
    FOREIGN KEY (expense_id) REFERENCES expenses(id),
    FOREIGN KEY (changed_by) REFERENCES users(id)
    );

INSERT INTO users (name, role) VALUES ('Sree', 'EMPLOYEE');
INSERT INTO users (name, role) VALUES ('Raj', 'MANAGER');
INSERT INTO users (name, role) VALUES ('Priya', 'FINANCE');