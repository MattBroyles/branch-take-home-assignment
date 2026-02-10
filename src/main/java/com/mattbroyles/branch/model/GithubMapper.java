package com.mattbroyles.branch.model;


import com.mattbroyles.branch.model.api.dto.RepoResponse;
import com.mattbroyles.branch.model.api.dto.UserResponse;
import com.mattbroyles.branch.model.client.dto.GithubRepoDto;
import com.mattbroyles.branch.model.client.dto.GithubUserDto;

import java.time.Instant;
import java.util.List;

public final class GithubMapper {

    private GithubMapper() {}

    public static UserResponse toUserResponse(
            GithubUserDto user,
            List<GithubRepoDto> repos
    ) {

        return new UserResponse(
                user.login(),
                user.name(),
                user.avatarUrl(),
                user.location(),
                user.email(),
                user.url(),
                Instant.parse(user.createdAt()),
                repos.stream().map(GithubMapper::toRepo).toList()
        );
    }

    private static RepoResponse toRepo(GithubRepoDto repo) {
        return new RepoResponse(repo.name(), repo.url());
    }
}