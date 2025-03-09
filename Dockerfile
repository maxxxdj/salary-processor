FROM eclipse-temurin:21.0.5_11-jre-alpine

MAINTAINER martin.miloshev

RUN apk add --no-cache libstdc++

ADD build/libs/*.jar app.jar

EXPOSE 80

ENV JAVA_TOOL_OPTIONS="-XX:MinRAMPercentage=50 -XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-Dspring.main.banner-mode=off", "-jar", "/app.jar"]
