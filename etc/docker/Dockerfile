FROM adoptopenjdk/openjdk11-openj9:x86_64-debianslim-jre-11.0.7_10_openj9-0.20.0

RUN mkdir -p /usr/app
WORKDIR /usr/app

COPY entrypoint.sh /usr/app/entrypoint.sh
COPY simple-shop-1.0.0.jar /usr/app/simple-shop.jar

ENTRYPOINT ["sh", "-c", "chmod +x /usr/app/entrypoint.sh && cd /usr/app && ./entrypoint.sh"]
