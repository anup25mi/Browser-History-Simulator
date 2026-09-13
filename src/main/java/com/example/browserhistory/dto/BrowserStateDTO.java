package com.example.browserhistory.dto;

import java.util.List;

/**
 * Data Transfer Object representing the current live state of the browser simulator.
 * Sent to the frontend to update buttons, display the active webpage,
 * and visualize the internal Back and Forward Stacks in real time.
 */
public class BrowserStateDTO {

    private String currentPage;
    private boolean canGoBack;
    private boolean canGoForward;
    private int backStackSize;
    private int forwardStackSize;
    private List<String> backStack;
    private List<String> forwardStack;
    private long totalHistoryCount;

    public BrowserStateDTO() {
    }

    public BrowserStateDTO(String currentPage, boolean canGoBack, boolean canGoForward,
                           int backStackSize, int forwardStackSize,
                           List<String> backStack, List<String> forwardStack,
                           long totalHistoryCount) {
        this.currentPage = currentPage;
        this.canGoBack = canGoBack;
        this.canGoForward = canGoForward;
        this.backStackSize = backStackSize;
        this.forwardStackSize = forwardStackSize;
        this.backStack = backStack;
        this.forwardStack = forwardStack;
        this.totalHistoryCount = totalHistoryCount;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isCanGoBack() {
        return canGoBack;
    }

    public void setCanGoBack(boolean canGoBack) {
        this.canGoBack = canGoBack;
    }

    public boolean isCanGoForward() {
        return canGoForward;
    }

    public void setCanGoForward(boolean canGoForward) {
        this.canGoForward = canGoForward;
    }

    public int getBackStackSize() {
        return backStackSize;
    }

    public void setBackStackSize(int backStackSize) {
        this.backStackSize = backStackSize;
    }

    public int getForwardStackSize() {
        return forwardStackSize;
    }

    public void setForwardStackSize(int forwardStackSize) {
        this.forwardStackSize = forwardStackSize;
    }

    public List<String> getBackStack() {
        return backStack;
    }

    public void setBackStack(List<String> backStack) {
        this.backStack = backStack;
    }

    public List<String> getForwardStack() {
        return forwardStack;
    }

    public void setForwardStack(List<String> forwardStack) {
        this.forwardStack = forwardStack;
    }

    public long getTotalHistoryCount() {
        return totalHistoryCount;
    }

    public void setTotalHistoryCount(long totalHistoryCount) {
        this.totalHistoryCount = totalHistoryCount;
    }
}
