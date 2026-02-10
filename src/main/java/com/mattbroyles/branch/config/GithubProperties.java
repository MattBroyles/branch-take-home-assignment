package com.mattbroyles.branch.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix="github")
public record GithubProperties(
    @NotBlank String userBaseUri,
    @NotBlank String userRepoUriSuffix,
    String pat
) {}
