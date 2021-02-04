FROM gradle:6.8-jdk15 AS build
WORKDIR /workspace

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
RUN gradle build --refresh-dependencies
COPY src src

RUN gradle installDist

FROM adoptopenjdk/openjdk15:alpine
VOLUME /tmp

COPY --from=build /workspace/build/install/homealone /app

ENTRYPOINT ["/app/bin/homealone"]

