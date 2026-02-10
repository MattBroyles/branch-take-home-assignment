package com.mattbroyles.branch.controller;

import com.mattbroyles.branch.exceptions.GithubUserNotFoundException;
import com.mattbroyles.branch.service.GithubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GithubController.class)
class GithubControllerErrorTest {

    @Autowired MockMvc mvc;

    @MockBean GithubService githubService;

    @Test
    void getUserRepos_whenUserNotFound_returns404() throws Exception {
        when(githubService.getUserWithRepos("nope"))
                .thenThrow(new GithubUserNotFoundException("nope"));

        mvc.perform(get("/github/users/{username}", "nope")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("GITHUB_USER_NOT_FOUND"));
    }
}
