package com.mattbroyles.branch.exceptions;

public class GithubForbiddenException extends RuntimeException {
    private final String details;

    public GithubForbiddenException(String message, String details) {
        super(message); this.details = details;
    }

    public String getDetails() {
        return details;
    }
}
