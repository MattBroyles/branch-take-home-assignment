package com.mattbroyles.branch.service;

import com.mattbroyles.branch.config.GithubProperties;
import com.mattbroyles.branch.exceptions.*;
import com.mattbroyles.branch.model.GithubMapper;
import com.mattbroyles.branch.model.api.dto.UserResponse;
import com.mattbroyles.branch.model.client.dto.GithubRepoDto;
import com.mattbroyles.branch.model.client.dto.GithubUserDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class GithubService {

    private final RestClient restClient;
    private final GithubProperties props;

    public GithubService(RestClient restClient, GithubProperties props) {
        this.restClient = restClient;
        this.props = props;
    }

    public UserResponse getUserWithRepos(String username) {
        GithubUserDto user = fetchUser(username);
        List<GithubRepoDto> repos = fetchRepos(username);
        return GithubMapper.toUserResponse(user, repos);
    }

    private GithubUserDto fetchUser(String username) {
        String url = UriComponentsBuilder
                .fromUriString(props.userBaseUri())
                .pathSegment(username)
                .toUriString();

        try {
            return restClient.get()
                    .uri(url)
                    .headers(this::applyGithubHeaders)
                    .retrieve()
                    .body(GithubUserDto.class);
        } catch (RestClientResponseException e) {
            throw mapGithubException("fetch user", username, e);
        }

    }

    private List<GithubRepoDto> fetchRepos(String username) {
        String url = UriComponentsBuilder
                .fromUriString(props.userBaseUri())
                .pathSegment(username)
                .path(props.userRepoUriSuffix()) // e.g. "/repos"
                .toUriString();

        try {
            GithubRepoDto[] repos = restClient.get()
                    .uri(url)
                    .headers(this::applyGithubHeaders)
                    .retrieve()
                    .body(GithubRepoDto[].class);

            return repos == null ? List.of() : List.of(repos);
        } catch (RestClientResponseException e) {
            throw mapGithubException("fetch user repos", username, e);
        }
    }

    private RuntimeException mapGithubException(String action, String username, RestClientResponseException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        String exceptionResponseBody = e.getResponseBodyAsString();

        if (statusCode == HttpStatus.NOT_FOUND) {
            return new GithubUserNotFoundException(username);
        }

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            return new GithubAuthException("Github credentials rejected");
        }

        if (statusCode == HttpStatus.FORBIDDEN) {
            return new GithubForbiddenException("Github request received forbidden response, possibly due to rate-limiting or missing PAT scopes", e.getResponseBodyAsString());
        }

        if (statusCode == HttpStatus.TOO_MANY_REQUESTS) {
            return new GithubTooManyRequestsException("Github returned too many requests exception", e.getResponseBodyAsString());
        }

        if (statusCode.is5xxServerError()) {
            return new GithubUpstreamException(String.format("Github 500 error during %s with code %s: %s", action, statusCode, exceptionResponseBody), statusCode.value(), exceptionResponseBody);
        }

        return new GithubUpstreamException(String.format("GitHub request failed during %s with code %s: %s", action, statusCode, exceptionResponseBody), statusCode.value(), exceptionResponseBody);
    }

    private void applyGithubHeaders(HttpHeaders headers) {
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.USER_AGENT, "branch-take-home/1.0"); // GitHub API is happier with a User-Agent
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        if (StringUtils.hasText(props.pat())) {
            headers.setBearerAuth(props.pat());
        }
    }
}