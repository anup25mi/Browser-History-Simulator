package com.example.browserhistory.service;

import com.example.browserhistory.dsa.BackStack;
import com.example.browserhistory.dsa.ForwardStack;
import com.example.browserhistory.dto.BrowserStateDTO;
import com.example.browserhistory.entity.HistoryItem;
import com.example.browserhistory.repository.BrowserHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service managing browser navigation state using two custom Stacks
 * and persisting history records in MySQL via Spring Data JPA.
 */
@Service
public class BrowserNavigationService {

    private final BrowserHistoryRepository historyRepository;

    // Custom DSA Stacks implemented from scratch
    private final BackStack backStack;
    private final ForwardStack forwardStack;

    // Current webpage URL loaded in the browser
    private String currentPage;

    // Default home page URL
    public static final String DEFAULT_HOME_PAGE = "about:blank";

    @Autowired
    public BrowserNavigationService(BrowserHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
        this.backStack = new BackStack();
        this.forwardStack = new ForwardStack();
        this.currentPage = DEFAULT_HOME_PAGE;
    }

    /**
     * Normalizes user input URL (adds https:// if missing protocol).
     */
    public String normalizeUrl(String url) {
        if (url == null) {
            return "";
        }
        String trimmed = url.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        if (trimmed.equalsIgnoreCase("about:blank")) {
            return "about:blank";
        }
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return "https://" + trimmed;
        }
        return trimmed;
    }

    /**
     * Visit a new URL.
     * 
     * Navigation Logic:
     * 1. If currentPage exists and is not empty or "about:blank", push it to Back Stack.
     * 2. Set new URL as currentPage.
     * 3. Clear Forward Stack (branching history invalidates forward path).
     * 4. Persist history record in MySQL.
     * 
     * @param rawUrl The target URL to visit.
     * @return Current BrowserStateDTO.
     */
    public BrowserStateDTO visit(String rawUrl) {
        String url = normalizeUrl(rawUrl);
        if (url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty.");
        }

        // 1. If there was an existing active page (and not about:blank), push to Back Stack
        if (currentPage != null && !currentPage.equals(DEFAULT_HOME_PAGE)) {
            backStack.push(currentPage);
        }

        // 2. Set new URL as current page
        currentPage = url;

        // 3. Clear Forward Stack
        forwardStack.clear();

        // 4. Save to MySQL database
        HistoryItem item = new HistoryItem(url, LocalDateTime.now());
        historyRepository.save(item);

        return getCurrentState();
    }

    /**
     * Go Back to the previous page.
     * 
     * Navigation Logic:
     * 1. If Back Stack is not empty:
     *    - Push current page to Forward Stack.
     *    - Pop top element from Back Stack.
     *    - Set popped element as currentPage.
     * 
     * @return Current BrowserStateDTO.
     */
    public BrowserStateDTO goBack() {
        if (backStack.isEmpty()) {
            throw new IllegalStateException("Back history is empty. Cannot go back.");
        }

        // Push current page to Forward Stack
        forwardStack.push(currentPage);

        // Pop from Back Stack and make it current
        currentPage = backStack.pop();

        return getCurrentState();
    }

    /**
     * Go Forward to the next page.
     * 
     * Navigation Logic:
     * 1. If Forward Stack is not empty:
     *    - Push current page to Back Stack.
     *    - Pop top element from Forward Stack.
     *    - Set popped element as currentPage.
     * 
     * @return Current BrowserStateDTO.
     */
    public BrowserStateDTO goForward() {
        if (forwardStack.isEmpty()) {
            throw new IllegalStateException("Forward history is empty. Cannot go forward.");
        }

        // Push current page to Back Stack
        backStack.push(currentPage);

        // Pop from Forward Stack and make it current
        currentPage = forwardStack.pop();

        return getCurrentState();
    }

    /**
     * Returns the current state of the browser simulator.
     */
    public BrowserStateDTO getCurrentState() {
        long totalHistoryCount = historyRepository.count();
        return new BrowserStateDTO(
                currentPage,
                !backStack.isEmpty(),
                !forwardStack.isEmpty(),
                backStack.size(),
                forwardStack.size(),
                backStack.toList(),
                forwardStack.toList(),
                totalHistoryCount
        );
    }

    /**
     * Retrieves all visited URLs stored in MySQL, ordered by most recent first.
     */
    public List<HistoryItem> getBrowsingHistory() {
        return historyRepository.findAllByOrderByVisitedAtDesc();
    }

    /**
     * Clears persistent MySQL browsing history and resets in-memory navigation stacks.
     */
    public BrowserStateDTO clearHistory() {
        // Clear MySQL database
        historyRepository.deleteAll();

        // Clear in-memory DSA stacks
        backStack.clear();
        forwardStack.clear();
        currentPage = DEFAULT_HOME_PAGE;

        return getCurrentState();
    }
}
