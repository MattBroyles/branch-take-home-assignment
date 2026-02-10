package com.mattbroyles.branch.controller;

import com.mattbroyles.branch.model.api.dto.UserResponse;
import com.mattbroyles.branch.service.GithubService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
@Validated
public class GithubController {
    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUserInfo(@PathVariable("username") @NotBlank String username)
    {
        return ResponseEntity.ok(githubService.getUserWithRepos(username));
    }
}

