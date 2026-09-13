package com.example.browserhistory.dsa;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Stack Implementation for Forward Browser Navigation (Forward Stack).
 * 
 * This class implements a Stack data structure manually from scratch using
 * a Singly Linked List (via the custom Node class).
 * 
 * Principles:
 * - LIFO: Last In, First Out.
 * - Used in browsers to store pages that were navigated 'Back' from.
 * - Cleared whenever a user visits a completely new page (branch navigation).
 * 
 * Time Complexities:
 * - push():    O(1) Constant Time
 * - pop():     O(1) Constant Time
 * - peek():    O(1) Constant Time
 * - isEmpty(): O(1) Constant Time
 * - size():    O(1) Constant Time
 */
public class ForwardStack {

    // Top pointer points to the most recently pushed Node
    private Node top;

    // Tracks the count of elements currently in the stack
    private int size;

    /**
     * Initializes an empty ForwardStack.
     */
    public ForwardStack() {
        this.top = null;
        this.size = 0;
    }

    /**
     * Pushes a new URL onto the top of the Forward Stack.
     * 
     * Algorithm:
     * 1. Create a new Node with the given URL.
     * 2. Point new Node's next pointer to current top.
     * 3. Move top pointer to the new Node.
     * 4. Increment size.
     * 
     * @param url The URL to push.
     */
    public void push(String url) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        Node newNode = new Node(url.trim());
        newNode.setNext(top);
        top = newNode;
        size++;
    }

    /**
     * Pops and removes the URL from the top of the Forward Stack.
     * 
     * Algorithm:
     * 1. Check if stack is empty (Underflow check).
     * 2. Save top node's URL.
     * 3. Move top pointer to top.next.
     * 4. Decrement size.
     * 5. Return the saved URL.
     * 
     * @return The popped URL string, or null if the stack is empty.
     */
    public String pop() {
        if (isEmpty()) {
            return null;
        }
        String poppedUrl = top.getUrl();
        top = top.getNext();
        size--;
        return poppedUrl;
    }

    /**
     * Looks at the URL at the top of the Forward Stack without removing it.
     * 
     * @return The top URL string, or null if empty.
     */
    public String peek() {
        if (isEmpty()) {
            return null;
        }
        return top.getUrl();
    }

    /**
     * Checks whether the Forward Stack is empty.
     * 
     * @return true if empty (top == null), false otherwise.
     */
    public boolean isEmpty() {
        return top == null;
    }

    /**
     * Returns the number of URLs currently stored in the Forward Stack.
     * 
     * @return Current size of the stack.
     */
    public int size() {
        return size;
    }

    /**
     * Clears all elements from the stack.
     * This is an essential browser operation:
     * When a user visits a brand new URL, the Forward history is wiped.
     */
    public void clear() {
        top = null;
        size = 0;
    }

    /**
     * Helper method to inspect all stack elements from Top to Bottom.
     * Useful for visualizing the stack in the frontend UI.
     * 
     * @return List of URL strings in stack order (top-most first).
     */
    public List<String> toList() {
        List<String> list = new ArrayList<>();
        Node current = top;
        while (current != null) {
            list.add(current.getUrl());
            current = current.getNext();
        }
        return list;
    }
}
