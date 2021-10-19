package com.clevercloud.testcontainers.gitlab;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.time.Duration;

public class GitlabContainer extends GenericContainer<GitlabContainer> {
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("gitlab/gitlab-ce");
    private static final String DEFAULT_TAG = "14.3.3-ce.0";
    private static final String GITLAB_PROTOCOL = "http";
    private static final Integer GITLAB_HTTP_PORT = 80;
    private static final Integer GITLAB_SSH_PORT = 22;
    private static final String GITLAB_API_TOKEN = "TestContainerGitlabToken";

    public GitlabContainer() {
        this(DEFAULT_TAG);
    }

    public GitlabContainer(final String tag) {
        this(DEFAULT_IMAGE_NAME.withTag(tag));
    }

    public GitlabContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);

        logger().info("Starting a Gitlab container using [{}]", dockerImageName);
        addExposedPort(GITLAB_HTTP_PORT);
        addExposedPort(GITLAB_SSH_PORT);
        setWaitStrategy(new HttpWaitStrategy()
                .forPort(GITLAB_HTTP_PORT)
                .forStatusCode(200)
                .withStartupTimeout(Duration.ofMinutes(5))
        );
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        try {
            initAPIToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initAPIToken() throws IOException, InterruptedException {
        String createToken = String.format(
                "token = User.find_by_username('root').personal_access_tokens.create(scopes: [:api, :sudo], name: 'Automation Token'); token.set_token('%s'); token.save",
                GITLAB_API_TOKEN
        );
        ExecResult result = execInContainer("gitlab-rails", "runner", createToken);
        if (result.getExitCode() > 0) {
            String error = "Failed to create root personal access token, exit code: " + result.getExitCode();
            logger().error(error);
            if (!result.getStdout().isEmpty()) {
                logger().error("Stdout: " + result.getStdout());
            }
            if (!result.getStderr().isEmpty()) {
                logger().error("Stderr: " + result.getStderr());
            }
            throw new RuntimeException(error);
        }
    }

    public String getGitlabRootPassword() {
        return GITLAB_API_TOKEN;
    }

    public String getHTTPHost() {
        return getHost();
    }

    public Integer getHTTPPort() {
        return getMappedPort(GITLAB_HTTP_PORT);
    }

    public String getHTTPHostAddress() {
        return getHost() + ":" + getMappedPort(GITLAB_HTTP_PORT);
    }

    public String getProtocol() {
        return GITLAB_PROTOCOL;
    }
}
