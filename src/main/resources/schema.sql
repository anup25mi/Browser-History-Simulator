-- =======================================================
-- SQL Script for Browser History Simulator
-- Database: browser_history_db
-- =======================================================

-- 1. Create database if it does not already exist
CREATE DATABASE IF NOT EXISTS browser_history_db;

-- 2. Switch to the database
USE browser_history_db;

-- 3. Create history table
CREATE TABLE IF NOT EXISTS history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    visited_at DATETIME NOT NULL
);
