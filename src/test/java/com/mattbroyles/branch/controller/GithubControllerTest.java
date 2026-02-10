package com.mattbroyles.branch.controller;

import com.mattbroyles.branch.model.api.dto.RepoResponse;
import com.mattbroyles.branch.model.api.dto.UserResponse;
import com.mattbroyles.branch.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GithubController.class)
class GithubControllerTest {

    @Autowired MockMvc mvc;

    @MockBean GithubService githubService;

    @Test
    void getUserRepos_returns200AndJson() throws Exception {
        UserResponse response = new UserResponse(
                "octocat",
                "The Octocat",
                "https://avatars.githubusercontent.com/u/583231?v=4",
                "San Francisco",
                null,
                "https://api.github.com/users/octocat",
                Instant.parse("2011-01-25T18:44:36Z"),
                List.of(new RepoResponse("boysenberry-repo-1", "https://api.github.com/repos/octocat/boysenberry-repo-1"))
        );

        System.out.print(response.toString());

        when(githubService.getUserWithRepos("octocat")).thenReturn(response);

        mvc.perform(get("/github/users/{username}", "octocat")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_name").value("octocat"))
                .andExpect(jsonPath("$.display_name").value("The Octocat"))
                .andExpect(jsonPath("$.repos[0].name").value("boysenberry-repo-1"));
    }
}