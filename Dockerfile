FROM adoptopenjdk/openjdk16:alpine AS build
WORKDIR workspace

ARG VERSION=no-version
ENV VERSION=${VERSION}

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
RUN ./gradlew build --refresh-dependencies
COPY src src

RUN ./gradlew installBootDist
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

FROM adoptopenjdk/openjdk16:alpine

RUN apk add -U tzdata \
    && cp /usr/share/zoneinfo/Europe/Moscow /etc/localtime \
    && echo "Europe/Moscow" >  /etc/timezone

COPY --from=build workspace/dependencies/ .
COPY --from=build workspace/snapshot-dependencies/ .
COPY --from=build workspace/spring-boot-loader/ .
COPY --from=build workspace/application/ .

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

