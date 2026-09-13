package com.example.browserhistory.dsa;

/**
 * Node class representing a single element in our custom Linked List Stack.
 * 
 * In a Linked List Stack:
 * - 'url' stores the website address (data).
 * - 'next' is the reference/pointer to the next node beneath it.
 * 
 * Time Complexity for operations using Node:
 * - Allocation: O(1)
 */
public class Node {

    private String url;
    private Node next;

    /**
     * Constructor to create a new Node with the given URL.
     * 
     * @param url The website URL string.
     */
    public Node(String url) {
        this.url = url;
        this.next = null;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
