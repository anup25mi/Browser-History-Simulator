package com.example.browserhistory.dsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for the Custom BackStack and ForwardStack implementations.
 * Verifies that LIFO principles, push(), pop(), peek(), isEmpty(), and size()
 * work correctly without any built-in Java collection stacks.
 */
public class StackTest {

    private BackStack backStack;
    private ForwardStack forwardStack;

    @BeforeEach
    void setUp() {
        backStack = new BackStack();
        forwardStack = new ForwardStack();
    }

    @Test
    @DisplayName("Test empty stack properties")
    void testInitialEmptyState() {
        assertTrue(backStack.isEmpty(), "New back stack must be empty");
        assertEquals(0, backStack.size(), "New back stack size must be 0");
        assertNull(backStack.peek(), "Peek on empty stack must return null");
        assertNull(backStack.pop(), "Pop on empty stack must return null without crashing");

        assertTrue(forwardStack.isEmpty(), "New forward stack must be empty");
        assertEquals(0, forwardStack.size(), "New forward stack size must be 0");
        assertNull(forwardStack.peek(), "Peek on empty forward stack must return null");
        assertNull(forwardStack.pop(), "Pop on empty forward stack must return null without crashing");
    }

    @Test
    @DisplayName("Test push and peek operations")
    void testPushAndPeek() {
        backStack.push("https://www.google.com");
        assertFalse(backStack.isEmpty());
        assertEquals(1, backStack.size());
        assertEquals("https://www.google.com", backStack.peek());

        backStack.push("https://www.youtube.com");
        assertEquals(2, backStack.size());
        assertEquals("https://www.youtube.com", backStack.peek());
    }

    @Test
    @DisplayName("Test LIFO (Last In First Out) behavior on pop")
    void testLifoPopOrder() {
        backStack.push("Page 1");
        backStack.push("Page 2");
        backStack.push("Page 3");

        assertEquals(3, backStack.size());

        // Page 3 was pushed last, so it must pop first
        assertEquals("Page 3", backStack.pop());
        assertEquals(2, backStack.size());
        assertEquals("Page 2", backStack.peek());

        // Page 2 pops next
        assertEquals("Page 2", backStack.pop());
        assertEquals(1, backStack.size());

        // Page 1 pops last
        assertEquals("Page 1", backStack.pop());
        assertEquals(0, backStack.size());
        assertTrue(backStack.isEmpty());
    }

    @Test
    @DisplayName("Test clearing the stack")
    void testClearStack() {
        forwardStack.push("https://github.com");
        forwardStack.push("https://stackoverflow.com");
        assertEquals(2, forwardStack.size());

        forwardStack.clear();
        assertTrue(forwardStack.isEmpty());
        assertEquals(0, forwardStack.size());
        assertNull(forwardStack.peek());
    }

    @Test
    @DisplayName("Test null and empty string push rejection")
    void testInvalidPushHandling() {
        backStack.push(null);
        backStack.push("");
        backStack.push("   ");
        assertTrue(backStack.isEmpty());
        assertEquals(0, backStack.size());
    }
}
