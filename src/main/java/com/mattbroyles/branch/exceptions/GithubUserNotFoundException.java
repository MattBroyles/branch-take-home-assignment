package com.mattbroyles.branch.exceptions;

public class GithubUserNotFoundException extends RuntimeException {
    public GithubUserNotFoundException(String userName) {
        super(String.format("Github user %s not found", userName));
    }
}
