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

The Docker images are for the application in this repository. It's a [Spring Boot](https://spring.io/projects/spring-boot) web application with a [PostgreSQL database](https://www.postgresql.org), generated with [JHipster](https://www.jhipster.tech).

- You **don't** need to run the application just to compare the Docker image. But if you do, then please change into the `src/main/docker` directory and run `docker-compose -f app.yml up` there. This will start both the Spring Boot application on port 8080 and a PostgreSQL database. You can log in either as "admin/admin" or "user/user".
- If you just want to see what the application **looks** like, then please look at [my second JHipster tutorial](https://betterprojectsfaster.com/learn/tutorial-jhipster-docker-02#running-the-application) . It shows you all screens, especially the built-in admin pages.

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

Let's look inside the image:

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

As my slides explain, the Docker image consists of layers:

- Each line is a layer
- The lower layers are at the bottom, the upper layers at the top
- The bottom layers all the way to the line that starts with `092f9ad82a56` belong to the base Docker image I'm using: `adoptopenjdk/openjdk11-openj9:x86_64-debianslim-jre-11.0.7_10_openj9-0.20.0`
- The first layer from my Dockerfile is the line that starts with `2d3975c3229c` &mdash; it's the `RUN mkdir -p /usr/app` command. You can see `mkdir -p /usr/app` on the right
- Every line in my Dockerfile created a layer.
- Each layer prints its size on the right. The biggest layer from my Dockerfile is the JAR file (second layer from top) at 61.4MB &mdash; no surprise here.

As you may be able to tell from the Docker image label `dockerfile-v2`, this image is actually the second version of the application. Compared to the first version of the application, I just [added one log statement to the main class](https://github.com/ksilz/bpf-talks-jib-docker/commit/9c11f5c2d1ca5b1c70fda9b465105f980331bfb6). So how did this one change impact the layers of my Docker image?

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

The last line tells us that the entire JAR file changed. The file size difference (58.5MB here vs 61.4MB from `docker log`) probably stems from defining 1MB differently: Once as 1000x1000 Bytes, and once as 1024x1024 Bytes.

### Google Jib Images

Now let's look at the Google Jib images. As you may recall from the slides, you don't write a Dockerfile with Jib. Instead, you configure Jib, and in turn it generates the Docker image for you.

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
  - The Angular web application, configuration & data files re in the second layer. It has `resources` as a `COMMENT`.
  - The java classes of the application are in the third layer. It has `classes` as a `COMMENT`.
  - The final layer contains the extra files from the [`src/main/jib` directory](https://github.com/ksilz/bpf-talks-jib-docker/tree/master/src/main/jib). That's just `entrypoint.sh` in our case. It has `extra files` as a `COMMENT`.

## What Do I Need to Build these Docker Images?

\__coming soon_

## How Do I Build the Docker Iamges?

\__coming soon_

## How Can I Use Google Jib in my Own Project?

\__coming soon_
