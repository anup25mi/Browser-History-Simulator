package com.example.browserhistory.controller;

import com.example.browserhistory.dto.ApiResponse;
import com.example.browserhistory.dto.BrowserStateDTO;
import com.example.browserhistory.dto.VisitRequest;
import com.example.browserhistory.entity.HistoryItem;
import com.example.browserhistory.service.BrowserNavigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing endpoints for browser navigation and history.
 */
@RestController
@RequestMapping("/api/browser")
@CrossOrigin(origins = "*") // Allows local cross-origin testing if needed
public class BrowserHistoryController {

    private final BrowserNavigationService navigationService;

    @Autowired
    public BrowserHistoryController(BrowserNavigationService navigationService) {
        this.navigationService = navigationService;
    }

    /**
     * Get the current browser navigation state (currentPage, canGoBack, canGoForward, stack snapshots).
     * Endpoint: GET /api/browser/state
     */
    @GetMapping("/state")
    public ResponseEntity<ApiResponse<BrowserStateDTO>> getState() {
        BrowserStateDTO state = navigationService.getCurrentState();
        return ResponseEntity.ok(ApiResponse.ok("Current state retrieved", state));
    }

    /**
     * Visit a new URL.
     * Endpoint: POST /api/browser/visit
     * Body: { "url": "https://www.google.com" }
     */
    @PostMapping("/visit")
    public ResponseEntity<ApiResponse<BrowserStateDTO>> visitUrl(@RequestBody VisitRequest request) {
        if (request == null || request.getUrl() == null || request.getUrl().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Please enter a valid website URL."));
        }
        try {
            BrowserStateDTO state = navigationService.visit(request.getUrl());
            return ResponseEntity.ok(ApiResponse.ok("Visited: " + state.getCurrentPage(), state));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    /**
     * Navigate Back.
     * Endpoint: POST /api/browser/back
     */
    @PostMapping("/back")
    public ResponseEntity<ApiResponse<BrowserStateDTO>> goBack() {
        try {
            BrowserStateDTO state = navigationService.goBack();
            return ResponseEntity.ok(ApiResponse.ok("Navigated back to " + state.getCurrentPage(), state));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    /**
     * Navigate Forward.
     * Endpoint: POST /api/browser/forward
     */
    @PostMapping("/forward")
    public ResponseEntity<ApiResponse<BrowserStateDTO>> goForward() {
        try {
            BrowserStateDTO state = navigationService.goForward();
            return ResponseEntity.ok(ApiResponse.ok("Navigated forward to " + state.getCurrentPage(), state));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    /**
     * Retrieve complete browsing history from MySQL.
     * Endpoint: GET /api/browser/history
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<HistoryItem>>> getHistory() {
        List<HistoryItem> history = navigationService.getBrowsingHistory();
        return ResponseEntity.ok(ApiResponse.ok("Browsing history fetched successfully", history));
    }

    /**
     * Clear all browsing history and reset session stacks.
     * Endpoint: DELETE /api/browser/history
     */
    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<BrowserStateDTO>> clearHistory() {
        BrowserStateDTO state = navigationService.clearHistory();
        return ResponseEntity.ok(ApiResponse.ok("Browsing history cleared successfully", state));
    }

    /**
     * Alternative POST endpoint to clear history for easy triggering from form/clients.
     * Endpoint: POST /api/browser/clear
     */
    @PostMapping("/clear")
    public ResponseEntity<ApiResponse<BrowserStateDTO>> clearHistoryPost() {
        BrowserStateDTO state = navigationService.clearHistory();
        return ResponseEntity.ok(ApiResponse.ok("Browsing history cleared successfully", state));
    }
}
