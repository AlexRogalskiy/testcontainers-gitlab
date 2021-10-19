package com.clevercloud.testcontainers.gitlab;

import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class GitlabContainerTest {
    private static final String GitlabUsersAPI = "/api/v4/users";
    private static final String GitlabVersion = "14.3.3-ce.0";

    @Test
    public void gitlabDefaultTest() {
        try (GitlabContainer container = new GitlabContainer(GitlabVersion)) {
            container.start();
        }
    }

    @Test
    public void gitlabGetUsers() throws IOException {
        try (GitlabContainer container = new GitlabContainer(GitlabVersion)) {
            container.start();

            Response users = gitlabRequest(container, GitlabUsersAPI, container.getGitlabRootPassword());
            assertEquals(200, users.code());
        }
    }

    private Response gitlabRequest(GitlabContainer container, String path, String auth) throws IOException {
        URL apiURL = new URL(container.getProtocol(), container.getHTTPHost(), container.getHTTPPort(), path);

        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder()
                .url(apiURL)
                .get();

        if (auth != null) {
            requestBuilder = requestBuilder.header("Authorization", String.format("Bearer %s", auth));
        }

        Request request = requestBuilder.build();

        return client.newCall(request).execute();
    }
}
