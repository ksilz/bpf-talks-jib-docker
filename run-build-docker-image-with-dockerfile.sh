#!/bin/sh

echo " "
echo "***************************************************************"
echo "*                                                             *"
echo "*               A betterprojectsfaster.com Talk               *"
echo "*               -------------------------------               *"
echo "*                                                             *"
echo "*          Google Jib: Smaller & Faster Docker Images         *"
echo "*                    for Java Applications                    *"
echo "*        https://github.com/ksilz/bpf-talks-jib-docker        *"
echo "*                                                             *"
echo "***************************************************************"
echo " "
echo "This build script is part of a presentation about Google Jib, a"
echo "build tool for Java Docker Images. Please see here for details:"
echo "https://github.com/ksilz/bpf-talks-jib-docker"
echo " "
echo "You need to have Docker installed to run this script!"
echo " "
echo "***************************************************************"
echo " "
echo "I'm building the Docker Image with a DOCKERFILE now!"
echo " "

./gradlew clean bootJar -Pprod
cp ./etc/docker/* build/libs/
cd ./build/libs
docker build -t joedata/bpf-talks-jib-docker:dockerfile-v2 .
cd ../..

echo " "
echo " "
echo "***************************************************************"
echo " "
echo "Here are the layers of the Docker Image I just built:"

docker history -H joedata/bpf-talks-jib-docker:dockerfile-v2

echo " "
echo " "
echo "***************************************************************"
echo " "
echo "Done."
echo " "
