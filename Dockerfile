FROM adoptopenjdk/openjdk15:alpine AS build
WORKDIR workspace

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .
RUN ./gradlew build --refresh-dependencies
COPY src src

RUN ./gradlew installBootDist
RUN java -Djarmode=layertools -jar build/libs/*.jar extract

FROM adoptopenjdk/openjdk15:alpine

COPY --from=build workspace/dependencies/ .
RUN true # https://github.com/moby/moby/issues/37965
COPY --from=build workspace/snapshot-dependencies/ .
RUN true # https://github.com/moby/moby/issues/37965
COPY --from=build workspace/spring-boot-loader/ .
RUN true # https://github.com/moby/moby/issues/37965
COPY --from=build workspace/application/ .

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]

