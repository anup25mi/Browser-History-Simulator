package com.example.browserhistory.service;

import com.example.browserhistory.dto.BrowserStateDTO;
import com.example.browserhistory.entity.HistoryItem;
import com.example.browserhistory.repository.BrowserHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying the complete Browser Navigation Logic using Two Stacks.
 * Validates the exact user sequence:
 * Google -> YouTube -> GitHub -> Back -> Back -> Forward -> Visit a new page.
 */
public class BrowserNavigationServiceTest {

    private BrowserHistoryRepository mockRepository;
    private BrowserNavigationService navigationService;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(BrowserHistoryRepository.class);
        when(mockRepository.save(any(HistoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mockRepository.count()).thenReturn(3L);
        when(mockRepository.findAllByOrderByVisitedAtDesc()).thenReturn(new ArrayList<>());

        navigationService = new BrowserNavigationService(mockRepository);
    }

    @Test
    @DisplayName("Verify requested sequence: Google -> YouTube -> GitHub -> Back -> Back -> Forward -> Visit new page")
    void testExactUserNavigationSequence() {
        // Step 1: Visit Google
        BrowserStateDTO s1 = navigationService.visit("google.com");
        assertEquals("https://google.com", s1.getCurrentPage());
        assertFalse(s1.isCanGoBack(), "Initial page visit should have empty Back stack");
        assertFalse(s1.isCanGoForward(), "Initial page visit should have empty Forward stack");

        // Step 2: Visit YouTube
        BrowserStateDTO s2 = navigationService.visit("youtube.com");
        assertEquals("https://youtube.com", s2.getCurrentPage());
        assertTrue(s2.isCanGoBack(), "Back stack should now have Google");
        assertEquals(1, s2.getBackStackSize());
        assertEquals("https://google.com", s2.getBackStack().get(0));
        assertFalse(s2.isCanGoForward(), "Forward stack should remain empty");

        // Step 3: Visit GitHub
        BrowserStateDTO s3 = navigationService.visit("github.com");
        assertEquals("https://github.com", s3.getCurrentPage());
        assertTrue(s3.isCanGoBack());
        assertEquals(2, s3.getBackStackSize());
        // Top of Back stack is YouTube, below is Google
        assertEquals("https://youtube.com", s3.getBackStack().get(0));
        assertEquals("https://google.com", s3.getBackStack().get(1));
        assertFalse(s3.isCanGoForward());

        // Step 4: Click Back (from GitHub to YouTube)
        BrowserStateDTO s4 = navigationService.goBack();
        assertEquals("https://youtube.com", s4.getCurrentPage());
        assertTrue(s4.isCanGoBack(), "Back stack should still have Google");
        assertEquals(1, s4.getBackStackSize());
        assertTrue(s4.isCanGoForward(), "Forward stack should now hold GitHub");
        assertEquals(1, s4.getForwardStackSize());
        assertEquals("https://github.com", s4.getForwardStack().get(0));

        // Step 5: Click Back again (from YouTube to Google)
        BrowserStateDTO s5 = navigationService.goBack();
        assertEquals("https://google.com", s5.getCurrentPage());
        assertFalse(s5.isCanGoBack(), "Back stack is now empty");
        assertEquals(0, s5.getBackStackSize());
        assertTrue(s5.isCanGoForward(), "Forward stack has YouTube on top, then GitHub");
        assertEquals(2, s5.getForwardStackSize());
        assertEquals("https://youtube.com", s5.getForwardStack().get(0));
        assertEquals("https://github.com", s5.getForwardStack().get(1));

        // Step 6: Click Forward (from Google back to YouTube)
        BrowserStateDTO s6 = navigationService.goForward();
        assertEquals("https://youtube.com", s6.getCurrentPage());
        assertTrue(s6.isCanGoBack(), "Back stack has Google");
        assertEquals(1, s6.getBackStackSize());
        assertTrue(s6.isCanGoForward(), "Forward stack has GitHub");
        assertEquals(1, s6.getForwardStackSize());

        // Step 7: Visit a new page (e.g. wikipedia.org)
        BrowserStateDTO s7 = navigationService.visit("wikipedia.org");
        assertEquals("https://wikipedia.org", s7.getCurrentPage());
        assertTrue(s7.isCanGoBack(), "Back stack has YouTube and Google");
        assertEquals(2, s7.getBackStackSize());
        // CRITICAL BROWSER RULE: Forward stack MUST be cleared when branching into a new URL!
        assertFalse(s7.isCanGoForward(), "Forward stack MUST be wiped upon visiting new page");
        assertEquals(0, s7.getForwardStackSize());
    }

    @Test
    @DisplayName("Verify exception thrown when trying to go back with empty stack")
    void testUnderflowBackThrowsException() {
        assertThrows(IllegalStateException.class, () -> navigationService.goBack());
    }

    @Test
    @DisplayName("Verify exception thrown when trying to go forward with empty stack")
    void testUnderflowForwardThrowsException() {
        assertThrows(IllegalStateException.class, () -> navigationService.goForward());
    }
}
