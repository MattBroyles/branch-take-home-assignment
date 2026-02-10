package com.mattbroyles.branch.model.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        String userName,
        String displayName,
        String avatar,
        String geoLocation,
        String email,
        String url,

        @JsonFormat(pattern = "EEE, dd MMM yyyy HH:mm:ss z", timezone = "GMT")
        Instant createdAt,
        List<RepoResponse> repos
) {}