package com.mattbroyles.branch.web;

import com.mattbroyles.branch.exceptions.GithubAuthException;
import com.mattbroyles.branch.exceptions.GithubForbiddenException;
import com.mattbroyles.branch.exceptions.GithubUpstreamException;
import com.mattbroyles.branch.exceptions.GithubUserNotFoundException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorResponse(String code, String message, Instant timestamp) {}

    @ExceptionHandler(GithubUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(GithubUserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("GITHUB_USER_NOT_FOUND", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(GithubAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuth(GithubAuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("GITHUB_AUTH_ERROR", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(GithubForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(GithubForbiddenException ex) {
        // You could choose 429 for rate-limits; 403 is fine if you can’t distinguish.
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("GITHUB_FORBIDDEN", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(GithubUpstreamException.class)
    public ResponseEntity<ErrorResponse> handleUpstream(GithubUpstreamException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorResponse("GITHUB_UPSTREAM_ERROR", ex.getMessage(), Instant.now()));
    }
}
