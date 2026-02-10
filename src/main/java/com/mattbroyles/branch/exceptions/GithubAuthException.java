package com.mattbroyles.branch.exceptions;

public class GithubAuthException extends RuntimeException {
    public GithubAuthException(String message) {
        super(message);
    }
}
