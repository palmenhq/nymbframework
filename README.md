# Nymb Framework - Not Your Magic Beast

**Awesome, understandable components**

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
