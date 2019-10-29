# Nymb Framework - Not Your Magic Beast

**Awesome, understandable components - bundled to an opinionated framework**

## Why yet another framework?

Just to be fair, Nymb isn't really a full-blown brand new framework that does all the magic for you. **Nymb is just a bit of glue between some really awesome and understandable libraries**. In a way similar to [Dropwizard](https://www.dropwizard.io/), but also not really.

The philosophy behind Nymb builds upon simplicity and control. What it means though is that it should be easy to understand and debug your code (no more `InvocationTargetException` spread over 300 lines of stack trace). You should know what is going on in your app (no "auto configuration"). The aim is that you should be able to "follow the code" when debugging, in order to figure out what's causing an issue, since most of us prefer to stay in our code editor over browsing documentation.

## The components

- [Dotenv](https://github.com/cdimascio/java-dotenv) and Java's standard [Properties](https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html) for configuration
- [Jackson](https://github.com/FasterXML/jackson) for object serialisation
- [Picocli](https://picocli.info/) for executing commands
- [Javalin](https://javalin.io/) for web serving
- [Logback](https://logback.qos.ch/) and [SLF4J](https://www.slf4j.org/) for logging
- [HikariCP](https://github.com/brettwooldridge/HikariCP) for database connection pooling
- [Liquibase](https://www.liquibase.org/) for database migrations
- [JDBI](http://jdbi.org/) for database interactions

You can quite easily set up these (or their friends/enemies) yourself, but Nymb saves you the hassle of it.

## A taste of Nymb

This'll start a web server, and respond "Hello world" on `localhost:7000/hello`:

```java
package com.mycompany.app;

import io.javalin.Javalin;
import org.nymbframework.core.NymbApplication;

public class MyApplication {
    public static void main(String... args) {
        final NymbApplication nymbApplication = NymbApplication.create("/my-config.properties");

        nymbApplication.getEnvironment()
            .configure(Javalin.class, javalin -> {
                javalin.get("/hello", ctx -> {
                    ctx
                        .contentType("text/plain")
                        .result("Hello world!")
                    ;
                });
            });

        nymbApplication.run(args);
    }
}
```

Compile, start with `java -jar target/my-app-SNAPSHOT.jar server`, and boom - profit 🤑 It doesn't have to be hard just because you get control.

## Getting started

Use [nymb-parent](https://github.com/palmenhq/nymbframework/tree/master/nymb-parent) to quickly get started building a [fat jar](https://stackoverflow.com/a/29925421/1959419), it contains the configuration you need for a hello-world project. The project will have an endpoint that says `{ "phrase", "hello world" }` and a CLI command that responds with `hello world`. You can find the final example at [palmenhq/nymb-hello-world](https://github.com/palmenhq/nymb-hello-world).

Your `pom.xml` should look something like

*pom.xml*
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mycompany.helloworld</groupId>
    <artifactId>nymb-hello-world</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>org.nymbframework</groupId>
        <artifactId>nymb-parent</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </parent>

    <properties>
        <java.version>1.8</java.version>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <main.class>com.mycompany.helloworld.HelloWorldApplication</main.class>
    </properties>
</project>
```

The POM extends `org.nymbframework:nymb-parent` and inherits the assembly of a fat JAR and the dependency `org.nymbframework:nymb-core`.

Before we put in the Java code you'll need to create a config file, let's name it `config.properties` in `src/main/resources`. Nymb requires us to set the `app.mode` property which is symbolic. Let's set it to `development` like so:

*config.properties*
```properties
app.mode=development
```

After this, Nymb is ready to start with the following Java code (the entry point being the `main.class` property specified in the POM).

```java
package com.mycompany.helloworld;

import org.nymbframework.core.NymbApplication;

public class HelloWorldApplication {
    public static void main(String[] args) {
        final NymbApplication nymbApplication = NymbApplication.create("/config.properties");
        nymbApplication.run(args);
    }
}
```

First, run `mvn clean install` to build the fat JAR. To verify the build succeeded we can run Nymb's built-in `check` command. Run the jar with `java -jar target/nymb-hello-world-0.1.0-SNAPSHOT.jar check`. You should see an output something similar to this:

```text
08:19:50.278 [main] INFO org.nymbframework.bundles.check.AppHealthChecker - Performing health checks
08:19:50.280 [main] INFO org.nymbframework.bundles.check.AppHealthChecker - Health checks performed
08:19:50.280 [main] INFO org.nymbframework.bundles.check.CheckCommand - All is good in appMode "development" 👍
```

And to run the web server (that still doesn't have any endpoints though), you can run `java -jar target/nymb-hello-world-0.1.0-SNAPSHOT.jar server` which'll start it on Javalin's default port 7000. This should give an output similar to:

```text
08:32:13.110 [main] INFO io.javalin.Javalin -
           __                      __ _
          / /____ _ _   __ ____ _ / /(_)____
     __  / // __ `/| | / // __ `// // // __ \
    / /_/ // /_/ / | |/ // /_/ // // // / / /
    \____/ \__,_/  |___/ \__,_//_//_//_/ /_/

        https://javalin.io/documentation

08:32:13.114 [main] DEBUG org.eclipse.jetty.util.log - Logging to Logger[org.eclipse.jetty.util.log] via org.eclipse.jetty.util.log.Slf4jLog
08:32:13.120 [main] INFO org.eclipse.jetty.util.log - Logging initialized @320ms to org.eclipse.jetty.util.log.Slf4jLog
08:32:13.121 [main] INFO io.javalin.Javalin - Starting Javalin ...
08:32:13.254 [main] INFO io.javalin.Javalin - Listening on http://localhost:7000/
08:32:13.254 [main] INFO io.javalin.Javalin - Javalin started in 133ms \o/
``` 

All properties can be overridden by environment variables, either from a `.env` file or from the system. To override the server port, for example, you can use the `server.port` property. Either you define it in `config.properties` by adding `server.port=1337`. But it can also be overridden by either using `export SERVER_PORT=1337` (which'll override any property configuration), or by putting the environment variable in a `.env` file. The configuration is resolved according to the following priority:

1. System environment variable
1. .env files
1. The *.properties file from the JAR's resources. 

### The first command

Nymb uses [Picocli](https://picocli.info/) to specify and execute commands. Commands must implement `NymbCommand` and be annotated with `@CommandLine.Command`, like so:

*HelloWorldCommand.java*
```java
package com.mycompany.helloworld;

import org.nymbframework.core.commandline.NymbCommand;
import picocli.CommandLine;

@CommandLine.Command(name = "hello-world") // hello-world is what we'll run
public class HelloWorldCommand implements NymbCommand {
    @Override
    public Integer call() throws Exception {
        System.out.println("Hello, world!");
        return 0;
    }
}
```

We'll need to register the command to Nymb's environment:

*HelloWorldApplication.java*
```java
// ...
final NymbApplication nymbApplication = NymbApplication.create("/config.properties");

nymbApplication.getEnvironment()
    .registerCommand(new HelloWorldCommand());

nymbApplication.run(args);
// ...
```

After this, lets try it out with `mvn clean install`, and `java -jar target/nymb-hello-world-0.1.0-SNAPSHOT.jar hello-world`. If everything's ok you should see `Hello, world!` in the console.

### The first endpoint

To define an endpoint we need to specify it to Javalin. Javaliin is registered in the `Environment` and can be configured like so:

*HelloWorldApplication.java*
```java
nymbApplication.getEnvironment().get(Javalin.class)
    .get("/hello-world", ctx -> {
        ctx.result("Hello, world!");
    });
```

Again, run `mvn clean install` and `java -jar target/nymb-hello-world-0.1.0-SNAPSHOT.jar server`. Javalin should start on port 7000, and you should be able to shoot off a request with `curl localhost:7000/hello-world` and get `Hello, world!` back.

By default Nymb comes with the log level `DEBUG` which can be a bit chatty, but we can change this by setting the property `log.level` to, for example, `INFO`. To do it runtime we can specify the environment variable `LOG_LEVEL` like so: `LOG_LEVEL=INFO java -jar target/nymb-hello-world-0.1.0-SNAPSHOT.jar server`
