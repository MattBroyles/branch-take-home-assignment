package com.mattbroyles.branch.exceptions;

public class GithubUpstreamException extends RuntimeException {
    private final int statusCode;
    private final String details;

    public GithubUpstreamException(String message, int statusCode, String details) {
        super(message);
        this.statusCode = statusCode;
        this.details = details;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDetails() {
        return details;
    }
}
