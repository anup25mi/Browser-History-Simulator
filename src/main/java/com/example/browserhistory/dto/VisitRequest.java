package com.example.browserhistory.dto;

/**
 * Data Transfer Object for incoming URL visit requests.
 */
public class VisitRequest {

    private String url;

    public VisitRequest() {
    }

    public VisitRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
