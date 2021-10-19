# testcontainers-gitlab

This implements a Java [testcontainer](https://www.testcontainers.org/) for [Gitlab](https://about.gitlab.com/).

## What dependency to add

Add the following to your project:

### pom.xml

```xml
<dependency>
  <groupId>com.clever-cloud</groupId>
  <artifactId>testcontainers-gitlab</artifactId>
  <version>1.0.0</version>
</dependency>
```

### build.gradle

```
implementation 'com.clever-cloud:testcontainers-gitlab:1.0.0'
```

### build.sbt

```scala
libraryDependencies += "com.clever-cloud" % "testcontainers-gitlab" % "1.0.0"
```

## Usage example

```java

try(GitlabContainer container=new GitlabContainer(GITLAB_VERSION)){
        // Start the container. This step might take some time...
        container.start();

// Do whatever you want with the http client ...
        final HttpClient client= /* Create HTTP Client with host container.getHTTPHostAddress() */

        Response response=client.request("GET","/api/v4/users").addHeader("Authorization", "Bearer " + container.getGitlabRootPassword());
        ⋯
}
```

(Disclaimer: this code will not compile. It's just so you get an idea!)
