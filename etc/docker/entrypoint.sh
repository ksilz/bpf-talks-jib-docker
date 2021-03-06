#!/bin/sh

echo " "
echo "***************************************************************"
echo "*                                                             *"
echo "*              A betterprojectsfaster.com Talk                *"
echo "*              -------------------------------                *"
echo "*                                                             *"
echo "*         Google Jib: Smaller & Faster Docker Images          *"
echo "*               for Java Applications                         *"
echo "*        https://github.com/ksilz/bpf-talks-jib-docker        *"
echo "*                                                             *"
echo "***************************************************************"
echo " "
echo "This container is part of a presentation about Google Jib, a"
echo "build tool for Java Docker Images. Please see here for details:"
echo "https://github.com/ksilz/bpf-talks-jib-docker"
echo " "
echo "This container runs a Spring Boot - Angular web application."
echo "JHipster (https://www.jhipster.tech) generated it."
echo " "
echo "***************************************************************"
echo " "
echo "This Docker container image was produced by a DOCKERFILE!"
echo " "
echo " "
echo "***************************************************************"
echo " "
echo "This container uses this Java version:"
java -version
echo " "
echo "This container uses these Java options:"
echo "${JAVA_OPTS}"
echo " "
echo "The application will start in ${JHIPSTER_SLEEP}s..."
sleep ${JHIPSTER_SLEEP}
echo " "
echo " "
echo "***************************************************************"
echo " "
echo "Running web application..."
java ${JAVA_OPTS} -jar /usr/app/simple-shop.jar
echo " "
echo " "
echo "***************************************************************"
echo " "
echo " Done. "
echo " "
