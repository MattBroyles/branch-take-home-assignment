package com.mattbroyles.branch.service;

import com.mattbroyles.branch.model.client.dto.GithubRepoDto;
import com.mattbroyles.branch.model.client.dto.GithubUserDto;
import com.mattbroyles.branch.config.GithubProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GithubServiceAuthHeaderTest {

    private RestClient restClient;
    private GithubService service;

    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersUriSpec getSpec;

    @SuppressWarnings("rawtypes")
    private RestClient.RequestHeadersSpec headersSpec;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);

        GithubProperties props = new GithubProperties(
                "https://api.github.com/users",
                "/repos",
                "don't commit this by accident"
        );

        service = new GithubService(restClient, props);

        getSpec = mock(RestClient.RequestHeadersUriSpec.class);
        headersSpec = mock(RestClient.RequestHeadersSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) getSpec);
        when(getSpec.uri(anyString())).thenReturn((RestClient.RequestHeadersSpec) headersSpec);
        when(headersSpec.headers(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        // minimal happy-path responses so the call completes
        when(responseSpec.body(eq(GithubUserDto.class)))
                .thenReturn(new GithubUserDto(
                        "octocat",
                        "The Octocat",
                        "avatar",
                        "SF",
                        null,
                        "url",
                        "2011-01-25T18:44:36Z"
                ));

        when(responseSpec.body(eq(GithubRepoDto[].class)))
                .thenReturn(new GithubRepoDto[0]);
    }

    @Test
    void includesAuthorizationHeader_whenPatIsPresent() {
        ArgumentCaptor<Consumer<HttpHeaders>> headersCaptor =
                ArgumentCaptor.forClass(Consumer.class);

        service.getUserWithRepos("octocat");

        verify(headersSpec, atLeastOnce()).headers(headersCaptor.capture());

        HttpHeaders headers = new HttpHeaders();
        headersCaptor.getValue().accept(headers);

        assertTrue(headers.containsKey(HttpHeaders.AUTHORIZATION));
        assertEquals(
                "Bearer don't commit this by accident",
                headers.getFirst(HttpHeaders.AUTHORIZATION)
        );
    }
}
