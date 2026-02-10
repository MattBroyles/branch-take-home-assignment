package com.mattbroyles.branch.exceptions;

public class GithubTooManyRequestsException extends RuntimeException {
    private final String details;

    public GithubTooManyRequestsException(String message, String details) {
        super(message);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}
