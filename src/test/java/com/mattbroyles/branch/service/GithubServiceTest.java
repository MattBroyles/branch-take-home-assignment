package com.mattbroyles.branch.service;

import com.mattbroyles.branch.exceptions.*;
import com.mattbroyles.branch.model.api.dto.UserResponse;
import com.mattbroyles.branch.model.client.dto.GithubRepoDto;
import com.mattbroyles.branch.model.client.dto.GithubUserDto;
import com.mattbroyles.branch.config.GithubProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GithubServiceTest {

    private RestClient restClient;
    private GithubProperties props;
    private GithubService service;

    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersUriSpec getSpec;

    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersSpec headersSpec;

    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);

        props = new GithubProperties(
                "https://api.github.com/users",
                "/repos",
                ""
        );
        service = new GithubService(restClient, props);

        getSpec = mock(RestClient.RequestHeadersUriSpec.class);
        headersSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(getSpec);
        when(getSpec.uri(anyString())).thenReturn(headersSpec);

        when(headersSpec.headers(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
    }


    @Test
    void getUserWithRepos_happyPath() {
        GithubUserDto userDto = new GithubUserDto(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                "2011-01-25T18:44:36Z"
        );

        GithubRepoDto[] reposDto = new GithubRepoDto[]{
                new GithubRepoDto("boysenberry-repo-1", "https://api.github.com/repos/octocat/boysenberry-repo-1"),
                new GithubRepoDto("boysenberry-repo-2", "https://api.github.com/repos/octocat/boysenberry-repo-2")
        };

        // First call returns user, second call returns repos
        when(responseSpec.body(eq(GithubUserDto.class))).thenReturn(userDto);
        when(responseSpec.body(eq(GithubRepoDto[].class))).thenReturn(reposDto);

        UserResponse resp = service.getUserWithRepos("octocat");

        assertEquals("octocat", resp.userName());
        assertEquals("The Octocat", resp.displayName());
        assertEquals(2, resp.repos().size());
        assertEquals("boysenberry-repo-1", resp.repos().get(0).name());
    }

    @Test
    void getUserWithRepos_whenGithub404_throwsUserNotFound() {
        when(responseSpec.body(eq(GithubUserDto.class)))
                .thenThrow(restEx(404, "{\"message\":\"Not Found\"}"));

        assertThrows(GithubUserNotFoundException.class, () -> service.getUserWithRepos("nope"));
    }

    @Test
    void getUserWithRepos_whenGithub401_throwsAuth() {
        when(responseSpec.body(eq(GithubUserDto.class)))
                .thenThrow(restEx(401, "{\"message\":\"Bad credentials\"}"));

        assertThrows(GithubAuthException.class, () -> service.getUserWithRepos("octocat"));
    }

    @Test
    void getUserWithRepos_whenGithub403_throwsForbidden() {
        when(responseSpec.body(eq(GithubUserDto.class)))
                .thenThrow(restEx(403, "{\"message\":\"forbidden\"}"));

        assertThrows(GithubForbiddenException.class, () -> service.getUserWithRepos("octocat"));
    }

    @Test
    void getUserWithRepos_whenGithub429_throwsTooManyRequests() {
        when(responseSpec.body(eq(GithubUserDto.class)))
                .thenThrow(restEx(429, "{\"message\": \"rate limit\"}"));

        assertThrows(GithubTooManyRequestsException.class, () -> service.getUserWithRepos("octocat"));
    }

    @Test
    void getUserWithRepos_whenGithub5xx_throwsUpstream() {
        when(responseSpec.body(eq(GithubUserDto.class)))
                .thenThrow(restEx(502, "{\"message\":\"bad gateway\"}"));

        assertThrows(GithubUpstreamException.class, () -> service.getUserWithRepos("octocat"));
    }

    private RestClientResponseException restEx(int status, String body) {
        return new RestClientResponseException(
                "boom",
                status,
                "status",
                null,
                body.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
    }
}
