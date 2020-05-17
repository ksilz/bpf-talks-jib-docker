# Google Jib: Smaller & Faster Docker Images for Java Applications

## What Is This?

[Google Jib](https://github.com/GoogleContainerTools/jib) is a Docker image build tool by Google. It produces **smaller** Docker images for your Java applications: You usually push **90%+ less data** to your Docker repository. So Jib **saves a ton of time & network bandwidth**!

I demonstrated this in a lightning talk to the [London Java Community](https://www.meetup.com/Londonjavacommunity/) (LJC) on May 15, 2020. Please **read the slides first!**

- If you're on an Apple device and have Apple's Keynote presentation program installed, then you **[should read the animated Keynote slides](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/Google%20JIB%20for%20Java%20Docker%20Images%20-%20LJC%20Lightning%20Talk%202020.key)**.
- Everybody else should **[should read the PDF slides](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/Google%20JIB%20for%20Java%20Docker%20Images%20-%20LJC%20Lightning%20Talk%202020.pdf)**.

## Why Should I Believe You?

You **shouldn't** believe everything you read on the Internet. Granted, I'm a full-stack developer with [21 years of Java experience](https://ksilz.com). And I think of myself as trustworthy. But don't we all?!

That's why you can compare the Docker images from both a Dockerfile and Google Jib yourself with the instructions here. And you can build the Docker Images yourself, seeing the effect of your changes! All you need is in this repo!

And now for some shameless self-promotion. I write a blog about how to get [better Java projects faster with JHipster and Docker](https://betterprojectsfaster.com). It's been dormant [since the end of 2019](https://betterprojectsfaster.com/blog/), but I'll pick it up again by June 2020. Spoiler alert: I'll also write about [Flutter](https://flutter.dev), Google's cross-platform UI toolkit for native mobile, web & desktop apps. I built [a mobile prototype with it](https://www.youtube.com/watch?v=dxqA6RhEwdQ&t=1s) last year.

## What Do I Need to Compare these Docker Images?

First, you need [Docker](https://www.docker.com/get-started). Docker is available on Windows, Mac & Linux.

Then you also need [`container-diff`](https://github.com/GoogleContainerTools/container-diff) , another open-source Docker tool from Google.

## How Do I Compare the Docker Images?

The Docker images are for the application in this repository. It's a [Spring Boot](https://spring.io/projects/spring-boot) web application with a [PostgreSQL database](https://www.postgresql.org), generated with [JHipster](https://www.jhipster.tech). [JHipster](https://www.jhipster.tech) generated it.

- You **don't** need to run the application just to compare the Docker image. But if you do, then please change into the `src/main/docker` directory and run `docker-compose -f app.yml up` there. This will start both the Spring Boot application on port 8080 and a PostgreSQL database. You can log in either as "admin/admin" or "user/user".
- If you just want to see what the application **looks** like, then please look at [my second JHipster tutorial](https://betterprojectsfaster.com/learn/tutorial-jhipster-docker-02#running-the-application). It shows you all screens, especially the built-in admin pages.

I created a [repository for the Docker images of this talk](https://hub.docker.com/r/joedata/bpf-talks-jib-docker). You'll use its Docker images.

### Dockerfile Images

First, let's peek inside a Docker image that I created with a Dockerfile. The [Dockerfile is here](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/etc/docker/Dockerfile). It contains the application as one big JAR file:

```
FROM adoptopenjdk/openjdk11-openj9:x86_64-debianslim-jre-11.0.7_10_openj9-0.20.0

RUN mkdir -p /usr/app
WORKDIR /usr/app
ENV JHIPSTER_SLEEP=0

COPY entrypoint.sh /usr/app/entrypoint.sh
COPY simple-shop-1.0.0.jar /usr/app/simple-shop.jar

ENTRYPOINT ["sh", "-c", "chmod +x /usr/app/entrypoint.sh && cd /usr/app && ./entrypoint.sh"]
```

Let's look inside the image with `docker history`:

```
% docker history -H joedata/bpf-talks-jib-docker:dockerfile-v2
IMAGE               CREATED             CREATED BY                                      SIZE
4ca3cdfa1fce        18 hours ago        /bin/sh -c #(nop)  ENTRYPOINT ["sh" "-c" "ch…   0B
367844815497        18 hours ago        /bin/sh -c #(nop) COPY file:28a9d0466c8a6120…   61.4MB
71f86a3aff85        18 hours ago        /bin/sh -c #(nop) COPY file:e823e990edd2a71d…   1.85kB
b13ab501b022        18 hours ago        /bin/sh -c #(nop)  ENV JHIPSTER_SLEEP=0         0B
8c877ba4d683        18 hours ago        /bin/sh -c #(nop) WORKDIR /usr/app              0B
2d3975c3229c        2 weeks ago         /bin/sh -c mkdir -p /usr/app                    0B
092f9ad82a56        3 weeks ago         /bin/sh -c #(nop)  ENV JAVA_TOOL_OPTIONS=-XX…   0B
<missing>           3 weeks ago         /bin/sh -c #(nop)  ENV JAVA_HOME=/opt/java/o…   0B
<missing>           3 weeks ago         /bin/sh -c set -eux;     ARCH="$(dpkg --prin…   120MB
<missing>           3 weeks ago         /bin/sh -c #(nop)  ENV JAVA_VERSION=jdk-11.0…   0B
<missing>           3 weeks ago         /bin/sh -c apt-get update     && apt-get ins…   35.5MB
<missing>           3 weeks ago         /bin/sh -c #(nop)  ENV LANG=en_US.UTF-8 LANG…   0B
<missing>           4 weeks ago         /bin/sh -c #(nop)  CMD ["bash"]                 0B
<missing>           4 weeks ago         /bin/sh -c #(nop) ADD file:865f9041e12eb341f…   69.2MB
%
```

As [my slides explain](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/Google%20JIB%20for%20Java%20Docker%20Images%20-%20LJC%20Lightning%20Talk%202020.pdf), the Docker image consists of layers:

- Each line is a layer.
- The lower layers are at the bottom, the upper layers at the top.
- The bottom layers all the way to the line that starts with `092f9ad82a56` belong to the base Docker image I'm using: `adoptopenjdk/openjdk11-openj9:x86_64-debianslim-jre-11.0.7_10_openj9-0.20.0`.
- The first layer from my Dockerfile is the line that starts with `2d3975c3229c` &mdash; it's the result of the `RUN mkdir -p /usr/app` command in the Dockerfile. You can see `mkdir -p /usr/app` on the right.
- Every line in my Dockerfile created a layer.
- Each layer prints its size on the right. The biggest layer from my Dockerfile is the JAR file (second layer from top) at 61.4MB &mdash; no surprise here.

As you may be able to tell from the Docker image label `dockerfile-v2`, this image is actually the second version of the application. Compared to the first version of the application, I just [added one log statement to the main class](https://github.com/ksilz/bpf-talks-jib-docker/commit/9c11f5c2d1ca5b1c70fda9b465105f980331bfb6). So how did this **one** change impact the layers of my Docker image?

That's where `container-diff` kicks in. It shows you which files have changed between two versions of a Docker image:

```
% container-diff diff joedata/bpf-talks-jib-docker:dockerfile-v1 joedata/bpf-talks-jib-docker:dockerfile-v2 --type=file

-----File-----

These entries have been added to joedata/bpf-talks-jib-docker:dockerfile-v1: None

These entries have been deleted from joedata/bpf-talks-jib-docker:dockerfile-v1: None

These entries have been changed between joedata/bpf-talks-jib-docker:dockerfile-v1 and joedata/bpf-talks-jib-docker:dockerfile-v2:
FILE                            SIZE1        SIZE2
/usr/app/simple-shop.jar        58.5M        58.5M

%
```

No files were added or deleted. But the last line tells us that the entire JAR file **changed**. So pushing the updated image to a Docker repository means pushing the layer with the entire JAR file!

The file size difference (58.5MB here vs 61.4MB from `docker log`) probably stems from defining 1MB differently: Once as 1000x1000 Bytes, and once as 1024x1024 Bytes.

### Google Jib Images

Now let's look at the Google Jib images. As you may recall from [the slides](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/Google%20JIB%20for%20Java%20Docker%20Images%20-%20LJC%20Lightning%20Talk%202020.pdf), you don't write a Dockerfile with Jib. Instead, you configure Jib, and in turn it generates the Docker image for you.

Here's the [Jib configuration that my Gradle build uses](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/gradle/docker.gradle):

```
jib {
    from {
        image = "adoptopenjdk/openjdk11-openj9:x86_64-debianslim-jre-11.0.7_10_openj9-0.20.0"
    }
    to {
        image = "joedata/bpf-talks-jib-docker:jib-v2"
    }
    container {
        entrypoint = ["bash", "-c", "/entrypoint.sh"]
        ports = ["8080"]
        environment = [
                SPRING_OUTPUT_ANSI_ENABLED: "ALWAYS",
                JHIPSTER_SLEEP            : "0"
        ]
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
    extraDirectories {
        paths = file("src/main/jib")
        permissions = ["/entrypoint.sh": "755"]
    }
}

```

You can find some of the same elements as with my Dockerfile above:

- The base image of `adoptopenjdk/openjdk11-openj9:x86_64-debianslim-jre-11.0.7_10_openj9-0.20.0`
- The environment variable `JHIPSTER_SLEEP`
- The `entrypoint.sh` shell script

[`entrypoint.sh`](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/src/main/jib/entrypoint.sh) is the only file in the [`src/main/jib` directory](https://github.com/ksilz/bpf-talks-jib-docker/tree/master/src/main/jib). It only differs from [its "Dockerfile buddy"](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/etc/docker/entrypoint.sh) in how it launches the Spring Boot application.

So what layers does the Jib Docker image have?

```
% docker history -H joedata/bpf-talks-jib-docker:jib-v2                                                                                                         11:28:34
IMAGE         CREATED BY                                      SIZE      COMMENT
91e62e9190e3  jib-gradle-plugin:2.2.0                         1.92kB    extra files
<missing>     jib-gradle-plugin:2.2.0                         381kB     classes
<missing>     jib-gradle-plugin:2.2.0                         4.34MB    resources
<missing>     jib-gradle-plugin:2.2.0                         59.8MB    dependencies
<missing>     /bin/sh -c #(nop)  ENV JAVA_TOOL_OPTIONS=-XX…   0B
<missing>     /bin/sh -c #(nop)  ENV JAVA_HOME=/opt/java/o…   0B
<missing>     /bin/sh -c set -eux;     ARCH="$(dpkg --prin…   120MB
<missing>     /bin/sh -c #(nop)  ENV JAVA_VERSION=jdk-11.0…   0B
<missing>     /bin/sh -c apt-get update     && apt-get ins…   35.5MB
<missing>     /bin/sh -c #(nop)  ENV LANG=en_US.UTF-8 LANG…   0B
<missing>     /bin/sh -c #(nop)  CMD ["bash"]                 0B
<missing>     /bin/sh -c #(nop) ADD file:9b8be2b52ee0fa31d…   69.2MB
%
```

I removed the `CREATED` column from the output and condensed it so that all fits on a line.

- We have the same layers from the base image. The last base image layer is the last line that has `/bin/sh` in the `CREATED BY` column.
- Jib adds the top four layers. They all have `jib-gradle-plugin:2.2.0` in the `CREATED BY` column.
  - The dependency JAR files are in the first layer. It has `dependencies` as a `COMMENT`.
  - The Angular web application, configuration & data files are in the second layer. It has `resources` as a `COMMENT`.
  - The java classes of the application are in the third layer. It has `classes` as a `COMMENT`.
  - The final layer contains the extra files from the [`src/main/jib` directory](https://github.com/ksilz/bpf-talks-jib-docker/tree/master/src/main/jib). That's just `entrypoint.sh` in our case. It has `extra files` as a `COMMENT`.

So now let's how the very same [one line change in the main class](https://github.com/ksilz/bpf-talks-jib-docker/commit/9c11f5c2d1ca5b1c70fda9b465105f980331bfb6) affected the layers of our Jib image:

```
% container-diff diff joedata/bpf-talks-jib-docker:jib-v1 joedata/bpf-talks-jib-docker:jib-v2 --type=file

-----File-----

These entries have been added to joedata/bpf-talks-jib-docker:jib-v1:
FILE                                                                               SIZE
/app/resources/static/app/0.89604fbecf66a9247c0f.chunk.js                          27.7K
/app/resources/static/app/1.89604fbecf66a9247c0f.chunk.js                          24.9K
/app/resources/static/app/10.89604fbecf66a9247c0f.chunk.js                         803B
/app/resources/static/app/11.89604fbecf66a9247c0f.chunk.js                         6.2K
/app/resources/static/app/12.89604fbecf66a9247c0f.chunk.js                         4.7K
/app/resources/static/app/13.89604fbecf66a9247c0f.chunk.js                         5.1K
/app/resources/static/app/2.89604fbecf66a9247c0f.chunk.js                          25K
/app/resources/static/app/3.89604fbecf66a9247c0f.chunk.js                          28.2K
/app/resources/static/app/4.89604fbecf66a9247c0f.chunk.js                          26.2K
/app/resources/static/app/5.89604fbecf66a9247c0f.chunk.js                          34.1K
/app/resources/static/app/6.89604fbecf66a9247c0f.chunk.js                          8.8K
/app/resources/static/app/7.89604fbecf66a9247c0f.chunk.js                          52K
/app/resources/static/app/8.89604fbecf66a9247c0f.chunk.js                          1022B
/app/resources/static/app/9.89604fbecf66a9247c0f.chunk.js                          5.3K
/app/resources/static/app/global.89604fbecf66a9247c0f.bundle.js                    936B
/app/resources/static/app/main.89604fbecf66a9247c0f.bundle.js                      940.3K
/app/resources/static/app/main.89604fbecf66a9247c0f.bundle.js.LICENSE              987B
/app/resources/static/precache-manifest.702269a1e7e28f09430abd7287a31cfa.js        4.8K

These entries have been deleted from joedata/bpf-talks-jib-docker:jib-v1:
FILE                                                                               SIZE
/app/resources/static/app/0.1df58802763d55ab4242.chunk.js                          27.7K
/app/resources/static/app/1.1df58802763d55ab4242.chunk.js                          24.9K
/app/resources/static/app/10.1df58802763d55ab4242.chunk.js                         803B
/app/resources/static/app/11.1df58802763d55ab4242.chunk.js                         6.2K
/app/resources/static/app/12.1df58802763d55ab4242.chunk.js                         4.7K
/app/resources/static/app/13.1df58802763d55ab4242.chunk.js                         5.1K
/app/resources/static/app/2.1df58802763d55ab4242.chunk.js                          25K
/app/resources/static/app/3.1df58802763d55ab4242.chunk.js                          28.2K
/app/resources/static/app/4.1df58802763d55ab4242.chunk.js                          26.2K
/app/resources/static/app/5.1df58802763d55ab4242.chunk.js                          34.1K
/app/resources/static/app/6.1df58802763d55ab4242.chunk.js                          8.8K
/app/resources/static/app/7.1df58802763d55ab4242.chunk.js                          52K
/app/resources/static/app/8.1df58802763d55ab4242.chunk.js                          1022B
/app/resources/static/app/9.1df58802763d55ab4242.chunk.js                          5.3K
/app/resources/static/app/global.1df58802763d55ab4242.bundle.js                    936B
/app/resources/static/app/main.1df58802763d55ab4242.bundle.js                      940.3K
/app/resources/static/app/main.1df58802763d55ab4242.bundle.js.LICENSE              987B
/app/resources/static/precache-manifest.1421200c222d3bffce057938f0e3396c.js        4.8K

These entries have been changed between joedata/bpf-talks-jib-docker:jib-v1 and joedata/bpf-talks-jib-docker:jib-v2:
FILE                                                                                 SIZE1         SIZE2
/app/resources/stats.html                                                            349.6K        349.6K
/app/resources/static/index.html                                                     6.1K          6.1K
/app/classes/com/betterprojectsfaster/talks/lightning/jib/SimpleShopApp.class        4.1K          4.2K
/entrypoint.sh                                                                       1.4K          1.9K
/app/resources/static/service-worker.js                                              927B          927B
/app/resources/META-INF/build-info.properties                                        168B          168B
/app/resources/git.properties                                                        71B           90B

%
```

So what has changed?

- The Angular web application gets rebuilt with each deployment. It has different file names each time so that web browser caches pick up the new version. Those are the added and deleted files above
- The HTML and Javascript files that changed also belong to Angular. You also see two files with build information there - `build-info.properties` and `git.properties`
- `entrypoint.sh` also changed here by accident. Looks like I didn't have the proper version the first time around! 😔
- Finally, we have `SimpleShopApp.class`, the Java class I did change.

All of the changed files were either in `/app/resources` or `/app/classes`. Hence, the layer for `/app/dependencies` didn't change. So pushing the updated image to a Docker repository means just pushing the last three layers, clocking in around 4.7MB. That's a huge difference to pushing 61.4MB for the "Dockerfile image" above!

## What Do I Need to Build these Docker Images?

You need a Java 8/11 JDK and [Docker](https://www.docker.com/get-started). If you don't have a proper JDK installed, then you [can get one from AdoptOpenJDK](https://adoptopenjdk.net).

This project uses the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html), so you don't need to have Gradle installed.

## How Do I Build the Docker Images?

You can test the differences in Docker images yourself:

- Change either the Java code in [`src/main/java`](https://github.com/ksilz/bpf-talks-jib-docker/tree/master/src/main/java/com/betterprojectsfaster/talks/lightning/jib) or the Angular code in [`src/main/webapp/app`](https://github.com/ksilz/bpf-talks-jib-docker/tree/master/src/main/webapp/app).
- Build the Docker image, either with Jib or a Dockerfile (see the next two sections).
- Compare the new Docker image against the old one.

### With Google Jib

This project uses the [Jib Gradle plugin](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin). Here's how you build your Docker image using a local Docker daemon:

```
./gradlew clean bootJar jibDockerBuild -Pprod
```

The macOS/Linux shell script [`run-build-docker-image-with-jib.sh`](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/run-build-docker-image-with-jib.sh) in the root directory of this project executes that build and then shows you the contents of the docker image with `docker history` afterwards (see above).

If you want to build the Docker image **without** a local Docker daemon, then you first need to configure a Docker repository where you Docker image will be pushed to. Please see the [Jib Gradle documentation](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin) for details. Then can use this command to build the Docker image:

```
./gradlew clean bootJar jib -Pprod --image=<YOUR IMAGE NAME AND TAG>
```

### With a Dockerfile

Build your Docker image with a Dockerfile requires three steps:

- Creating the application as a JAR file with `./gradlew clean bootJar -Pprod`,
- Copying the JAR file into the directory where the Dockerfile resides, and
- Building the Docker image with `docker build -t <YOUR IMAGE NAME AND TAG> .`

The macOS/Linux shell script [`run-build-docker-image-with-dockerfile.sh`](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/run-build-docker-image-with-dockerfile.sh) in the root directory of this project performs these steps and then shows you the contents of the docker image with `docker history` afterwards (see above).

## How Can I Use Google Jib in My Own Project?

Google Jib has its own documentation on how to use it with [Maven](https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin) and [Gradle](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin). Please check it out.

This project uses the Jib Gradle plugin, as generated by [JHipster](https://www.jhipster.tech). The Gradle integration is as follows:

- The Jib plugin is loaded in [the `plugin` section of `build.gradle`](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/build.gradle#L13). I changed the version number to 2.2.0, the latest version available as of May 15, 2020:

```
plugins {
[...]
    id "com.google.cloud.tools.jib" version "2.2.0"
[...]
}

```

- The separated Jib Gradle build file `docker.gradle` [is loaded in `build.gradle`](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/build.gradle#L39):

```
apply from: "gradle/docker.gradle"
```

- [`docker.gradle`](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/gradle/docker.gradle) configures Jib:

```
jib {
    from {
        image = "adoptopenjdk/openjdk11-openj9:x86_64-debianslim-jre-11.0.7_10_openj9-0.20.0"
    }
    to {
        image = "joedata/bpf-talks-jib-docker:jib-v2"
    }
    container {
        entrypoint = ["bash", "-c", "/entrypoint.sh"]
        ports = ["8080"]
        environment = [
                SPRING_OUTPUT_ANSI_ENABLED: "ALWAYS",
                JHIPSTER_SLEEP            : "0"
        ]
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
    extraDirectories {
        paths = file("src/main/jib")
        permissions = ["/entrypoint.sh": "755"]
    }
}
```

- The files in [`src/main/jib`](https://github.com/ksilz/bpf-talks-jib-docker/tree/master/src/main/jib) are automatically part of the Docker image. Here, it's just the [shell script that starts the application](https://github.com/ksilz/bpf-talks-jib-docker/blob/master/src/main/jib/entrypoint.sh).
