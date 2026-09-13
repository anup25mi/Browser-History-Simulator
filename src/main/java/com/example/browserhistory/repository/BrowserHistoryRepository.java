package com.example.browserhistory.repository;

import com.example.browserhistory.entity.HistoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for persisting and querying browsing history in MySQL.
 */
@Repository
public interface BrowserHistoryRepository extends JpaRepository<HistoryItem, Long> {

    /**
     * Retrieve all history records sorted with most recent visit first.
     * 
     * @return List of HistoryItem objects in descending order of visitedAt.
     */
    List<HistoryItem> findAllByOrderByVisitedAtDesc();
}
