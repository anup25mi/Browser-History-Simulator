package com.example.browserhistory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * JPA Entity representing a single visited page entry stored in the MySQL database.
 * 
 * Maps directly to table: 'history' in 'browser_history_db'.
 */
@Entity
@Table(name = "history")
public class HistoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    /**
     * Default constructor required by JPA.
     */
    public HistoryItem() {
    }

    /**
     * Parameterized constructor for creating new history entries.
     * 
     * @param url The visited website URL.
     * @param visitedAt The date and time when the URL was visited.
     */
    public HistoryItem(String url, LocalDateTime visitedAt) {
        this.url = url;
        this.visitedAt = visitedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getVisitedAt() {
        return visitedAt;
    }

    public void setVisitedAt(LocalDateTime visitedAt) {
        this.visitedAt = visitedAt;
    }

    /**
     * Helper method to get formatted timestamp string for simple display.
     */
    public String getFormattedVisitedAt() {
        if (visitedAt == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return visitedAt.format(formatter);
    }
}
