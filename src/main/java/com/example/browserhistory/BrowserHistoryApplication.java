package com.example.browserhistory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Entry Point for Browser History Simulator Application.
 * 
 * College Java DSA Mini-Project.
 * Features:
 * - Custom Stack implementation (BackStack & ForwardStack)
 * - Spring Boot 3 REST APIs
 * - Spring Data JPA with MySQL persistence
 * - Interactive Browser Simulator UI with Real-time Stack Visualizer
 */
@SpringBootApplication
public class BrowserHistoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrowserHistoryApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("🚀 Browser History Simulator is running!");
        System.out.println("🌐 Open in your browser: http://localhost:8080");
        System.out.println("==========================================================");
    }
}
